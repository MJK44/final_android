package view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tonydoumit_androidmidterm_petapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val googleSignInButton = findViewById<Button>(R.id.googleSignInButton)

        // Configure Google Sign-In
        googleSignInClient = GoogleSignIn.getClient(this, userViewModel.getGoogleSignInOptions())

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginButton.isEnabled = false
            Toast.makeText(this, "Authenticating...", Toast.LENGTH_SHORT).show()

            userViewModel.authenticateUser(email, password).observe(this, { isValid ->
                loginButton.isEnabled = true
                if (isValid) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Invalid credentials or network error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val signUpLink = findViewById<TextView>(R.id.signUpLink)
        signUpLink.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        val editProfileLink = findViewById<TextView>(R.id.editProfileLink)
        editProfileLink.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    userViewModel.authenticateWithGoogle(it.idToken ?: "").observe(this, { isValid ->
                        if (isValid) {
                            startActivity(Intent(this, WelcomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
