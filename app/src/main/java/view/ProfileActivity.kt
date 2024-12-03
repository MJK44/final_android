package view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.tonydoumit_androidmidterm_petapp.R
import viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    // UI elements
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize the UI elements
        firstNameEditText = findViewById(R.id.editTextFirstName)
        lastNameEditText = findViewById(R.id.editTextLastName)
        updateButton = findViewById(R.id.buttonUpdate)
        backButton = findViewById(R.id.buttonCancel)

        // Fetch user profile
        fetchUserProfile()

        // Handle Update button click
        updateButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                updateUserProfile(firstName, lastName)
            }
        }

        // Handle Back button click (to navigate to WelcomeActivity)
        backButton.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUserProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userViewModel.getUserProfile(currentUser.uid).observe(this, Observer { user ->
                if (user != null) {
                    firstNameEditText.setText(user.firstName)
                    lastNameEditText.setText(user.lastName)
                }
            })
        }
    }

    private fun updateUserProfile(firstName: String, lastName: String) {
        userViewModel.updateUserProfile(firstName, lastName).observe(this, Observer { isUpdated ->
            if (isUpdated) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
