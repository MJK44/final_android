package viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import repository.DogRepository
import model.Animal

class DogViewModel : ViewModel() {
    private val dogRepository = DogRepository()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Fetch dogs from API using client credentials
    fun fetchDogsFromApi(clientId: String, clientSecret: String) {
        dogRepository.fetchDogsFromApi(clientId, clientSecret)
    }

    // Get the LiveData for dogs
    fun getDogsLiveData(): LiveData<List<Animal>> = dogRepository.getDogsLiveData()

    fun getFavoritesLiveData(): LiveData<List<Animal>> = dogRepository.favoritesLiveData

    fun toggleFavorite(dog: Animal) {
        val userId = auth.currentUser?.uid ?: return  // Ensure we have a user ID
        dogRepository.toggleFavorite(dog, userId)
        refreshFavorites()  // Refresh favorites to reflect changes
    }

    private fun refreshFavorites() {
        val userId = auth.currentUser?.uid ?: return
        dogRepository.loadFavorites(userId)
    }

    fun searchDogsByName(name: String) {
        dogRepository.searchDogsByName(name)
    }
}