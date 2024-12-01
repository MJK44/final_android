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

    @Query("SELECT * FROM users where email = :email AND hashedPassword = :hashedPassword")
    suspend fun getUserByEmailAndPassword(email: String, hashedPassword: String) : User? //Retrieve useer by email and hashed password

    @Update
    suspend fun updatePassword(id: Int, hashedPassword: String)
}
//@Dao : Marks the interface as a Data Access Object
//@Insert : Adds a new user to the database
//@Query : Retrieves user details by their email and password for login validation
//This interface contains methods for accessing the database. It's the interface through which we interact with the database.