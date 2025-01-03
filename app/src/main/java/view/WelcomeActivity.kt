package view

import PetViewModel
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tonydoumit_androidmidterm_petapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import adapter.PetAdapter

class WelcomeActivity : AppCompatActivity() {

    private lateinit var petRecyclerView: RecyclerView
    private lateinit var petAdapter: PetAdapter
    private lateinit var petViewModel: PetViewModel
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        setupWelcomeMessage()
        setupIconListeners()
        setupRecyclerViewAndFetchPets()
        setupSearchBar() // Initialize the search bar
    }

    private fun setupWelcomeMessage() {
        val welcomeMessage = findViewById<TextView>(R.id.welcome_message)
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val firstName = document.getString("firstName") ?: user.displayName?.split(" ")?.firstOrNull()
                    if (!firstName.isNullOrBlank()) {
                        welcomeMessage.text = "Welcome, $firstName"
                    } else {
                        welcomeMessage.text = "Welcome, User"
                    }
                }
                .addOnFailureListener {
                    welcomeMessage.text = "Welcome, ${user.displayName?.split(" ")?.firstOrNull() ?: "User"}"
                }
        } else {
            welcomeMessage.text = "Hello, User"
        }
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
        findViewById<View>(R.id.icon_others).setOnClickListener {
            startActivity(Intent(this, AddedPetListActivity::class.java))
        }
    }

    fun onDogIconClicked(view: View) {
        val intent = Intent(this, DogActivity::class.java)
        startActivity(intent)
    }

    private fun setupRecyclerViewAndFetchPets() {
        petRecyclerView = findViewById(R.id.pet_list)
        petRecyclerView.layoutManager = LinearLayoutManager(this)

        petAdapter = PetAdapter(
            pets = listOf(),
            onFavoriteClick = { pet ->
                petViewModel.toggleFavorite(pet)
            },
            onItemClick = { pet ->
                val intent = Intent(this@WelcomeActivity, PetDetailsActivity::class.java)

                intent.putExtra("PET_NAME", pet.name)
                intent.putExtra("PET_BREED", pet.breeds.primary)
                intent.putExtra("PET_LOCATION", pet.location)
                intent.putExtra("PET_IMAGE_URL", pet.photos?.firstOrNull()?.medium)
                startActivity(intent)
            }
        )

        petRecyclerView.adapter = petAdapter

        petViewModel = ViewModelProvider(this)[PetViewModel::class.java]

        petViewModel.getPetsLiveData().observe(this) { pets ->
            if (pets.isNotEmpty()) {
                petAdapter.updatePets(pets)
            } else {
                Toast.makeText(this, "No pets available to display", Toast.LENGTH_SHORT).show()
            }
        }

        petViewModel.fetchPetsFromApi(
            clientId = "FyK3sU03vVREgeUpu5IyxKoQEbL7X23ARVEZqQxMQN5zgTpDyD",
            clientSecret = "PXgkzsUrJhcZKbMzIWWyIpvs2Ii99zyx58sB2Wlc"
        )
    }

    private fun setupSearchBar() {
        searchBar = findViewById(R.id.search_bar)

        // Add a TextWatcher to listen for changes in the search bar
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    petViewModel.searchPetsByName(query) // Perform search in ViewModel
                } else {
                    petViewModel.fetchPetsFromApi(
                        clientId = "FyK3sU03vVREgeUpu5IyxKoQEbL7X23ARVEZqQxMQN5zgTpDyD",
                        clientSecret = "PXgkzsUrJhcZKbMzIWWyIpvs2Ii99zyx58sB2Wlc"
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}