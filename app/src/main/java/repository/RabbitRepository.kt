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

class RabbitRepository {
    private val rabbitsLiveData = MutableLiveData<List<Animal>>()
    private var fetchedRabbitCount = 0
    private var lastToken: String = ""

    private val favoritesLiveData = MutableLiveData<List<Animal>>()
    private val db = FirebaseFirestore.getInstance()
    private val favoritesCollection = db.collection("favorites")

    fun fetchRabbitsFromApi(clientId: String, clientSecret: String) {
        val oAuthService = RetrofitClient.instance.create(OAuthService::class.java)
        oAuthService.getAccessToken(clientId = clientId, clientSecret = clientSecret)
            .enqueue(object : Callback<OAuthTokenResponse> {
                override fun onResponse(call: Call<OAuthTokenResponse>, response: Response<OAuthTokenResponse>) {
                    if (response.isSuccessful) {
                        lastToken = response.body()?.access_token ?: return
                        fetchRabbits(lastToken)
                    } else {
                        Log.e("RabbitRepository", "Failed to fetch token: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<OAuthTokenResponse>, t: Throwable) {
                    Log.e("RabbitRepository", "Failed to get access token: ${t.message}")
                }
            })
    }

    private fun fetchRabbits(token: String) {
        val petApiService = RetrofitClient.instance.create(PetApiService::class.java)
        petApiService.listPets(authHeader = "Bearer $token", type = "rabbit", limit = 100)
            .enqueue(object : Callback<PetResponse> {
                override fun onResponse(call: Call<PetResponse>, response: Response<PetResponse>) {
                    if (response.isSuccessful) {
                        val rabbits = response.body()?.animals ?: emptyList()
                        fetchedRabbitCount += rabbits.size
                        saveRabbitsToFirebase(rabbits)
                        updateLiveData(rabbits)
                    } else {
                        Log.e("RabbitRepository", "Failed to fetch rabbits: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                    Log.e("RabbitRepository", "Failed to fetch rabbits: ${t.message}")
                }
            })
    }

    private fun saveRabbitsToFirebase(rabbits: List<Animal>) {
        val firestore = FirebaseFirestore.getInstance()
        rabbits.forEach { rabbit ->
            val rabbitMap = hashMapOf(
                "name" to rabbit.name,
                "breed" to rabbit.breeds.primary,
                "type" to rabbit.type,
                "age" to rabbit.age,
                "description" to rabbit.description,
                "location" to rabbit.location,
                "imageUrl" to (rabbit.photos?.firstOrNull()?.medium ?: "default_image_url")
            )
            firestore.collection("rabbits").add(rabbitMap)
                .addOnSuccessListener { documentReference ->
                    Log.d("RabbitRepository", "Rabbit saved with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("RabbitRepository", "Error adding rabbit: ", e)
                }
        }
    }

    // Toggle favorite status
    fun toggleFavorite(animal: Animal, userId: String) {
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

    // Load user favorites from Firestore
    fun loadFavorites(userId: String) {
        favoritesCollection.document(userId)
            .collection("userFavorites")
            .get()
            .addOnSuccessListener { documents ->
                val favoriteIds = documents.mapNotNull { it.id.toIntOrNull() }
                val favorites = rabbitsLiveData.value?.filter { it.id in favoriteIds }
                favoritesLiveData.postValue(favorites ?: emptyList())
            }
            .addOnFailureListener {
                Log.e("RabbitRepository", "Failed to load favorites.")
            }
    }

    // Update LiveData with the list of rabbits
    private fun updateLiveData(rabbits: List<Animal>) {
        rabbitsLiveData.postValue(rabbits)
    }

    fun getRabbitsLiveData(): LiveData<List<Animal>> = rabbitsLiveData

    fun getFavoritesLiveData(): LiveData<List<Animal>> = favoritesLiveData

    fun searchRabbitsByName(name: String) {
        val currentRabbits = rabbitsLiveData.value
        if (currentRabbits != null) {
            val filteredRabbits = currentRabbits.filter { it.name.contains(name, ignoreCase = true) }
            rabbitsLiveData.postValue(filteredRabbits)
        } else {
            rabbitsLiveData.postValue(emptyList())
        }
    }
}
