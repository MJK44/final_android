package view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tonydoumit_androidmidterm_petapp.R
import adapter.DogAdapter
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import viewmodel.DogViewModel

class DogActivity : AppCompatActivity() {
    private lateinit var dogRecyclerView: RecyclerView
    private lateinit var dogAdapter: DogAdapter
    private lateinit var dogViewModel: DogViewModel
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog)

        setupIconListeners()
        setupRecyclerViewAndFetchDogs()
        setupSearchBar()
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
        findViewById<View>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<View>(R.id.icon_others).setOnClickListener {
            startActivity(Intent(this, AddedPetListActivity::class.java))
        }
    }

    private fun setupRecyclerViewAndFetchDogs() {
        dogRecyclerView = findViewById(R.id.dog_list)
        dogRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the DogAdapter with favorite click and item click listeners
        dogAdapter = DogAdapter(
            dogs = listOf(),
            onFavoriteClick = { dog ->
                // Toggle favorite status in the ViewModel
                dogViewModel.toggleFavorite(dog)
            },
            onItemClick = { dog ->
                // Navigate to PetDetailsActivity with selected dog details
                val intent = Intent(this, PetDetailsActivity::class.java).apply {
                    putExtra("PET_NAME", dog.name)
                    putExtra("PET_BREED", dog.breeds.primary)
                    putExtra("PET_LOCATION", dog.location)
                    putExtra("PET_IMAGE_URL", dog.photos?.firstOrNull()?.medium)
                }
                startActivity(intent)
            }
        )

        dogRecyclerView.adapter = dogAdapter

        // Initialize DogViewModel
        dogViewModel = ViewModelProvider(this)[DogViewModel::class.java]

        // Observe LiveData from DogViewModel
        dogViewModel.getDogsLiveData().observe(this) { dogs ->
            if (dogs.isNotEmpty()) {
                dogAdapter.updateDogs(dogs)
            } else {
                Toast.makeText(this, "No dogs available to display", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch dogs using DogViewModel
        dogViewModel.fetchDogsFromApi(
            clientId = "FyK3sU03vVREgeUpu5IyxKoQEbL7X23ARVEZqQxMQN5zgTpDyD",
            clientSecret = "PXgkzsUrJhcZKbMzIWWyIpvs2Ii99zyx58sB2Wlc"
        )
    }

    private fun setupSearchBar() {
        searchBar = findViewById(R.id.search_bar)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    dogViewModel.searchDogsByName(query)
                } else {
                    dogViewModel.fetchDogsFromApi(
                        clientId = "FyK3sU03vVREgeUpu5IyxKoQEbL7X23ARVEZqQxMQN5zgTpDyD",
                        clientSecret = "PXgkzsUrJhcZKbMzIWWyIpvs2Ii99zyx58sB2Wlc"
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}