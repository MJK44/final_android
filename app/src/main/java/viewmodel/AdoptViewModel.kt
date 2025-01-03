package viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import repository.AdoptRepository

class AdoptViewModel : ViewModel() {
    private val adoptRepository = AdoptRepository()

    fun adoptPet(
        petName: String,
        petBreed: String,
        petLocation: String,
        petImageUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("AdoptViewModel", "Calling adoptPet with petName: $petName, petBreed: $petBreed")
        adoptRepository.adoptPet(petName, petBreed, petLocation, petImageUrl, onSuccess, onFailure)
    }
}