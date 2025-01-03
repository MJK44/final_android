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

class RabbitAdapter(
    private var rabbits: List<Animal>,
    private val onFavoriteClick: (Animal) -> Unit,
    private val onItemClick: (Animal) -> Unit
) : RecyclerView.Adapter<RabbitAdapter.RabbitViewHolder>() {

    fun updateRabbits(newRabbits: List<Animal>) {
        rabbits = newRabbits
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RabbitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rabbit, parent, false)
        return RabbitViewHolder(view, onFavoriteClick, onItemClick)
    }

    override fun onBindViewHolder(holder: RabbitViewHolder, position: Int) {
        holder.bind(rabbits[position])
    }

    override fun getItemCount(): Int = rabbits.size

    class RabbitViewHolder(
        view: View,
        private val onFavoriteClick: (Animal) -> Unit,
        private val onItemClick: (Animal) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val rabbitName: TextView = view.findViewById(R.id.rabbit_name)
        private val rabbitBreed: TextView = view.findViewById(R.id.rabbit_breed)
        private val rabbitLocation: TextView = view.findViewById(R.id.rabbit_location)
        private val rabbitImage: ImageView = view.findViewById(R.id.rabbit_image)
        private val heartIcon: ImageView = view.findViewById(R.id.heart_icon)

        fun bind(rabbit: Animal) {
            rabbitName.text = rabbit.name
            rabbitBreed.text = rabbit.breeds.primary
            rabbitLocation.text = rabbit.location

            Picasso.get()
                .load(rabbit.photos?.firstOrNull()?.medium)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(rabbitImage)

            heartIcon.isSelected = rabbit.isFavorited

            heartIcon.setOnClickListener {
                onFavoriteClick(rabbit)
                heartIcon.isSelected = !heartIcon.isSelected
            }

            itemView.setOnClickListener {
                onItemClick(rabbit)
            }
        }
    }
}