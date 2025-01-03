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

class HorseRepository {
    private val horsesLiveData = MutableLiveData<List<Animal>>()
    private var fetchedHorseCount = 0
    private var lastToken: String = "" // Store the last fetched token

    private val favoritesLiveData = MutableLiveData<List<Animal>>()
    private val db = FirebaseFirestore.getInstance()
    private val favoritesCollection = db.collection("favorites")

    // Fetch horses from the API
    fun fetchHorsesFromApi(clientId: String, clientSecret: String) {
        val oAuthService = RetrofitClient.instance.create(OAuthService::class.java)
        oAuthService.getAccessToken(clientId = clientId, clientSecret = clientSecret)
            .enqueue(object : Callback<OAuthTokenResponse> {
                override fun onResponse(call: Call<OAuthTokenResponse>, response: Response<OAuthTokenResponse>) {
                    if (response.isSuccessful) {
                        lastToken = response.body()?.access_token ?: return
                        fetchHorses(lastToken) // Fetch horses specifically
                    } else {
                        Log.e("HorseRepository", "Failed to fetch token: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<OAuthTokenResponse>, t: Throwable) {
                    Log.e("HorseRepository", "Failed to get access token: ${t.message}")
                }
            })
    }

    // Fetch all horses from the API
    private fun fetchHorses(token: String) {
        val petApiService = RetrofitClient.instance.create(PetApiService::class.java)
        petApiService.listPets(authHeader = "Bearer $token", type = "horse", limit = 100) // Fetching only horses
            .enqueue(object : Callback<PetResponse> {
                override fun onResponse(call: Call<PetResponse>, response: Response<PetResponse>) {
                    if (response.isSuccessful) {
                        val horses = response.body()?.animals ?: emptyList()
                        fetchedHorseCount += horses.size
                        saveHorsesToFirebase(horses)
                        updateLiveData(horses)  // Update LiveData with the fetched horses
                    } else {
                        Log.e("HorseRepository", "Failed to fetch horses: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                    Log.e("HorseRepository", "Failed to fetch horses: ${t.message}")
                }
            })
    }

    // Save horses to Firebase
    private fun saveHorsesToFirebase(horses: List<Animal>) {
        val firestore = FirebaseFirestore.getInstance()
        horses.forEach { horse ->
            val horseMap = hashMapOf(
                "name" to horse.name,
                "breed" to horse.breeds.primary,
                "type" to horse.type,
                "age" to horse.age,
                "description" to horse.description,
                "location" to horse.location,
                "imageUrl" to (horse.photos?.firstOrNull()?.medium ?: "default_image_url")
            )

            firestore.collection("horses").add(horseMap)
                .addOnSuccessListener { documentReference ->
                    Log.d("HorseRepository", "Horse saved with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("HorseRepository", "Error adding horse: ", e)
                }
        }
    }

    fun toggleFavorite(animal: Animal, userId: String) {
        // Each user has a sub-collection "userFavorites" under the "favorites" collection
        val favoriteDoc = favoritesCollection.document(userId)
            .collection("userFavorites")
            .document(animal.id.toString())

        animal.isFavorited = !animal.isFavorited
        if (animal.isFavorited) {
            val data = mapOf("favorited" to true)
            favoriteDoc.set(data)
        } else {
            favoriteDoc.delete()
        }
    }

    fun loadFavorites(userId: String) {
        favoritesCollection.document(userId)
            .collection("userFavorites")
            .get()
            .addOnSuccessListener { documents ->
                val favoriteIds = documents.mapNotNull { it.id.toIntOrNull() }
                val favorites = horsesLiveData.value?.filter { it.id in favoriteIds }
                favoritesLiveData.postValue(favorites ?: emptyList())
            }
            .addOnFailureListener {
                Log.e("HorseRepository", "Failed to load favorites.")
            }
    }

    // Update LiveData with the list of horses
    private fun updateLiveData(horses: List<Animal>) {
        horsesLiveData.postValue(horses)
    }

    // Get the LiveData to observe in the ViewModel
    fun getHorsesLiveData(): LiveData<List<Animal>> = horsesLiveData

    fun getFavoritesLiveData(): LiveData<List<Animal>> = favoritesLiveData

    fun searchHorsesByName(name: String) {
        val currentHorses = horsesLiveData.value
        if (currentHorses != null) {
            val filteredHorses = currentHorses.filter { it.name.contains(name, ignoreCase = true) }
            horsesLiveData.postValue(filteredHorses)
        } else {
            horsesLiveData.postValue(emptyList())
        }
    }
}
