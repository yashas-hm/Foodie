package yashas.assignment.foodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import yashas.assignment.foodie.R
import yashas.assignment.foodie.model.OrderHistory
import yashas.assignment.foodie.model.OrderHistoryItems
import yashas.assignment.foodie.model.RestaurantMenu

class OrderHistoryRecyclerAdapter(
    val context: Context,
    val resName: ArrayList<OrderHistory>
) : RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResName: TextView = view.findViewById(R.id.txtResName)
        val txtTime: TextView = view.findViewById(R.id.txtTime)
        val recyclerItems: RecyclerView = view.findViewById(R.id.recyclerItems)
        val txtCost: TextView = view.findViewById(R.id.txtCost)
        lateinit var recyclerAdapter: OrderHistoryItemRecyclerAdapter
        lateinit var layoutManager: RecyclerView.LayoutManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return resName.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val item = resName[position]
        holder.txtResName.text = item.restaurant_name
        holder.txtTime.text = item.order_placed_at
        holder.txtCost.text = "Total: ${item.cost}"
        val foodItemList = ArrayList<OrderHistoryItems>()
        for(i in 0 until item.food.length()){
            val foodJson = item.food.getJSONObject(i)
            foodItemList.add(
                OrderHistoryItems(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost").toInt()
                )
            )
        }
        holder.recyclerAdapter = OrderHistoryItemRecyclerAdapter(context, foodItemList)
        holder.layoutManager = LinearLayoutManager(context)
        holder.recyclerItems.adapter = holder.recyclerAdapter
        holder.recyclerItems.layoutManager = holder.layoutManager
    }
}