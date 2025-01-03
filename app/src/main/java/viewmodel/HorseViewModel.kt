package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import repository.HorseRepository
import model.Animal

class HorseViewModel : ViewModel() {
    private val horseRepository = HorseRepository()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun fetchHorsesFromApi(clientId: String, clientSecret: String) {
        horseRepository.fetchHorsesFromApi(clientId, clientSecret)
    }

    fun getHorsesLiveData(): LiveData<List<Animal>> = horseRepository.getHorsesLiveData()

    // Observe the user’s favorite horses
    fun getFavoritesLiveData(): LiveData<List<Animal>> = horseRepository.getFavoritesLiveData()

    // Toggle favorite status for a horse
    fun toggleFavorite(horse: Animal) {
        val userId = auth.currentUser?.uid ?: return  // Ensure we have a user ID
        horseRepository.toggleFavorite(horse, userId)
        refreshFavorites()  // Refresh favorites to reflect changes
    }

    // Refresh the list of favorite horses from the repository
    private fun refreshFavorites() {
        val userId = auth.currentUser?.uid ?: return
        horseRepository.loadFavorites(userId)
    }

    fun searchHorsesByName(name: String) {
        horseRepository.searchHorsesByName(name)
    }
}