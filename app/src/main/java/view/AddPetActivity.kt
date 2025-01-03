package view

import android.content.Intent
import android.os.Bundle
import android.util.Log // Import for logging
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tonydoumit_androidmidterm_petapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddPetActivity : AppCompatActivity() {

    private lateinit var petNameInput: EditText
    private lateinit var petAgeInput: EditText
    private lateinit var petTypeInput: EditText
    private lateinit var petBreedInput: EditText
    private lateinit var petLocationInput: EditText
    private lateinit var petDescriptionInput: EditText
    private lateinit var addButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pet)

        // Initialize UI components
        petNameInput = findViewById(R.id.editTextName)
        petAgeInput = findViewById(R.id.editTextAge)
        petTypeInput = findViewById(R.id.editTextType)
        petBreedInput = findViewById(R.id.editTextBreed)
        petLocationInput = findViewById(R.id.editTextLocation)
        petDescriptionInput = findViewById(R.id.editTextDescription)
        addButton = findViewById(R.id.buttonAdd)
        cancelButton = findViewById(R.id.buttonCancel)

        // Set up button listeners
        addButton.setOnClickListener {
            Toast.makeText(this, "Add button clicked", Toast.LENGTH_SHORT).show()
            addPet()
        }
        cancelButton.setOnClickListener { redirectToWelcomeActivity() }
    }

    private fun addPet() {
        val petName = petNameInput.text.toString().trim()
        val petAge = petAgeInput.text.toString().trim()
        val petType = petTypeInput.text.toString().trim()
        val petBreed = petBreedInput.text.toString().trim()
        val petLocation = petLocationInput.text.toString().trim()
        val petDescription = petDescriptionInput.text.toString().trim()

        // Validate input fields
        if (petName.isEmpty() || petAge.isEmpty() || petType.isEmpty() || petBreed.isEmpty() || petLocation.isEmpty() || petDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate age is a positive number
        val ageInt = petAge.toIntOrNull()
        if (ageInt == null || ageInt < 0) {
            Toast.makeText(this, "Please enter a valid positive age", Toast.LENGTH_SHORT).show()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "You need to be logged in to add a pet", Toast.LENGTH_SHORT).show()
            return
        }

        val petData = hashMapOf(
            "name" to petName,
            "age" to ageInt.toString(),
            "type" to petType,
            "breed" to petBreed,
            "location" to petLocation,
            "description" to petDescription,
            "addedBy" to user.uid
        )

        // Firestore operation
        FirebaseFirestore.getInstance().collection("addedpets")
            .add(petData)
            .addOnSuccessListener {
                Log.d("AddPetActivity", "Pet added successfully!")
                Toast.makeText(this, "Pet added successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("AddPetActivity", "Error: ${e.message}")
                Toast.makeText(this, "Failed to add pet: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    private fun redirectToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}