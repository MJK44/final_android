package repository

import android.util.Log
import model.User
import model.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Authenticate user using Firebase
    suspend fun authenticateWithFirebase(email: String, password: String): Boolean {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = firebaseAuth.currentUser
            if (user != null) {
                val hashedPassword = hashPassword(password)
                cacheUserLocally(email, "", "", hashedPassword)
            }
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error authenticating with Firebase: ${e.localizedMessage}")
            false
        }
    }

    // Cache user locally in Room
    suspend fun cacheUserLocally(email: String, firstName: String, lastName: String, hashedPassword: String) {
        val user = User(email = email, firstName = firstName, lastName = lastName, hashedPassword = hashedPassword)
        userDao.insertUser(user)
    }

    // Fetch user from Room by email
    suspend fun getCachedUser(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    // Insert a new user
    suspend fun insertUser(email: String, firstName: String, lastName: String, password: String) {
        val hashedPassword = hashPassword(password)
        val user = User(firstName = firstName, lastName = lastName, email = email, hashedPassword = hashedPassword)
        userDao.insertUser(user)
    }


    // Update password in Firebase
    suspend fun updatePasswordInFirebase(newPassword: String): Boolean {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            firebaseUser?.updatePassword(newPassword)?.await()
            Log.d("UserRepository", "Password successfully updated in Firebase.")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating password in Firebase: ${e.localizedMessage}")
            false
        }
    }

    // Authenticate with Google
    suspend fun authenticateWithGoogle(idToken: String): Boolean {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()

            firebaseAuth.currentUser?.let { user ->
                val email = user.email ?: return false // Fails if email is null
                val displayName = user.displayName ?: ""
                cacheUserLocally(email, displayName, "", "")
                true
            } ?: false
        } catch (e: Exception) {
            Log.e("UserRepository", "Error authenticating with Google: ${e.localizedMessage}", e)
            false
        }
    }

    // Hash password using SHA-256
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(StandardCharsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Update user profile in Firebase
    suspend fun updateFirebaseUserProfile(firstName: String, lastName: String): Boolean {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val updates = mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName
                )
                firestore.collection("users").document(currentUser.uid).update(updates).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user profile in Firebase: ${e.localizedMessage}")
            false
        }
    }
}