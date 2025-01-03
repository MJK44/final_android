package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import repository.RabbitRepository
import model.Animal

class RabbitViewModel : ViewModel() {
    private val rabbitRepository = RabbitRepository()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun fetchRabbitsFromApi(clientId: String, clientSecret: String) {
        rabbitRepository.fetchRabbitsFromApi(clientId, clientSecret)
    }

    fun getRabbitsLiveData(): LiveData<List<Animal>> = rabbitRepository.getRabbitsLiveData()

    // Observe the user’s favorite rabbits
    fun getFavoritesLiveData(): LiveData<List<Animal>> = rabbitRepository.getFavoritesLiveData()

    // Toggle favorite status for a rabbit
    fun toggleFavorite(rabbit: Animal) {
        val userId = auth.currentUser?.uid ?: return  // Ensure we have a valid user ID
        rabbitRepository.toggleFavorite(rabbit, userId)
        refreshFavorites()  // Refresh favorites to reflect the latest changes
    }

    // Refresh the list of favorite rabbits
    private fun refreshFavorites() {
        val userId = auth.currentUser?.uid ?: return
        rabbitRepository.loadFavorites(userId)
    }

    fun searchRabbitsByName(name: String) {
        rabbitRepository.searchRabbitsByName(name)
    }
}