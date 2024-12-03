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

    // Insert user into Room
    fun insertUser(email: String, firstName: String, lastName: String, password: String) {
        viewModelScope.launch {
            repository.insertUser(email, firstName, lastName, password)
        }
    }

    // Authenticate user with Firebase
    fun authenticateUser(email: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val isValid = repository.authenticateWithFirebase(email, password)
            result.postValue(isValid)
        }
        return result
    }

    // Fetch user profile from Room
    fun getUserProfile(email: String): LiveData<User?> {
        val result = MutableLiveData<User?>()
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getCachedUser(email)
            result.postValue(user)
        }
        return result
    }

    // Update user profile in Firebase
    fun updateUserProfile(firstName: String, lastName: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.updateFirebaseUserProfile(firstName, lastName)
            result.postValue(success)
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

    // Get Google Sign-In options
    fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1:157331824307:android:554495dd183bf7cf985ac6")
            .requestEmail()
            .build()
    }

    // Update password in Firebase
    fun updatePasswordInFirebase(newPassword: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.updatePasswordInFirebase(newPassword)
            result.postValue(success)
        }
        return result
    }
}
