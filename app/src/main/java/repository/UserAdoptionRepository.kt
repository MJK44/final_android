package repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserAdoptionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val adoptionCollection = db.collection("useradoption")

    fun adoptPet(
        petId: String,
        petName: String,
        petBreed: String,
        petType: String,
        petImageUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("UserAdoptionRepository", "User not logged in")
            onFailure(Exception("User is not logged in"))
            return
        }

        val petData = mapOf(
            "id" to petId,
            "name" to petName,
            "breed" to petBreed,
            "type" to petType,
            "imageUrl" to petImageUrl,
            "userId" to userId
        )

        adoptionCollection.add(petData)
            .addOnSuccessListener {
                Log.d("UserAdoptionRepository", "Pet added to useradoption collection successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("UserAdoptionRepository", "Failed to add pet: ${e.message}")
                onFailure(e)
            }
    }
}