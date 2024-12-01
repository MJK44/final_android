package utils
import java.security.MessageDigest

// Function to hash the password using SHA-256
fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

// Function to validate password based on security requirements
fun validatePassword(password: String): Pair<Boolean, String?> {
    if (password.length < 8) return Pair(false, "Password must be at least 8 characters long.")
    if (!password.matches(".*[A-Z].*".toRegex())) return Pair(false, "Password must contain at least one uppercase letter.")
    if (!password.matches(".*\\d.*".toRegex())) return Pair(false, "Password must contain at least one number.")
    if (!password.matches(".*[@#\$%^&+=].*".toRegex())) return Pair(false, "Password must contain at least one special character.")
    return Pair(true, null)
}
//The kotlin file option lets us to create standalone kotlin files for utility functions that aren't tied to a specific class or object. This keeps our project organized and reusable