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

class HorseAdapter(
    private var horses: List<Animal>,
    private val onFavoriteClick: (Animal) -> Unit,
    private val onItemClick: (Animal) -> Unit
) : RecyclerView.Adapter<HorseAdapter.HorseViewHolder>() {

    fun updateHorses(newHorses: List<Animal>) {
        horses = newHorses
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horse, parent, false)
        return HorseViewHolder(view, onFavoriteClick, onItemClick)
    }

    override fun onBindViewHolder(holder: HorseViewHolder, position: Int) {
        holder.bind(horses[position])
    }

    override fun getItemCount(): Int = horses.size

    class HorseViewHolder(
        view: View,
        private val onFavoriteClick: (Animal) -> Unit,
        private val onItemClick: (Animal) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val horseName: TextView = view.findViewById(R.id.horse_name)
        private val horseBreed: TextView = view.findViewById(R.id.horse_breed)
        private val horseLocation: TextView = view.findViewById(R.id.horse_location)
        private val horseImage: ImageView = view.findViewById(R.id.horse_image)
        private val heartIcon: ImageView = view.findViewById(R.id.heart_icon)

        fun bind(horse: Animal) {
            horseName.text = horse.name
            horseBreed.text = horse.breeds.primary
            horseLocation.text = horse.location

            Picasso.get()
                .load(horse.photos?.firstOrNull()?.medium)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(horseImage)

            heartIcon.isSelected = horse.isFavorited

            heartIcon.setOnClickListener {
                onFavoriteClick(horse)
                heartIcon.isSelected = !heartIcon.isSelected
            }

            itemView.setOnClickListener {
                onItemClick(horse)
            }
        }
    }
}