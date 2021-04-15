package yashas.assignment.foodie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import yashas.assignment.foodie.R
import yashas.assignment.foodie.adapter.MenuItemsRecyclerAdapter
import yashas.assignment.foodie.database_restaurant.*
import yashas.assignment.foodie.databaseorder.OrderDatabase
import yashas.assignment.foodie.model.RestaurantMenu
import yashas.assignment.foodie.util.APIKeys
import yashas.assignment.foodie.util.ConnectionManager

class MenuItems : AppCompatActivity(), DatabaseFunctionsFavourites{
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imgFavourite: ImageView
    private lateinit var btnProceedToCart: Button
    private lateinit var recyclerMenuItems: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val menuList = arrayListOf<RestaurantMenu>()
    private lateinit var recyclerAdapter: MenuItemsRecyclerAdapter
    private var id: String? = "9873"
    private var name: String? = "z"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_items)

        if(!DBCASynctask(this@MenuItems, 2).execute().get()) {
            DBCASynctask(this@MenuItems, 1).execute()
        }

        imgFavourite = findViewById(R.id.imgFavourite)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        recyclerMenuItems = findViewById(R.id.recyclerMenuItems)
        layoutManager = LinearLayoutManager(this@MenuItems)
        sharedPreferences = getSharedPreferences("restaurant_id", Context.MODE_PRIVATE)
        btnProceedToCart.visibility = View.GONE

        if (intent != null) {
            id = intent.getStringExtra("restaurant_id")
            name = intent.getStringExtra("restaurant_name")
        }
        else {
            Toast.makeText(
                this@MenuItems,
                "Some Error Occurred",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        if (id == "9873" || name=="z") {
            finish()
            Toast.makeText(
                this@MenuItems,
                "some unexpected error occurred",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(this@MenuItems, MainActivity::class.java))
            finish()
        }

        title = "Menu of $name"

        val restaurantEntity = RestaurantEntity(
            id.toString(),
            null.toString(),
            null.toString(),
            null.toString(),
            null.toString()
        )
        if (!DatabaseFunctionsFavourites.DBASyncTask(this@MenuItems, restaurantEntity, 1).execute().get()) {
            imgFavourite.setImageResource(R.drawable.favourite_border)
        } else {
            imgFavourite.setImageResource(R.drawable.favourite)
        }

        btnProceedToCart.setOnClickListener {
            val intent = Intent(this@MenuItems, Cart::class.java)
            intent.putExtra("restaurant_id", id)
            intent.putExtra("restaurant_name", name)
            startActivity(intent)
        }

        val queue = Volley.newRequestQueue(this@MenuItems)
        val url = APIKeys.getResultWithId(id!!)

        if (ConnectionManager().checkConnectivity(this@MenuItems)) {
            val jsonRequest = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener {
                    val mainObject = it.getJSONObject("data")
                    val success = mainObject.getBoolean("success")
                    try {
                        if(success){
                            val data = mainObject.getJSONArray("data")
                            for(i in 0 until data.length()){
                                val menuJsonObject = data.getJSONObject(i)
                                val list = RestaurantMenu(
                                    menuJsonObject.getString("id"),
                                    menuJsonObject.getString("name"),
                                    menuJsonObject.getString("cost_for_one").toInt(),
                                    menuJsonObject.getString("restaurant_id")
                                )
                                menuList.add(list)
                                recyclerAdapter = MenuItemsRecyclerAdapter(this@MenuItems, menuList, object: MenuItemsRecyclerAdapter.OnItemClickListener{
                                    override fun cartCheck() {
                                        if(DBCASynctask(this@MenuItems, 2).execute().get())
                                            btnProceedToCart.visibility = View.GONE
                                        else
                                            btnProceedToCart.visibility = View.VISIBLE
                                    }
                                })
                                recyclerMenuItems.adapter = recyclerAdapter
                                recyclerMenuItems.layoutManager = layoutManager
                            }
                        }
                        else{
                            Toast.makeText(
                                this@MenuItems,
                                "some error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }catch (e: JSONException){
                        Toast.makeText(
                            this@MenuItems,
                            "some error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(
                        this@MenuItems,
                        "some error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = APIKeys.auth
                    return headers
                }
            }
            queue.add(jsonRequest)
        }
        else{
            val dialog = AlertDialog.Builder(this@MenuItems)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this@MenuItems)
            }
            dialog.create()
            dialog.show()
        }
    }


    override fun onBackPressed() {
        if(!DBCASynctask(this@MenuItems, 2).execute().get()){
            val dialog = AlertDialog.Builder(this@MenuItems)
            dialog.setTitle("Alert")
            dialog.setMessage("Going back will erase items from cart")
            dialog.setPositiveButton("Stay") { _, _ ->
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                DBCASynctask(this@MenuItems, 1).execute()
                startActivity(Intent(this@MenuItems, MainActivity::class.java))
                finish()
            }
            dialog.create()
            dialog.show()
        }
        else{
            super.onBackPressed()
            finish()
        }
    }

     class DBCASynctask(val context: Context, private val mode: Int): AsyncTask<Void, Void, Boolean>(){
        private val db = Room.databaseBuilder(context, OrderDatabase::class.java, "cart-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().emptyCart()
                    db.close()
                }
                2 -> {
                    val cart = db.orderDao().getAllFoodId()
                    val size = cart.size
                    return size==0
                }
            }
            return false
        }
     }
}
