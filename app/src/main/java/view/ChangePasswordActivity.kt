package view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.tonydoumit_androidmidterm_petapp.R
import viewmodel.UserViewModel

class ChangePasswordActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextNewPassword: EditText = findViewById(R.id.editTextNewPassword)
        val editTextConfirmPassword: EditText = findViewById(R.id.editTextConfirmPassword)
        val buttonChangePassword: Button = findViewById(R.id.buttonChangePassword)
        val buttonCancel: Button = findViewById(R.id.buttonCancel) // Cancel button
        val textViewPasswordWarning: TextView = findViewById(R.id.textViewPasswordWarning)

        // Cancel button logic to navigate to WelcomeActivity
        buttonCancel.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish() // Close the activity when the user clicks cancel
        }

        // Change password button logic
        buttonChangePassword.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val newPassword = editTextNewPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            // Check if the new password and confirm password match
            if (newPassword != confirmPassword) {
                textViewPasswordWarning.text = getString(R.string.passwords_do_not_match)
                textViewPasswordWarning.visibility = TextView.VISIBLE
            } else {
                textViewPasswordWarning.visibility = TextView.GONE

                // Fetch the current user profile from Room
                userViewModel.getUserProfile(email).observe(this, Observer { user ->
                    user?.let {
                        // Check if the current password is correct (this logic can be added to the repository)
                        // Here, I am assuming user.hashedPassword is the old password.
                        if (it.hashedPassword == newPassword) {
                            textViewPasswordWarning.text = getString(R.string.password_is_same_as_old)
                            textViewPasswordWarning.visibility = TextView.VISIBLE
                        } else {
                            // Update password in both Firebase and Room
                            userViewModel.updatePassword(email, newPassword).observe(this, Observer { success ->
                                if (success) {
                                    // Redirect to LoginActivity after successful password change
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish() // Close the current activity
                                } else {
                                    // Handle failure
                                    textViewPasswordWarning.text = getString(R.string.password_update_failed)
                                    textViewPasswordWarning.visibility = TextView.VISIBLE
                                }
                            })
                        }
                    } ?: run {
                        // Handle user not found
                        textViewPasswordWarning.text = getString(R.string.user_not_found)
                        textViewPasswordWarning.visibility = TextView.VISIBLE
                    }
                })
            }
        }
    }
}
