package yashas.assignment.foodie.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import yashas.assignment.foodie.R
import yashas.assignment.foodie.activity.MenuItems
import yashas.assignment.foodie.database_restaurant.RestaurantEntity

class FavouriteRecyclerAdapter(val context: Context, val favouriteList: List<RestaurantEntity>): RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>(){
    class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imgResCover: ImageView = view.findViewById(R.id.imgFResCover)
        val txtResName : TextView = view.findViewById(R.id.txtFResName)
        val txtResPrice: TextView = view.findViewById(R.id.txtFResPrice)
        val txtResRating: TextView = view.findViewById(R.id.txtFResRating)
        val imgFavourite: ImageButton = view.findViewById(R.id.btnImgFFavourite)
        val Fllcontent: LinearLayout = view.findViewById(R.id.FllContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favourite, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favouriteList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val favRes = favouriteList[position]
        holder.txtResName.text = favRes.name
        holder.txtResPrice.text = favRes.cost_for_one
        holder.txtResRating.text = favRes.rating
        Picasso.get().load(favRes.image_url).error(R.drawable.profile).into(holder.imgResCover)

        holder.Fllcontent.setOnClickListener {
            val intent = Intent(context, MenuItems::class.java)
            intent.putExtra("restaurant_id", favRes.id)
            intent.putExtra("restaurant_name", favRes.name)
            context.startActivity(intent)
        }
    }
}