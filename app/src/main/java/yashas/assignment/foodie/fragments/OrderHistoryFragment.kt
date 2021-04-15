package yashas.assignment.foodie.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import yashas.assignment.foodie.R
import yashas.assignment.foodie.adapter.OrderHistoryRecyclerAdapter
import yashas.assignment.foodie.model.OrderHistory
import yashas.assignment.foodie.util.APIKeys
import yashas.assignment.foodie.util.ConnectionManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class OrderHistoryFragment : Fragment() {

    private lateinit var recyclerOrderHistory: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    private var orderHistory = arrayListOf<OrderHistory>()
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var textView: RelativeLayout
    private lateinit var text: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        layoutManager = LinearLayoutManager(activity as Context)
        text = view.findViewById(R.id.text)
        textView = view.findViewById(R.id.textView)
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE
        textView.visibility = View.GONE
        text.visibility = View.GONE

        sharedPreferences = context!!.getSharedPreferences(
            getString(R.string.login_preference),
            Context.MODE_PRIVATE
        )

        val id = sharedPreferences.getString("user_id", "3")
        val url = APIKeys.getOrderHistoryWithId(id!!)
        val queue = Volley.newRequestQueue(activity as Context)

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonRequest = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener {
                    val mainObject = it.getJSONObject("data")
                    val success = mainObject.getBoolean("success")
                    try{
                        if(success) {
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            val data = mainObject.getJSONArray("data")
                            if (data.length()==0) {
                                textView.visibility = View.VISIBLE
                                text.visibility = View.VISIBLE
                            }
                            else {
                                for (x in 0 until data.length()) {
                                    val orderJsonObject = data.getJSONObject(x)
                                    val time = orderJsonObject.getString("order_placed_at")
                                    val orderObject = OrderHistory(
                                        orderJsonObject.getString("order_id"),
                                        orderJsonObject.getString("restaurant_name"),
                                        orderJsonObject.getString("total_cost"),
                                        formatDate(time),
                                        orderJsonObject.getJSONArray("food_items")
                                    )
                                    orderHistory.add(orderObject)
                                    recyclerAdapter = OrderHistoryRecyclerAdapter(
                                        activity as Context,
                                        orderHistory
                                    )
                                    recyclerOrderHistory.adapter = recyclerAdapter
                                    recyclerOrderHistory.layoutManager = layoutManager
                                }
                            }
                        }
                        else{
                            Toast.makeText(
                                activity as Context,
                                "some error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }catch (e: JSONException) {
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
                            "volley error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

    private fun formatDate(dateString: String): String {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }

}
