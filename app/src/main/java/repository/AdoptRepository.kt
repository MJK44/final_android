package repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdoptRepository {
    private val db = FirebaseFirestore.getInstance()
    private val adoptedPetsCollection = db.collection("adoptedpets")

    fun adoptPet(
        petName: String,
        petBreed: String,
        petLocation: String,
        petImageUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("AdoptRepository", "User not logged in")
            onFailure(Exception("User not logged in"))
            return
        }

        val petData = mapOf(
            "name" to petName,
            "breed" to petBreed,
            "location" to petLocation,
            "imageUrl" to petImageUrl,
            "userId" to userId // Link the pet to the user who adopted it
        )

        // Add the pet to the adoptedPets collection
        adoptedPetsCollection.add(petData)
            .addOnSuccessListener {
                Log.d("AdoptRepository", "Pet added to adoptedPets collection successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("AdoptRepository", "Failed to add pet to adoptedPets collection: ${e.message}")
                onFailure(e)
            }
    }
}