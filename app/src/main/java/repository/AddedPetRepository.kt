package repository

import com.google.firebase.firestore.FirebaseFirestore
import model.Pet

class AddedPetRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchAddedPets(callback: (List<Pet>) -> Unit) {
        firestore.collection("addedpets").get()
            .addOnSuccessListener { querySnapshot ->
                val pets = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Pet::class.java)
                }
                callback(pets)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}