package repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import model.Animal
import network.OAuthService
import network.PetApiService
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import model.PetResponse
import network.OAuthTokenResponse

class CatRepository {
    private val catsLiveData = MutableLiveData<List<Animal>>()
    private var fetchedCatCount = 0
    private var lastToken: String = "" // Store the last fetched token
    private val favoritesLiveData = MutableLiveData<List<Animal>>()

    private val db = FirebaseFirestore.getInstance()
    private val favoritesCollection = db.collection("favorites")



    // Fetch cats from the API
    fun fetchCatsFromApi(clientId: String, clientSecret: String) {
        val oAuthService = RetrofitClient.instance.create(OAuthService::class.java)
        oAuthService.getAccessToken(clientId = clientId, clientSecret = clientSecret)
            .enqueue(object : Callback<OAuthTokenResponse> {
                override fun onResponse(call: Call<OAuthTokenResponse>, response: Response<OAuthTokenResponse>) {
                    if (response.isSuccessful) {
                        lastToken = response.body()?.access_token ?: return
                        fetchCats(lastToken) // Fetch cats specifically
                    } else {
                        Log.e("CatRepository", "Failed to fetch token: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<OAuthTokenResponse>, t: Throwable) {
                    Log.e("CatRepository", "Failed to get access token: ${t.message}")
                }
            })
    }

    // Fetch all cats from the API
    private fun fetchCats(token: String) {
        val petApiService = RetrofitClient.instance.create(PetApiService::class.java)
        petApiService.listPets(authHeader = "Bearer $token", type = "cat", limit = 100) // Fetching only cats
            .enqueue(object : Callback<PetResponse> {
                override fun onResponse(call: Call<PetResponse>, response: Response<PetResponse>) {
                    if (response.isSuccessful) {
                        val cats = response.body()?.animals ?: emptyList()
                        fetchedCatCount += cats.size
                        saveCatsToFirebase(cats)
                        updateLiveData(cats)  // Update LiveData with the fetched cats
                    } else {
                        Log.e("CatRepository", "Failed to fetch cats: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                    Log.e("CatRepository", "Failed to fetch cats: ${t.message}")
                }
            })
    }

    // Save cats to Firebase
    private fun saveCatsToFirebase(cats: List<Animal>) {
        val firestore = FirebaseFirestore.getInstance()
        cats.forEach { cat ->
            val catMap = hashMapOf(
                "name" to cat.name,
                "breed" to cat.breeds.primary,
                "type" to cat.type,
                "age" to cat.age,
                "description" to cat.description,
                "location" to cat.location,
                "imageUrl" to (cat.photos?.firstOrNull()?.medium ?: "default_image_url")
            )

            firestore.collection("cats").add(catMap)
                .addOnSuccessListener { documentReference ->
                    Log.d("CatRepository", "Cat saved with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("CatRepository", "Error adding cat: ", e)
                }
        }
    }

    fun toggleFavorite(animal: Animal, userId: String) {
        val favoriteDoc = favoritesCollection.document(userId).collection("userFavorites").document(animal.id.toString())
        animal.isFavorited = !animal.isFavorited
        if (animal.isFavorited) {
            val data = mapOf("favorited" to true)
            favoriteDoc.set(data)
        } else {
            favoriteDoc.delete()
        }
    }

    // Load the favorite status for cats
    fun loadFavorites(userId: String) {
        favoritesCollection.document(userId).collection("userFavorites")
            .get()
            .addOnSuccessListener { documents ->
                val favoriteIds = documents.mapNotNull { it.id.toIntOrNull() }
                val favorites = catsLiveData.value?.filter { it.id in favoriteIds }
                favoritesLiveData.postValue(favorites ?: emptyList())
            }
    }

    // Update LiveData with the list of cats
    private fun updateLiveData(cats: List<Animal>) {
        catsLiveData.postValue(cats)
    }

    // Get the LiveData to observe in the ViewModel
    fun getCatsLiveData(): LiveData<List<Animal>> = catsLiveData

    fun getFavoritesLiveData(): LiveData<List<Animal>> = favoritesLiveData

    fun searchCatsByName(name: String) {
        val currentCats = catsLiveData.value
        if (currentCats != null) {
            val filteredCats = currentCats.filter { it.name.contains(name, ignoreCase = true) }
            catsLiveData.postValue(filteredCats)
        } else {
            catsLiveData.postValue(emptyList())
        }
    }

}