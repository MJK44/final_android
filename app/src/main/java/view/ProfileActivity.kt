package view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.tonydoumit_androidmidterm_petapp.R
import viewmodel.UserViewModel

class ProfileActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    // UI elements
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var backButton: Button // Assuming you have a back button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize the UI elements
        firstNameEditText = findViewById(R.id.editTextFirstName)
        lastNameEditText = findViewById(R.id.editTextLastName)
        emailEditText = findViewById(R.id.editTextEmail)
        updateButton = findViewById(R.id.buttonUpdate)
        backButton = findViewById(R.id.buttonBack) // Assuming you have a back button in the layout

        // Fetch user profile (assuming user is logged in)
        fetchUserProfile()

        // Handle Update button click
        updateButton.setOnClickListener {
            updateUserProfile()
        }

        // Handle Back button click (to navigate to WelcomeActivity)
        backButton.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)  // Create an intent to navigate to WelcomeActivity
            startActivity(intent)  // Start WelcomeActivity
            finish()  // Optionally finish the current activity to prevent returning to it
        }
        // Handle Change Password link click
        val changePasswordLink = findViewById<TextView>(R.id.textViewChangePassword)
        changePasswordLink.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)  // Replace with your ChangePasswordActivity class
            startActivity(intent)
        }
    }

    // Fetch user data (from Room, Firebase, or both)
    private fun fetchUserProfile() {
        val email = "user@example.com" // Get the logged-in user's email (you can fetch it from Firebase Auth)

        // Check user data from Firebase or Room
        userViewModel.getUserProfile(email).observe(this, Observer { user ->
            if (user != null) {
                // Populate the EditTexts with the user data
                firstNameEditText.setText(user.firstName)
                lastNameEditText.setText(user.lastName)
                emailEditText.setText(user.email)
            } else {
                // Handle error or no data (you can show a message)
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Handle profile update
    private fun updateUserProfile() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()

        // Validate input
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        // Update user profile both locally (Room) and remotely (Firebase)
        userViewModel.updateUserProfile(email, firstName, lastName).observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
