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

class CatAdapter(
    private var cats: List<Animal>,
    private val onFavoriteClick: (Animal) -> Unit,
    private val onItemClick: (Animal) -> Unit
) : RecyclerView.Adapter<CatAdapter.CatViewHolder>() {

    fun updateCats(newCats: List<Animal>) {
        cats = newCats
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cat, parent, false)
        return CatViewHolder(view, onFavoriteClick, onItemClick)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        holder.bind(cats[position])
    }

    override fun getItemCount(): Int = cats.size

    class CatViewHolder(
        view: View,
        private val onFavoriteClick: (Animal) -> Unit,
        private val onItemClick: (Animal) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val catName: TextView = view.findViewById(R.id.cat_name)
        private val catBreed: TextView = view.findViewById(R.id.cat_breed)
        private val catLocation: TextView = view.findViewById(R.id.cat_location)
        private val catImage: ImageView = view.findViewById(R.id.cat_image)
        private val heartIcon: ImageView = view.findViewById(R.id.heart_icon)

        fun bind(cat: Animal) {
            catName.text = cat.name
            catBreed.text = cat.breeds.primary
            catLocation.text = cat.location

            Picasso.get()
                .load(cat.photos?.firstOrNull()?.medium)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(catImage)

            heartIcon.isSelected = cat.isFavorited

            heartIcon.setOnClickListener {
                onFavoriteClick(cat)
                heartIcon.isSelected = !heartIcon.isSelected
            }

            itemView.setOnClickListener {
                onItemClick(cat)
            }
        }
    }
}