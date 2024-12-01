package view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.tonydoumit_androidmidterm_petapp.R
import viewmodel.UserViewModel

class SignUpActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val firstNameEditText = findViewById<EditText>(R.id.firstNameEditText)
        val lastNameEditText = findViewById<EditText>(R.id.lastNameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val loginLink = findViewById<TextView>(R.id.loginlink) // Reference to the "Already have an account" link

        val firebaseAuth = FirebaseAuth.getInstance()

        signUpButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate Input
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Authentication
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()

                        // Cache user locally (including hashed password)
                        userViewModel.insertUser(email, firstName, lastName, password)

                        // Navigate to Welcome Activity
                        val intent = Intent(this,WelcomeActivity::class.java)
                        startActivity(intent)
                        finish() // Close sign-up activity
                    } else {
                        // Handle errors
                        val errorMessage = task.exception?.localizedMessage ?: "Sign-up failed"
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Add intent to redirect to LoginActivity
        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close sign-up activity
        }
    }
}
