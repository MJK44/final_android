package fragments

import PetViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tonydoumit_androidmidterm_petapp.R
import adapter.PetAdapter


class WelcomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var petAdapter: PetAdapter
    private lateinit var petViewModel: PetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.pet_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        petAdapter = PetAdapter(
            pets = listOf(),
            onFavoriteClick = { pet ->
                petViewModel.toggleFavorite(pet)
            },
            onItemClick = { pet ->
                // Handle pet click, e.g., navigate to a details fragment or activity
                Toast.makeText(context, "Clicked on ${pet.name}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = petAdapter

        petViewModel = ViewModelProvider(requireActivity())[PetViewModel::class.java]
        petViewModel.getPetsLiveData().observe(viewLifecycleOwner) { pets ->
            if (pets.isNotEmpty()) {
                petAdapter.updatePets(pets)
            } else {
                Toast.makeText(context, "No pets available to display", Toast.LENGTH_SHORT).show()
            }
        }

        petViewModel.fetchPetsFromApi("FyK3sU03vVREgeUpu5IyxKoQEbL7X23ARVEZqQxMQN5zgTpDyD", "PXgkzsUrJhcZKbMzIWWyIpvs2Ii99zyx58sB2Wlc")
    }
}