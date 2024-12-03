package view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tonydoumit_androidmidterm_petapp.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

class ChangePasswordActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var textViewPasswordWarning: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextCurrentPassword: EditText = findViewById(R.id.editTextCurrentPassword)
        val editTextNewPassword: EditText = findViewById(R.id.editTextNewPassword)
        val editTextConfirmPassword: EditText = findViewById(R.id.editTextConfirmPassword)
        val buttonChangePassword: Button = findViewById(R.id.buttonChangePassword)
        val buttonCancel: Button = findViewById(R.id.buttonCancel)
        textViewPasswordWarning = findViewById(R.id.textViewPasswordWarning)

        // Cancel button logic
        buttonCancel.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Change password button logic
        buttonChangePassword.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val currentPassword = editTextCurrentPassword.text.toString().trim()
            val newPassword = editTextNewPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            // Validate input
            if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                textViewPasswordWarning.text = getString(R.string.all_fields_required)
                textViewPasswordWarning.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                textViewPasswordWarning.text = getString(R.string.passwords_do_not_match)
                textViewPasswordWarning.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            if (!isPasswordStrong(newPassword)) {
                textViewPasswordWarning.text = "Password must be at least 6 characters."
                textViewPasswordWarning.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            textViewPasswordWarning.visibility = TextView.GONE

            val currentUser = firebaseAuth.currentUser
            if (currentUser != null && currentUser.email == email) {
                val credential = EmailAuthProvider.getCredential(email, currentPassword)
                currentUser.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            Log.d("ChangePassword", "Re-authentication successful.")
                            currentUser.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Log.d("ChangePassword", "Password successfully updated in Firebase.")
                                        Toast.makeText(
                                            this,
                                            getString(R.string.password_updated),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish()
                                    } else {
                                        Log.e("ChangePassword", "Error updating password in Firebase: ${updateTask.exception?.localizedMessage}")
                                        handleFirebasePasswordUpdateFailure(updateTask.exception)
                                    }
                                }
                        } else {
                            Log.e("ChangePassword", "Re-authentication failed: ${reauthTask.exception?.localizedMessage}")
                            textViewPasswordWarning.text = "Re-authentication failed: ${reauthTask.exception?.localizedMessage}"
                            textViewPasswordWarning.visibility = TextView.VISIBLE
                        }
                    }
            } else {
                textViewPasswordWarning.text = getString(R.string.user_not_authenticated)
                textViewPasswordWarning.visibility = TextView.VISIBLE
            }
        }
    }

    private fun handleFirebasePasswordUpdateFailure(exception: Exception?) {
        if (exception is FirebaseAuthRecentLoginRequiredException) {
            textViewPasswordWarning.text = getString(R.string.relogin_required)
        } else {
            textViewPasswordWarning.text = "Error: ${exception?.localizedMessage}"
        }
        textViewPasswordWarning.visibility = TextView.VISIBLE
    }

    private fun isPasswordStrong(password: String): Boolean {
        return password.length >= 6 // Modify to include additional checks if needed
    }
}
