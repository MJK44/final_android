package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tonydoumit_androidmidterm_petapp.R
import com.squareup.picasso.Picasso
import model.Animal

class PetAdapter(
    private var pets: List<Animal>,
    private val onFavoriteClick: (Animal) -> Unit,
    private val onItemClick: (Animal) -> Unit
) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    fun updatePets(newPets: List<Animal>) {
        pets = newPets
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false)
        return PetViewHolder(view, onFavoriteClick, onItemClick)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(pets[position])
    }

    override fun getItemCount(): Int = pets.size

    class PetViewHolder(
        view: View,
        private val onFavoriteClick: (Animal) -> Unit,
        private val onItemClick: (Animal) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val petName: TextView = view.findViewById(R.id.pet_name)
        private val petBreed: TextView = view.findViewById(R.id.pet_species_breed)
        private val petLocation: TextView = view.findViewById(R.id.pet_location)
        private val petImage: ImageView = view.findViewById(R.id.petImage)
        private val heartIcon: ImageView = view.findViewById(R.id.heart_icon)

        // Initialize block removed for clarity and direct handling in bind method

        fun bind(pet: Animal) {
            petName.text = pet.name
            petBreed.text = pet.breeds.primary
            petLocation.text = pet.location

            Picasso.get()
                .load(pet.photos?.firstOrNull()?.medium)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(petImage)

            heartIcon.isSelected = pet.isFavorited

            heartIcon.setOnClickListener {
                onFavoriteClick(pet)
                heartIcon.isSelected = !heartIcon.isSelected
            }

            itemView.setOnClickListener {
                onItemClick(pet)
            }
        }
    }
}