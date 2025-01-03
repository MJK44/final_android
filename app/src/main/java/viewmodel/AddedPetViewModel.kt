package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AddedPetViewModel : ViewModel() {

    private val _pets = MutableLiveData<List<Map<String, String>>>()
    val pets: LiveData<List<Map<String, String>>> get() = _pets

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchAddedPets() {
        FirebaseFirestore.getInstance().collection("addedpets")
            .get()
            .addOnSuccessListener { documents ->
                val pets = documents.mapNotNull { document ->
                    val name = document.getString("name")
                    val age = document.getString("age")
                    val breed = document.getString("breed")
                    val location = document.getString("location")
                    val description = document.getString("description")

                    // Ensure all required fields are present
                    if (name != null && age != null && breed != null && location != null && description != null) {
                        mapOf(
                            "name" to name,
                            "age" to age,
                            "breed" to breed,
                            "location" to location,
                            "description" to description
                        )
                    } else null
                }
                _pets.value = pets
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = exception.message
            }
    }
}
