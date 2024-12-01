package model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class],version=1)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun userDao(): UserDao //Provides access to userDao methods

    companion object // Companion object : Defines a single shared instance of the AppDatabase class that can be accessed globally (Ensures that only one instance of the database is created)
    {
        @Volatile //Volatile : Ensures that changes made to the INSTANCE variable are visible to all threads immediately (Prevents multiple threads from creating separate instances of the database)
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase //This method provides a globally accessible instance of the database, requires Context object to access the application's resources and used to create the database file
        {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database" //Database file name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
//@Database: Marks this class as a Room database.
//entities = [User::class] : Specifies the tables in our database. User is the entity representing the users table.
//Version = 1 : Defines the version of the database. The version will be incremented whenever we make changes to the database schema (like adding a new column or table).
//The AppDatabase class extends RoomDatabase, making it the main database class (It provides access to the database and its DAO)
//getDatabase: Creates a singleton instance of the database to prevent multiple instances.
//INSTANCE ? : Checks if the INSTANCE variable is null. If it's not null, the current instance is returned
//synchronized(this) : Ensures that only one thread can access this block at a time. Prevents multiple threads from creating separate database instances simultaneously
//context.applicationContext: Uses the application context to avoid memory leaks.
//AppDatabase::class.java: Specifies the class that defines the database schema.
//"user_database": Name of the database file.
//.build : Builds the database instance with the specified configuration.
//INSTANCE = instance : Stores the created database instance in the INSTANCE variable to reuse it in future calls.
//Singleton: Ensures there is only one instance of AppDatabase in the app.
//Thread-Safety: Uses @Volatile and synchronized to prevent multiple threads from creating duplicate instances.
//Reusable: The getDatabase method returns the shared instance for use across the app.
//Room Integration: The Room.databaseBuilder sets up the database using the User entity and UserDao.

//IN SUMMARY :
//AppDataBase manages the database instance, and gives access to the Dao, so in the Dao we define the queries on the users table to perform operations like fetching a user by email inserting a new user, or validating login credentials
