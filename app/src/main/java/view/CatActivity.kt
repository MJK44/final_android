package view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tonydoumit_androidmidterm_petapp.R
import adapter.CatAdapter
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import viewmodel.CatViewModel

class CatActivity : AppCompatActivity() {
    private lateinit var catRecyclerView: RecyclerView
    private lateinit var catAdapter: CatAdapter
    private lateinit var catViewModel: CatViewModel
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cat)

        setupIconListeners()
        setupRecyclerViewAndFetchCats()
        setupSearchBar()
    }

    private fun setupIconListeners() {
        findViewById<View>(R.id.icon_dog).setOnClickListener {
            startActivity(Intent(this, DogActivity::class.java))
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

    private fun setupRecyclerViewAndFetchCats() {
        catRecyclerView = findViewById(R.id.cat_list)
        catRecyclerView.layoutManager = LinearLayoutManager(this)

        catAdapter = CatAdapter(
            cats = listOf(),
            onFavoriteClick = { cat ->
                catViewModel.toggleFavorite(cat)
            },
            onItemClick = { cat ->
                val intent = Intent(this, PetDetailsActivity::class.java)
                intent.putExtra("PET_NAME", cat.name)
                intent.putExtra("PET_BREED", cat.breeds.primary)
                intent.putExtra("PET_LOCATION", cat.location)
                intent.putExtra("PET_IMAGE_URL", cat.photos?.firstOrNull()?.medium)
                startActivity(intent)
            }
        )

        catRecyclerView.adapter = catAdapter

        catViewModel = ViewModelProvider(this)[CatViewModel::class.java]
        catViewModel.getCatsLiveData().observe(this) { cats ->
            if (cats.isNotEmpty()) {
                catAdapter.updateCats(cats)
            } else {
                Toast.makeText(this, "No cats available to display", Toast.LENGTH_SHORT).show()
            }
        }

        catViewModel.fetchCatsFromApi(
            "FyK3sU03vVREgeUpu5IyxKoQEbL7X23ARVEZqQxMQN5zgTpDyD",
            "PXgkzsUrJhcZKbMzIWWyIpvs2Ii99zyx58sB2Wlc"
        )
    }

    private fun setupSearchBar() {
        searchBar = findViewById(R.id.search_bar)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    catViewModel.searchCatsByName(query)
                } else {
                    catViewModel.fetchCatsFromApi(
                        clientId = "FyK3sU03vVREgeUpu5IyxKoQEbL7X23ARVEZqQxMQN5zgTpDyD",
                        clientSecret = "PXgkzsUrJhcZKbMzIWWyIpvs2Ii99zyx58sB2Wlc"
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}