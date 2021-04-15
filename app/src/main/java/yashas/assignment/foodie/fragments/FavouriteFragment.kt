package yashas.assignment.foodie.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import yashas.assignment.foodie.R
import yashas.assignment.foodie.adapter.FavouriteRecyclerAdapter
import yashas.assignment.foodie.database_restaurant.RestaurantDatabase
import yashas.assignment.foodie.database_restaurant.RestaurantEntity

class FavouriteFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    var dbRestaurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        layoutManager = LinearLayoutManager(activity)

        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE

        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()

        if(activity!=null){
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbRestaurantList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }

        return view
    }
    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity>{
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()
            return db.restaurantDao().getAllRestaurants()
        }
    }
}
