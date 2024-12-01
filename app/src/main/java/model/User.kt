package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int =0, //Auto-increment unique ID for each user
    val firstName: String, //First name of the user
    val lastName: String, //Last name of the user
    val email: String, //email of the user
    val hashedPassword: String //Password stored as a hashed value for security
)
//@Entity : Marks this class as a database table
//PrimaryKey : Specifies the primary key column for unique identification