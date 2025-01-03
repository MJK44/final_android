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

class DogAdapter(
    private var dogs: List<Animal>,
    private val onFavoriteClick: (Animal) -> Unit,
    private val onItemClick: (Animal) -> Unit
) : RecyclerView.Adapter<DogAdapter.DogViewHolder>() {

    fun updateDogs(newDogs: List<Animal>) {
        dogs = newDogs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dog, parent, false)
        return DogViewHolder(view, onFavoriteClick, onItemClick)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bind(dogs[position])
    }

    override fun getItemCount(): Int = dogs.size

    class DogViewHolder(
        view: View,
        private val onFavoriteClick: (Animal) -> Unit,
        private val onItemClick: (Animal) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val dogName: TextView = view.findViewById(R.id.dog_name)
        private val dogBreed: TextView = view.findViewById(R.id.dog_breed)
        private val dogLocation: TextView = view.findViewById(R.id.dog_location)
        private val dogImage: ImageView = view.findViewById(R.id.dog_image)
        private val heartIcon: ImageView = view.findViewById(R.id.heart_icon)

        fun bind(dog: Animal) {
            dogName.text = dog.name
            dogBreed.text = dog.breeds.primary
            dogLocation.text = dog.location

            Picasso.get()
                .load(dog.photos?.firstOrNull()?.medium)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(dogImage)

            heartIcon.isSelected = dog.isFavorited

            heartIcon.setOnClickListener {
                onFavoriteClick(dog)
                heartIcon.isSelected = !heartIcon.isSelected
            }

            itemView.setOnClickListener {
                onItemClick(dog)
            }
        }
    }
}