package yashas.assignment.foodie.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import yashas.assignment.foodie.database_restaurant.DatabaseFunctionsFavourites
import yashas.assignment.foodie.R
import yashas.assignment.foodie.activity.MenuItems
import yashas.assignment.foodie.database_restaurant.RestaurantEntity
import yashas.assignment.foodie.model.Restaurant

class HomeRecyclerAdapter(val context: Context, val restaurantList: ArrayList<Restaurant>): RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(),
    DatabaseFunctionsFavourites {
    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imgResCover: ImageView = view.findViewById(R.id.imgResCover)
        val txtResName : TextView = view.findViewById(R.id.txtResName)
        val txtResPrice: TextView = view.findViewById(R.id.txtResPrice)
        val txtResRating: TextView = view.findViewById(R.id.txtResRating)
        val imgFavourite: ImageButton = view.findViewById(R.id.btnImgFavourite)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val res = restaurantList[position]
        val text = res.cost_for_one
        val id = res.id
        val imageUrl = res.image_url
        holder.txtResName.text = res.name
        holder.txtResPrice.text = "₹$text/person"
        holder.txtResRating.text = res.rating
        Picasso.get().load(imageUrl).error(R.drawable.profile).into(holder.imgResCover)
        val restaurantEntity = RestaurantEntity(
            id,
            holder.txtResName.text.toString(),
            holder.txtResRating.text.toString(),
            holder.txtResPrice.text.toString(),
            imageUrl
        )
        if(!DatabaseFunctionsFavourites.DBASyncTask(context, restaurantEntity, 1 ).execute().get()){
            holder.imgFavourite.setImageResource(R.drawable.favourite_border)
        }
        else{
            holder.imgFavourite.setImageResource(R.drawable.favourite)
        }


        holder.imgFavourite.setOnClickListener {
            if(!DatabaseFunctionsFavourites.DBASyncTask(context, restaurantEntity, 1).execute().get()){
                val checkFav = DatabaseFunctionsFavourites.DBASyncTask(context, restaurantEntity, 2).execute()
                val result =  checkFav.get()
                if(result){
                    Toast.makeText(
                        context,
                        "Added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.imgFavourite.setImageResource(R.drawable.favourite)
                }
                else{
                    Toast.makeText(
                        context,
                        "Some unexpected Error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else{
                val checkFav = DatabaseFunctionsFavourites.DBASyncTask(context, restaurantEntity, 3).execute()
                val result =  checkFav.get()
                if(result){
                    Toast.makeText(
                        context,
                        "Removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.imgFavourite.setImageResource(R.drawable.favourite_border)
                }
                else{
                    Toast.makeText(
                        context,
                        "Some unexpected Error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        holder.llContent.setOnClickListener {
            val intent = Intent(context, MenuItems::class.java)
            intent.putExtra("restaurant_id", id)
            intent.putExtra("restaurant_name", res.name)
            context.startActivity(intent)
        }

    }
}

