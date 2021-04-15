package yashas.assignment.foodie.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import yashas.assignment.foodie.R
import yashas.assignment.foodie.adapter.HomeRecyclerAdapter
import yashas.assignment.foodie.model.Restaurant
import yashas.assignment.foodie.util.APIKeys
import yashas.assignment.foodie.util.ConnectionManager
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    private lateinit var recyclerHome: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val restaurantList = arrayListOf<Restaurant>()
    private lateinit var recyclerAdapter: HomeRecyclerAdapter
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences

    private val ratingComparator = Comparator<Restaurant> { Restaurant1, Restaurant2 ->
        if(Restaurant1.rating.compareTo(Restaurant2.rating, true)==0){
            Restaurant1.name.compareTo(Restaurant2.name, true)
        }
        else{
            Restaurant1.rating.compareTo(Restaurant2.rating, true)
        }
    }

    private val nameComparator = Comparator<Restaurant> { Restaurant1, Restaurant2 ->
        if(Restaurant1.name.compareTo(Restaurant2.name, true)==0){
            Restaurant1.rating.compareTo(Restaurant2.rating, true)
        }
        else{
            Restaurant1.name.compareTo(Restaurant2.name, true)
        }
    }

    private val priceComparator = Comparator<Restaurant>{ Restaurant1, Restaurant2 ->
        if(Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true)==0){
            Restaurant1.rating.compareTo(Restaurant2.rating, true)
        }
        else{
            Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        sharedPreferences = context!!.getSharedPreferences("restaurant_id", Context.MODE_PRIVATE)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        val queue = Volley.newRequestQueue(activity as Context)
        val url = APIKeys.restaurantResultUrl

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener {
                    val main = it.getJSONObject("data")
                    val success: Boolean = main.getBoolean("success")
                    try {
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val data = main.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantList.add(restaurantObject)
                                sharedPreferences.edit().putString("restaurant_id", restaurantJsonObject.getString("id")).apply()
                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, restaurantList)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "some error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "some error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {
                    if(activity!=null) {
                        Toast.makeText(
                            activity as Context,
                            "volley occured!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = APIKeys.auth
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.sortRating -> {
                Collections.sort(restaurantList, ratingComparator)
                restaurantList.reverse()
            }

            R.id.sortNameAsc -> {
                Collections.sort(restaurantList, nameComparator)
            }

            R.id.sortPrice -> {
                Collections.sort(restaurantList, priceComparator)
                restaurantList.reverse()
            }
        }

        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}

