package model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val breed: String,
    val age: Int,
    val location: String,
    val description: String,
    var imageUrl: String,
    var type: String,
    //val userId: String,
)