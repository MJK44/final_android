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

class DogRepository {
    private val dogsLiveData = MutableLiveData<List<Animal>>()
    private var fetchedDogCount = 0
    private var lastToken: String = "" // Store the last fetched token
    val favoritesLiveData = MutableLiveData<List<Animal>>()

    private val db = FirebaseFirestore.getInstance()
    private val favoritesCollection = db.collection("favorites")

    // Fetch dogs from the API
    fun fetchDogsFromApi(clientId: String, clientSecret: String) {
        val oAuthService = RetrofitClient.instance.create(OAuthService::class.java)
        oAuthService.getAccessToken(clientId = clientId, clientSecret = clientSecret)
            .enqueue(object : Callback<OAuthTokenResponse> {
                override fun onResponse(call: Call<OAuthTokenResponse>, response: Response<OAuthTokenResponse>) {
                    if (response.isSuccessful) {
                        lastToken = response.body()?.access_token ?: return
                        fetchDogs(lastToken) // Fetch dogs specifically
                    } else {
                        Log.e("DogRepository", "Failed to fetch token: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<OAuthTokenResponse>, t: Throwable) {
                    Log.e("DogRepository", "Failed to get access token: ${t.message}")
                }
            })
    }

    // Fetch all dogs from the API
    private fun fetchDogs(token: String) {
        val petApiService = RetrofitClient.instance.create(PetApiService::class.java)
        petApiService.listPets(authHeader = "Bearer $token", type = "dog", limit = 100) // Fetching only dogs
            .enqueue(object : Callback<PetResponse> {
                override fun onResponse(call: Call<PetResponse>, response: Response<PetResponse>) {
                    if (response.isSuccessful) {
                        val dogs = response.body()?.animals ?: emptyList()
                        fetchedDogCount += dogs.size
                        saveDogsToFirebase(dogs)
                        updateLiveData(dogs)  // Update LiveData with the fetched dogs
                    } else {
                        Log.e("DogRepository", "Failed to fetch dogs: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                    Log.e("DogRepository", "Failed to fetch dogs: ${t.message}")
                }
            })
    }

    // Save dogs to Firebase
    private fun saveDogsToFirebase(dogs: List<Animal>) {
        val firestore = FirebaseFirestore.getInstance()
        dogs.forEach { dog ->
            val dogMap = hashMapOf(
                "name" to dog.name,
                "breed" to dog.breeds.primary,
                "type" to dog.type,
                "age" to dog.age,
                "description" to dog.description,
                "location" to dog.location,
                "imageUrl" to (dog.photos?.firstOrNull()?.medium ?: "default_image_url")
            )

            firestore.collection("dogs").add(dogMap)
                .addOnSuccessListener { documentReference ->
                    Log.d("DogRepository", "Dog saved with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("DogRepository", "Error adding dog: ", e)
                }
        }
    }

    fun toggleFavorite(animal: Animal, userId: String) {
        val favoriteDoc = favoritesCollection.document(userId).collection("userFavorites").document(animal.id.toString())
        val isNowFavorited = !animal.isFavorited
        animal.isFavorited = isNowFavorited
        if (isNowFavorited) {
            val data = mapOf("favorited" to true)
            favoriteDoc.set(data)
        } else {
            favoriteDoc.delete()
        }
    }

    fun loadFavorites(userId: String) {
        favoritesCollection.document(userId).collection("userFavorites")
            .get()
            .addOnSuccessListener { documents ->
                val favoriteIds = documents.mapNotNull { it.id.toIntOrNull() }
                val favorites = dogsLiveData.value?.filter { it.id in favoriteIds }
                favoritesLiveData.postValue(favorites ?: emptyList())
            }
    }

    // Update LiveData with the list of dogs
    private fun updateLiveData(dogs: List<Animal>) {
        dogsLiveData.postValue(dogs)
    }

    // Get the LiveData to observe in the ViewModel
    fun getDogsLiveData(): LiveData<List<Animal>> = dogsLiveData

    fun getFavoritesLiveData(): LiveData<List<Animal>> = favoritesLiveData

    //make the search bar functional, to fetch only dogs (by name)
    fun searchDogsByName(name: String) {
        val currentDogs = dogsLiveData.value
        if (currentDogs != null) {
            val filteredDogs = currentDogs.filter { it.name.contains(name, ignoreCase = true) }
            dogsLiveData.postValue(filteredDogs)
        } else {
            dogsLiveData.postValue(emptyList())
        }
    }
}