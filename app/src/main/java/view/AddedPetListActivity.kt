package view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tonydoumit_androidmidterm_petapp.R
import adapter.AddedPetAdapter
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import viewmodel.AddedPetViewModel

class AddedPetListActivity : AppCompatActivity() {

    private lateinit var addedPetRecyclerView: RecyclerView
    private lateinit var addedPetAdapter: AddedPetAdapter
    private lateinit var addedPetViewModel: AddedPetViewModel
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_added_pet_list)

        // Initialize Views
        searchBar = findViewById(R.id.search_bar)
        addedPetRecyclerView = findViewById(R.id.pet_list)

        setupViewModel()
        setupRecyclerViews()
        setupSearchBar()
        observeViewModel()
        setupIconListeners()
    }

    private fun setupViewModel() {
        addedPetViewModel = ViewModelProvider(this)[AddedPetViewModel::class.java]
        addedPetViewModel.fetchAddedPets()
    }

    private fun setupIconListeners() {
        findViewById<View>(R.id.icon_cat).setOnClickListener {
            startActivity(Intent(this, CatActivity::class.java))
        }
        findViewById<View>(R.id.icon_horse).setOnClickListener {
            startActivity(Intent(this, HorseActivity::class.java))
        }
        findViewById<View>(R.id.icon_rabbit).setOnClickListener {
            startActivity(Intent(this, RabbitActivity::class.java))
        }
        findViewById<View>(R.id.icon_dog).setOnClickListener {
            startActivity(Intent(this, DogActivity::class.java))
        }
        findViewById<View>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<View>(R.id.icon_add).setOnClickListener {
            startActivity(Intent(this, AddPetActivity::class.java))
        }
    }

    private fun setupRecyclerViews() {
        // Initialize and set up AddedPetAdapter
        addedPetAdapter = AddedPetAdapter(listOf()) { pet ->
            adoptPet(pet)
        }
        addedPetRecyclerView.layoutManager = LinearLayoutManager(this)
        addedPetRecyclerView.adapter = addedPetAdapter
    }

    private fun setupSearchBar() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        val filteredAddedPets = addedPetViewModel.pets.value?.filter { pet ->
            pet["name"]?.contains(query, ignoreCase = true) == true
        } ?: emptyList()

        addedPetAdapter.updateList(filteredAddedPets) // Update list with filtered data
    }

    private fun observeViewModel() {
        addedPetViewModel.pets.observe(this) { pets ->
            addedPetAdapter.updateList(pets) // Update list with all data
        }
    }

    private fun adoptPet(pet: Map<String, String>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val petId = pet["id"] ?: System.currentTimeMillis().toString() // Use timestamp if no ID
            val petDetails = mapOf(
                "name" to pet["name"],
                "age" to pet["age"],
                "type" to pet["type"],
                "breed" to pet["breed"],
                "location" to pet["location"],
                "description" to pet["description"]
            )

            FirebaseFirestore.getInstance()
                .collection("usersadoption")
                .document(userId)
                .collection("adopted_pets")
                .document(petId)
                .set(petDetails)
                .addOnSuccessListener {
                    Toast.makeText(this, "${pet["name"]} adopted successfully!", Toast.LENGTH_SHORT).show()
                    // Redirect to WelcomeActivity
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish() // Close the current activity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to adopt: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Please log in to adopt a pet.", Toast.LENGTH_SHORT).show()
        }
    }
}