package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import repository.CatRepository
import model.Animal

class CatViewModel : ViewModel() {
    private val catRepository = CatRepository()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun fetchCatsFromApi(clientId: String, clientSecret: String) {
        catRepository.fetchCatsFromApi(clientId, clientSecret)
    }

    fun getCatsLiveData(): LiveData<List<Animal>> = catRepository.getCatsLiveData()

    // Get the LiveData for favorites
    fun getFavoritesLiveData(): LiveData<List<Animal>> = catRepository.getFavoritesLiveData()

    // Toggle favorite status for a cat
    fun toggleFavorite(cat: Animal) {
        val userId = auth.currentUser?.uid ?: return  // Ensure we have a user ID
        catRepository.toggleFavorite(cat, userId)
        refreshFavorites()  // Refresh favorites to reflect changes
    }

    // Refresh the list of favorite cats from the repository
    private fun refreshFavorites() {
        val userId = auth.currentUser?.uid ?: return
        catRepository.loadFavorites(userId)
    }

    fun searchCatsByName(name: String) {
        catRepository.searchCatsByName(name)
    }

}