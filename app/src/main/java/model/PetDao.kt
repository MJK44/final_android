package model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PetDao {
    @Insert
    suspend fun insertPet(pet: Pet) // Insert a new pet into the database

    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun getPetById(id: Int): Pet? // Retrieve a pet by its ID

    @Query("SELECT * FROM pets WHERE name = :name")
    suspend fun getPetByName(name: String): List<Pet> // Retrieve pets by name

   /* @Query("UPDATE Pet SET age = :age, breed = :breed, userId = :ownerId WHERE id = :id")
    suspend fun updatePet(id: Int, age: Int, breed: String, ownerId: Int) */

    @Query("SELECT * FROM pets")
    suspend fun getAllPets(): List<Pet> // Retrieve all pets

    @Query("SELECT * FROM pets GROUP BY type")
    fun getOnePetPerType(): List<Pet>
}