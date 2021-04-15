package yashas.assignment.foodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_cart.view.*
import yashas.assignment.foodie.R
import yashas.assignment.foodie.databaseorder.OrderEntity

class CartRecyclerAdapter(val context: Context, val orderList: List<OrderEntity>): RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>(){
    class CartViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtCost: TextView = view.findViewById(R.id.txtCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = orderList[position]
        holder.txtItemName.text = item.name
        holder.txtCost.text = item.cost.toString()
    }
}