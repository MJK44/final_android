
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

class PetRepository {
    private val petsLiveData = MutableLiveData<List<Animal>>()
    private var fetchedPetCount = 0
    private var lastToken: String = "" // Store the last fetched token

    private val favoritesLiveData = MutableLiveData<List<Animal>>()
    private val db = FirebaseFirestore.getInstance()
    private val favoritesCollection = db.collection("favorites")

    // Fetch pets from the API
    fun fetchPetsFromApi(clientId: String, clientSecret: String) {
        val oAuthService = RetrofitClient.instance.create(OAuthService::class.java)
        oAuthService.getAccessToken(clientId = clientId, clientSecret = clientSecret)
            .enqueue(object : Callback<OAuthTokenResponse> {
                override fun onResponse(call: Call<OAuthTokenResponse>, response: Response<OAuthTokenResponse>) {
                    if (response.isSuccessful) {
                        lastToken = response.body()?.access_token ?: return
                        fetchPets(lastToken) // Fetch all pets without random selection
                    } else {
                        Log.e("PetRepository", "Failed to fetch token: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<OAuthTokenResponse>, t: Throwable) {
                    Log.e("PetRepository", "Failed to get access token: ${t.message}")
                }
            })
    }

    // Fetch all pets (no random selection)
    private fun fetchPets(token: String) {
        val petApiService = RetrofitClient.instance.create(PetApiService::class.java)
        petApiService.listPets(authHeader = "Bearer $token", type = "", limit = 100) // Fetching all pets, no limit on type
            .enqueue(object : Callback<PetResponse> {
                override fun onResponse(call: Call<PetResponse>, response: Response<PetResponse>) {
                    if (response.isSuccessful) {
                        val pets = response.body()?.animals ?: emptyList()
                        fetchedPetCount += pets.size
                        savePetsToFirebase(pets)  // Save fetched pets to Firebase if needed
                        updateLiveData(pets)  // Update LiveData with the fetched pets
                    } else {
                        Log.e("PetRepository", "Failed to fetch pets: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                    Log.e("PetRepository", "Failed to fetch pets: ${t.message}")
                }
            })
    }

    // Save pets to Firebase (if needed)
    private fun savePetsToFirebase(pets: List<Animal>) {
        val firestore = FirebaseFirestore.getInstance()
        pets.forEach { pet ->
            val petMap = hashMapOf(
                "name" to pet.name,
                "breed" to pet.breeds.primary,
                "type" to pet.type,
                "age" to pet.age,
                "description" to pet.description,
                "location" to pet.location,
                "imageUrl" to (pet.photos?.firstOrNull()?.medium ?: "default_image_url")
            )

            firestore.collection("pets").add(petMap)
                .addOnSuccessListener { documentReference ->
                    Log.d("PetRepository", "Pet saved with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("PetRepository", "Error adding pet: ", e)
                }
        }
    }

    fun toggleFavorite(animal: Animal, userId: String) {
        // Each user has a sub-collection "userFavorites" under the "favorites" collection
        val favoriteDoc = favoritesCollection.document(userId)
            .collection("userFavorites")
            .document(animal.id.toString())

        // Flip the favorited state and update in Firestore
        animal.isFavorited = !animal.isFavorited
        if (animal.isFavorited) {
            val data = mapOf("favorited" to true)
            favoriteDoc.set(data)
        } else {
            favoriteDoc.delete()
        }
    }

    // Load all favorite pets for a specific user
    fun loadFavorites(userId: String) {
        favoritesCollection.document(userId)
            .collection("userFavorites")
            .get()
            .addOnSuccessListener { documents ->
                val favoriteIds = documents.mapNotNull { it.id.toIntOrNull() }
                val favorites = petsLiveData.value?.filter { it.id in favoriteIds }
                favoritesLiveData.postValue(favorites ?: emptyList())
            }
            .addOnFailureListener {
                Log.e("PetRepository", "Failed to load favorites.")
            }
    }

    // Update LiveData with the list of pets
    fun updateLiveData(pets: List<Animal>) {
        petsLiveData.postValue(pets)
    }

    // Get the LiveData to observe in the ViewModel
    fun getPetsLiveData(): LiveData<List<Animal>> = petsLiveData

    fun getFavoritesLiveData(): LiveData<List<Animal>> = favoritesLiveData

    // Provide the token if needed
    fun getToken(): String {
        return lastToken // Provide the token for other methods
    }

    //function to make the search bar functional
    fun searchPetsByName(name: String) {
        val currentPets = petsLiveData.value
        if (currentPets != null) {
            val filteredPets = currentPets.filter { it.name.contains(name, ignoreCase = true) }
            petsLiveData.postValue(filteredPets)
        } else {
            petsLiveData.postValue(emptyList()) // Provide an empty list if no pets are available
        }
    }
}
