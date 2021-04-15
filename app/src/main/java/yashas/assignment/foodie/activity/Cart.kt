package yashas.assignment.foodie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import yashas.assignment.foodie.R
import yashas.assignment.foodie.adapter.CartRecyclerAdapter
import yashas.assignment.foodie.databaseorder.OrderDatabase
import yashas.assignment.foodie.databaseorder.OrderEntity
import yashas.assignment.foodie.model.StoreId
import yashas.assignment.foodie.util.APIKeys
import yashas.assignment.foodie.util.ConnectionManager

class Cart : AppCompatActivity() {

    private lateinit var btnPlaceOrder: Button
    private lateinit var txtResName: TextView
    private lateinit var recyclerCart: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: CartRecyclerAdapter
    private var foodItemList = listOf<StoreId>()
    private var orderList = listOf<OrderEntity>()
    private lateinit var sum: yashas.assignment.foodie.model.Total
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var restaurantSharedPreferences: SharedPreferences


    var name: String = "restaurant"
    var id: String = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        txtResName = findViewById(R.id.txtResName)
        recyclerCart = findViewById(R.id.recyclerCart)
        layoutManager = LinearLayoutManager(this@Cart)
        sharedPreferences =
            getSharedPreferences(getString(R.string.login_preference), Context.MODE_PRIVATE)
        title = "Cart"
        restaurantSharedPreferences = getSharedPreferences("restaurant_id", Context.MODE_PRIVATE)
        if (intent != null) {
            name = intent.getStringExtra("restaurant_name")!!
            id = intent.getStringExtra("restaurant_id")!!
        } else {
            Toast.makeText(
                this@Cart,
                "Some Error Occurred",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
        if (id=="1234" || name == "z") {
            finish()
            Toast.makeText(
                this@Cart,
                "some unexpected error occurred",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(this@Cart, MainActivity::class.java))
            finish()
        }
        sum = Total(this).execute().get()
        btnPlaceOrder.text = "Place Order(Total ${sum.total})"
        txtResName.text = "Ordering from : $name"
        val userId: String = sharedPreferences.getString("user_id", "1")!!

        orderList = CartItems(this@Cart).execute().get()
        recyclerAdapter = CartRecyclerAdapter(this@Cart, orderList)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager

        foodItemList = RetrieveAllItems(this@Cart).execute().get()
        val queue = Volley.newRequestQueue(this@Cart)
        val url = APIKeys.placeOrderUrl

        val itemArray = JSONArray()
        for(i in foodItemList.indices){
            val item = JSONObject()
            item.put("food_item_id", foodItemList[i].food_item_id)
            itemArray.put(item)
        }

        val jsonParams =  JSONObject()
        jsonParams.put("user_id", userId)
        jsonParams.put("restaurant_id", id)
        jsonParams.put("total_cost", sum.total.toString())
        jsonParams.put("food", itemArray)

        btnPlaceOrder.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this@Cart)) {
                val jsonRequest = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    jsonParams,
                    Response.Listener {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        try {
                            if (success) {
                                val intent = Intent(this@Cart, Success::class.java)
                                EmptyCart(this@Cart)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@Cart,
                                    "Some error  server Occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }catch (e: JSONException){
                            Toast.makeText(
                                this@Cart,
                                "Some error Occurred json",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@Cart,
                            "Some error Occurred volley ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = APIKeys.auth
                        return headers
                    }
                }
                queue.add(jsonRequest)
            }
            else {
                val dialog = AlertDialog.Builder(this@Cart)
                dialog.setTitle("Error")
                dialog.setMessage("Internet connection not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    finish()
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    class EmptyCart(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        private val db = Room.databaseBuilder(context, OrderDatabase::class.java, "cart-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().emptyCart()
            db.close()
            return true
        }
    }

    class RetrieveAllItems(val context: Context) : AsyncTask<Void, Void, List<StoreId>>() {
        private val db = Room.databaseBuilder(context, OrderDatabase::class.java, "cart-db").build()
        override fun doInBackground(vararg params: Void?): List<StoreId> {
            return db.orderDao().getAllFoodId()
        }
    }

    class Total(val context: Context) : AsyncTask<Void, Void, yashas.assignment.foodie.model.Total>() {
        private val db = Room.databaseBuilder(context, OrderDatabase::class.java, "cart-db").build()
        override fun doInBackground(vararg params: Void?): yashas.assignment.foodie.model.Total {
            return db.orderDao().total()
        }
    }

    class CartItems(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        private val db = Room.databaseBuilder(context, OrderDatabase::class.java, "cart-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllItems()
        }
    }
}
