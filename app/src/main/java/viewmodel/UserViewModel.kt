package viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.AppDatabase
import model.User
import repository.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    // Insert user with plain password (password will be hashed inside the repository)
    fun insertUser(email: String, firstName: String, lastName: String, password: String) {
        viewModelScope.launch {
            repository.insertUser(email, firstName, lastName, password)
        }
    }

    // Authenticate user with Firebase (delegated to repository)
    fun authenticateUser(email: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val isValid = repository.authenticateWithFirebase(email, password)
            result.postValue(isValid)
        }
        return result
    }

    // Authenticate user offline with Room (delegated to repository)
    fun authenticateOffline(email: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val isValid = repository.authenticateOffline(email, password)
            result.postValue(isValid)
        }
        return result
    }
    // Fetch user profile
    //fetch a user's profile from the local database (Room). It takes an email as input and returns the user profile as a LiveData<User?>.
    fun getUserProfile(email: String): LiveData<User?> {
        val result = MutableLiveData<User?>()
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch from Room
            val user = repository.getCachedUser(email, "")
            result.postValue(user)
        }
        return result
    }
    // Update user profile (in both Firebase and Room)
    fun updateUserProfile(email: String, firstName: String, lastName: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>() //This will hold the success or failure of the operation
        viewModelScope.launch(Dispatchers.IO) {
            // Update Firebase
            val firebaseUser = repository.updateFirebaseUserProfile(email, firstName, lastName) //The updateFirebaseUserProfile method in the repository is called to update the user's profile in Firebase. It takes the email, firstName, and lastName as arguments.
            if (firebaseUser) { //if the firebase update is successful, the profile is then updated locally in Room by calling the updateUserInRoom method in the repository
                // Update locally in Room
                val userUpdated = repository.updateUserInRoom(email, firstName, lastName)
                result.postValue(userUpdated)
            } else {
                result.postValue(false)
            }
        }
        return result
    }
    // Update password (in both Firebase and Room)
    fun updatePassword(email: String, newPassword: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.updatePasswordInRoom(email, newPassword)
            if (success) {
                // Update password in Firebase as well
                val firebaseSuccess = repository.updatePasswordInFirebase(email, newPassword)
                result.postValue(firebaseSuccess)
            } else {
                result.postValue(false)
            }
        }
        return result
    }

    // Authenticate with Google
    fun authenticateWithGoogle(idToken: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val isValid = repository.authenticateWithGoogle(idToken)
            result.postValue(isValid)
        }
        return result
    }
    fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1:157331824307:android:554495dd183bf7cf985ac6")
            .requestEmail()
            .build()
    }
}