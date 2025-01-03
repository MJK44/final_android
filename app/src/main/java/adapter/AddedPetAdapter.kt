package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tonydoumit_androidmidterm_petapp.R

/*
class AddedPetAdapter(private var originalPets: List<Map<String, String>>) :
    RecyclerView.Adapter<AddedPetAdapter.AddedPetViewHolder>() {

    private var displayedPets: List<Map<String, String>> = originalPets

    class AddedPetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petName: TextView = view.findViewById(R.id.pet_name)
        val petAge: TextView = view.findViewById(R.id.pet_age)
        val petType: TextView = view.findViewById(R.id.pet_type)
        val petBreed: TextView = view.findViewById(R.id.pet_breed)
        val petLocation: TextView = view.findViewById(R.id.pet_location)
        val petDescription: TextView = view.findViewById(R.id.pet_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddedPetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.added_pets, parent, false)
        return AddedPetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddedPetViewHolder, position: Int) {
        val pet = displayedPets[position]
        holder.petName.text = pet["name"]
        holder.petAge.text = "Age: ${pet["age"]}"
        holder.petType.text = "Type: ${pet["type"]}"
        holder.petBreed.text = "Breed: ${pet["breed"]}"
        holder.petLocation.text = "Location: ${pet["location"]}"
        holder.petDescription.text = "Description: ${pet["description"]}"
    }

    override fun getItemCount(): Int = displayedPets.size

    fun updateFilteredList(filteredPets: List<Map<String, String>>) {
        displayedPets = filteredPets
        notifyDataSetChanged()
    }
}*/
class AddedPetAdapter(
    private var pets: List<Map<String, String>>,
    private val onAdoptClick: (Map<String, String>) -> Unit
) : RecyclerView.Adapter<AddedPetAdapter.AddedPetViewHolder>() {

    inner class AddedPetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petName: TextView = view.findViewById(R.id.pet_name)
        val petAge: TextView = view.findViewById(R.id.pet_age)
        val petType: TextView = view.findViewById(R.id.pet_type)
        val petBreed: TextView = view.findViewById(R.id.pet_breed)
        val petLocation: TextView = view.findViewById(R.id.pet_location)
        val petDescription: TextView = view.findViewById(R.id.pet_description)
        val adoptButton: Button = view.findViewById(R.id.button_adopt_me)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddedPetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.added_pets, parent, false)
        return AddedPetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddedPetViewHolder, position: Int) {
        val pet = pets[position]
        holder.petName.text = pet["name"]
        holder.petAge.text = "Age: ${pet["age"]}"
        holder.petType.text = "Type: ${pet["type"]}"
        holder.petBreed.text = "Breed: ${pet["breed"]}"
        holder.petLocation.text = "Location: ${pet["location"]}"
        holder.petDescription.text = "Description: ${pet["description"]}"

        // Handle the Adopt Me button click
        holder.adoptButton.setOnClickListener {
            onAdoptClick(pet)
        }
    }

    override fun getItemCount(): Int = pets.size

    fun updateList(newPets: List<Map<String, String>>) {
        pets = newPets
        notifyDataSetChanged()
    }
}