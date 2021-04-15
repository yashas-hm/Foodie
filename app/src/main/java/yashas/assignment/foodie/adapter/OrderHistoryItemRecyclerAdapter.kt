package yashas.assignment.foodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import yashas.assignment.foodie.R
import yashas.assignment.foodie.model.OrderHistoryItems
import yashas.assignment.foodie.model.RestaurantMenu

class OrderHistoryItemRecyclerAdapter(val context: Context, val itemList: ArrayList<OrderHistoryItems>): RecyclerView.Adapter<OrderHistoryItemRecyclerAdapter.OrderHistoryItemViewHolder>(){
    class OrderHistoryItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtCost: TextView = view.findViewById(R.id.txtCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart, parent, false)
        return OrderHistoryItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.txtItemName.text = item.name
        holder.txtCost.text = "Rs. ${item.cost}"
    }
}