import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import model.Animal

class PetViewModel : ViewModel() {
    private val petRepository = PetRepository()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun fetchPetsFromApi(clientId: String, clientSecret: String) {
        petRepository.fetchPetsFromApi(clientId, clientSecret)
    }

    fun getPetsLiveData(): LiveData<List<Animal>> = petRepository.getPetsLiveData()

    fun getFavoritesLiveData(): LiveData<List<Animal>> = petRepository.getFavoritesLiveData()

    fun toggleFavorite(pet: Animal) {
        val userId = auth.currentUser?.uid ?: return
        petRepository.toggleFavorite(pet, userId)
        refreshFavorites()
    }

    private fun refreshFavorites() {
        val userId = auth.currentUser?.uid ?: return
        petRepository.loadFavorites(userId)
    }

    fun searchPetsByName(name: String) {
        val allPets = petRepository.getPetsLiveData().value ?: return
        val filteredPets = allPets.filter { it.name.contains(name, ignoreCase = true) }
        petRepository.updateLiveData(filteredPets) // Use repository to update LiveData
    }
}