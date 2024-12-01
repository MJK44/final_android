package repository
import model.User
import model.UserDao
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import model.AppDatabase
import java.security.MessageDigest
import java.nio.charset.StandardCharsets

class UserRepository(private val userDao: UserDao) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Authenticate user using Firebase
    suspend fun authenticateWithFirebase(email: String, password: String): Boolean {
        return try {
            // Sign in with Firebase (throws exception if login fails)
            firebaseAuth.signInWithEmailAndPassword(email, password).await()

            // Cache user details locally after Firebase authentication
            val user = firebaseAuth.currentUser
            if (user != null) {
                // Hash password before caching locally (even though it's not used directly in Firebase)
                cacheUserLocally(email, "", "", password)
            }
            true // Authentication successful
        } catch (e: Exception) {
            false // Authentication failed
        }
    }


    // Store user details locally after Firebase authentication
    suspend fun cacheUserLocally(email: String, firstName: String, lastName: String, password: String) {
        val hashedPassword = hashPassword(password)
        val user = User(email = email, firstName = firstName, lastName = lastName, hashedPassword = hashedPassword)
        userDao.insertUser(user) // Save user in Room with hashed password
    }

    // Fetch user details from Room (for offline support)
    suspend fun getCachedUser(email: String,password: String): User? {
        return userDao.getUserByEmailAndPassword(email,password)
    }

    // Function to insert a new user
    suspend fun insertUser(email: String, firstName: String, lastName: String, password: String) {
        // Hash the password before saving it to the database
        val hashedPassword = hashPassword(password)

        // Create user object and insert into the database
        val user = User(firstName = firstName, lastName = lastName, email = email, hashedPassword = hashedPassword)
        userDao.insertUser(user)
    }

    // Authenticate user offline (compare hashed passwords)
    suspend fun authenticateOffline(email: String, password: String): Boolean {
        val user = getCachedUser(email,password)
        return user?.hashedPassword == hashPassword(password) // Compare hashed passwords
    }

    // Helper function to hash the password using SHA-256 (or another hashing algorithm)
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(StandardCharsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Fetch user profile from Firebase
    suspend fun updateFirebaseUserProfile(email: String, firstName: String, lastName: String): Boolean {
        return try {
            val user = firebaseAuth.currentUser //To retrieve the current authenticated firebase user
            if (user != null && user.email == email) { //ensures that the update is happening only if the email of the logged-in user matches the one passed to the method
                val userUpdates = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName
                )
                firestore.collection("users").document(user.uid).update(userUpdates as Map<String, Any>).await() // updates the firstName and lastName fields in the Firestore document. The userUpdates map contains the fields to be updated.
                //await() is used to make the function suspend and wait f
                true //Update successful
            } else {
                false // if any exception occurs or if the user is not found
            }
        } catch (e: Exception) {
            false
        }
    }

    // Update user locally in Room
    suspend fun updateUserInRoom(email: String, firstName: String, lastName: String): Boolean {
        return try {
            val hashedPassword = ""
            val user = User(email = email, firstName = firstName, lastName = lastName, hashedPassword = hashedPassword)
            userDao.insertUser(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updatePasswordInRoom(email: String, newPassword: String): Boolean {
        // Hash the password before storing it in the database
        val hashedPassword = hashPassword(newPassword)
        // Update the password in Room database
        val user = userDao.getUserByEmailAndPassword(email, hashedPassword)
        return if (user != null) {
            userDao.updatePassword(user.id, hashedPassword)
            true
        } else {
            false
        }
    }// Update password in Firebase
    suspend fun updatePasswordInFirebase(email: String, newPassword: String): Boolean {
        return try {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser?.updatePassword(newPassword)?.await()
            true
        } catch (e: Exception) {
            false
        }
    }
    // Authenticate with Google (Firebase)
    suspend fun authenticateWithGoogle(idToken: String): Boolean {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()

            val user = firebaseAuth.currentUser
            if (user != null) {
                cacheUserLocally(user.email ?: "", user.displayName ?: "", "", "")
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
