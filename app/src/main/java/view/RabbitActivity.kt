package view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tonydoumit_androidmidterm_petapp.R
import adapter.RabbitAdapter
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import viewmodel.RabbitViewModel

class RabbitActivity : AppCompatActivity() {
    private lateinit var rabbitRecyclerView: RecyclerView
    private lateinit var rabbitAdapter: RabbitAdapter
    private lateinit var rabbitViewModel: RabbitViewModel
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rabbit)

        setupIconListeners()
        setupRecyclerViewAndFetchRabbits()
        setupSearchBar()
    }

    private fun setupIconListeners() {
        findViewById<View>(R.id.icon_cat).setOnClickListener {
            startActivity(Intent(this, CatActivity::class.java))
        }
        findViewById<View>(R.id.icon_dog).setOnClickListener {
            startActivity(Intent(this, DogActivity::class.java))
        }
        findViewById<View>(R.id.icon_horse).setOnClickListener {
            startActivity(Intent(this, HorseActivity::class.java))
        }
        findViewById<View>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<View>(R.id.icon_others).setOnClickListener {
            startActivity(Intent(this, AddedPetListActivity::class.java))
        }
    }

    private fun setupRecyclerViewAndFetchRabbits() {
        rabbitRecyclerView = findViewById(R.id.rabbit_list)
        rabbitRecyclerView.layoutManager = LinearLayoutManager(this)

        // Create an adapter instance and pass a lambda for handling favorite clicks
        rabbitAdapter = RabbitAdapter(
            rabbits = listOf(),
            onFavoriteClick = { rabbit ->
                rabbitViewModel.toggleFavorite(rabbit)
            },
            onItemClick = { rabbit ->
                val intent = Intent(this, PetDetailsActivity::class.java)
                intent.putExtra("PET_NAME", rabbit.name)
                intent.putExtra("PET_BREED", rabbit.breeds.primary)
                intent.putExtra("PET_LOCATION", rabbit.location)
                intent.putExtra("PET_IMAGE_URL", rabbit.photos?.firstOrNull()?.medium)
                startActivity(intent)
            }
        )

        rabbitRecyclerView.adapter = rabbitAdapter

        rabbitViewModel = ViewModelProvider(this)[RabbitViewModel::class.java]

        rabbitViewModel.getRabbitsLiveData().observe(this) { rabbits ->
            if (rabbits.isNotEmpty()) {
                rabbitAdapter.updateRabbits(rabbits)
            } else {
                Toast.makeText(this, "No rabbits available to display", Toast.LENGTH_SHORT).show()
            }
        }

        rabbitViewModel.fetchRabbitsFromApi(
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
                    rabbitViewModel.searchRabbitsByName(query)
                } else {
                    rabbitViewModel.fetchRabbitsFromApi(
                        clientId = "FyK3sU03vVREgeUpu5IyxKoQEbL7X23ARVEZqQxMQN5zgTpDyD",
                        clientSecret = "PXgkzsUrJhcZKbMzIWWyIpvs2Ii99zyx58sB2Wlc"
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}