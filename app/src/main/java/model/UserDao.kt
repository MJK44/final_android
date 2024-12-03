package model

import androidx.room.Dao
import model.User
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user:User) //Insert a new user into the database

    @Query("SELECT * FROM users where email = :email")
    suspend fun getUserByEmail(email: String) : User? //Retrieve user by email

    @Query("UPDATE users SET hashedPassword = :hashedPassword WHERE id = :id")
    suspend fun updatePassword(id: Int, hashedPassword: String)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

}
//@Dao : Marks the interface as a Data Access Object
//@Insert : Adds a new user to the database
//@Query : Retrieves user details by their email and password for login validation
//This interface contains methods for accessing the database. It's the interface through which we interact with the database.