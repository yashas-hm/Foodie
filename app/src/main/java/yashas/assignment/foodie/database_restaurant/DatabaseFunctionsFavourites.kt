package yashas.assignment.foodie.database_restaurant

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

interface   DatabaseFunctionsFavourites {
    class DBASyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        /*
        Mode 1  ->  Check the DB if restaurant is favourite or not
        Mode 2  ->  Save restaurant into DB as favourite
        Mode 3  ->  Remove favourite restaurant
        */

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return restaurant != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}