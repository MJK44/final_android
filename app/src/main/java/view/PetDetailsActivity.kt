/*
package view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tonydoumit_androidmidterm_petapp.R
import com.squareup.picasso.Picasso
import viewmodel.AdoptViewModel

class PetDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_details)

        // Extract intent data
        val petName = intent.getStringExtra("PET_NAME") ?: "Unknown"
        val petBreed = intent.getStringExtra("PET_BREED") ?: "Unknown"
        val petLocation = intent.getStringExtra("PET_LOCATION") ?: "Unknown"
        val petImageUrl = intent.getStringExtra("PET_IMAGE_URL") ?: ""

        // Display pet details
        findViewById<TextView>(R.id.pet_name).text = petName
        findViewById<TextView>(R.id.pet_breed).text = petBreed
        findViewById<TextView>(R.id.pet_location).text = petLocation
        Picasso.get().load(petImageUrl).into(findViewById<ImageView>(R.id.pet_image))

        // Adopt button logic
        findViewById<Button>(R.id.adopt_button).setOnClickListener {
            Log.d("PetDetailsActivity", "Adopt button clicked")
            Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show()
            AdoptViewModel().adoptPet(
                petName = petName,
                petBreed = petBreed,
                petLocation = petLocation,
                petImageUrl = petImageUrl,
                onSuccess = {
                    Log.d("PetDetailsActivity", "Pet adopted successfully")
                    Toast.makeText(this, "Pet adopted successfully!", Toast.LENGTH_SHORT).show()

                    // Redirect to WelcomeActivity
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish() // Close the current activity to prevent going back
                },
                onFailure = { e ->
                    Log.e("PetDetailsActivity", "Failed to adopt pet: ${e.message}")
                    Toast.makeText(this, "Failed to adopt pet: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}*/

package view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tonydoumit_androidmidterm_petapp.R
import com.squareup.picasso.Picasso
import viewmodel.AdoptViewModel

class PetDetailsActivity : AppCompatActivity() {
    private lateinit var adoptViewModel: AdoptViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_details)

        // Initialize ViewModel
        adoptViewModel = AdoptViewModel()

        // Extract intent data
        val petName = intent.getStringExtra("PET_NAME") ?: "Unknown"
        val petBreed = intent.getStringExtra("PET_BREED") ?: "Unknown"
        val petLocation = intent.getStringExtra("PET_LOCATION") ?: "Unknown"
        val petImageUrl = intent.getStringExtra("PET_IMAGE_URL") ?: ""

        // Display pet details
        findViewById<TextView>(R.id.pet_name).text = petName
        findViewById<TextView>(R.id.pet_breed).text = petBreed
        findViewById<TextView>(R.id.pet_location).text = petLocation
        if (petImageUrl.isNotEmpty()) {
            Picasso.get().load(petImageUrl).into(findViewById<ImageView>(R.id.pet_image))
        }

        // Adopt button logic
        findViewById<Button>(R.id.adopt_button).setOnClickListener {
            Log.d("PetDetailsActivity", "Adopt button clicked")
            adoptPet(petName, petBreed, petLocation, petImageUrl)
        }
    }

    private fun adoptPet(petName: String, petBreed: String, petLocation: String, petImageUrl: String) {
        adoptViewModel.adoptPet(
            petName = petName,
            petBreed = petBreed,
            petLocation = petLocation,
            petImageUrl = petImageUrl,
            onSuccess = {
                Log.d("PetDetailsActivity", "Pet adopted successfully")
                Toast.makeText(this, "Pet adopted successfully!", Toast.LENGTH_SHORT).show()

                // Redirect to WelcomeActivity
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish() // Close the current activity to prevent going back
            },
            onFailure = { e ->
                Log.e("PetDetailsActivity", "Failed to adopt pet: ${e.message}")
                Toast.makeText(this, "Failed to adopt pet: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}