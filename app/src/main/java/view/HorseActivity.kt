package view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tonydoumit_androidmidterm_petapp.R
import adapter.HorseAdapter
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import viewmodel.HorseViewModel

class HorseActivity : AppCompatActivity() {
    private lateinit var horseRecyclerView: RecyclerView
    private lateinit var horseAdapter: HorseAdapter
    private lateinit var horseViewModel: HorseViewModel
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horse)

        setupIconListeners()
        setupRecyclerViewAndFetchHorses()
        setupSearchBar()
    }

    private fun setupIconListeners() {
        findViewById<View>(R.id.icon_cat).setOnClickListener {
            startActivity(Intent(this, CatActivity::class.java))
        }
        findViewById<View>(R.id.icon_dog).setOnClickListener {
            startActivity(Intent(this, DogActivity::class.java))
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

    private fun setupRecyclerViewAndFetchHorses() {
        horseRecyclerView = findViewById(R.id.horse_list)
        horseRecyclerView.layoutManager = LinearLayoutManager(this)

        // Create an adapter instance and pass a lambda for handling favorite clicks
        horseAdapter = HorseAdapter(
            horses = listOf(),
            onFavoriteClick = { horse ->
                horseViewModel.toggleFavorite(horse)
            },
            onItemClick = { horse ->
                val intent = Intent(this, PetDetailsActivity::class.java)
                intent.putExtra("PET_NAME", horse.name)
                intent.putExtra("PET_BREED", horse.breeds.primary)
                intent.putExtra("PET_LOCATION", horse.location)
                intent.putExtra("PET_IMAGE_URL", horse.photos?.firstOrNull()?.medium)
                startActivity(intent)
            }
        )

        horseRecyclerView.adapter = horseAdapter

        horseViewModel = ViewModelProvider(this)[HorseViewModel::class.java]
        horseViewModel.getHorsesLiveData().observe(this) { horses ->
            if (horses.isNotEmpty()) {
                horseAdapter.updateHorses(horses)
            } else {
                Toast.makeText(this, "No horses available to display", Toast.LENGTH_SHORT).show()
            }
        }

        horseViewModel.fetchHorsesFromApi(
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
                    horseViewModel.searchHorsesByName(query)
                } else {
                    horseViewModel.fetchHorsesFromApi(
                        clientId = "FyK3sU03vVREgeUpu5IyxKoQEbL7X23ARVEZqQxMQN5zgTpDyD",
                        clientSecret = "PXgkzsUrJhcZKbMzIWWyIpvs2Ii99zyx58b2Wlc"
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}