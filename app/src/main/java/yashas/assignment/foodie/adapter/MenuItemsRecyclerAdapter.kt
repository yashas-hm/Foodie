package yashas.assignment.foodie.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import yashas.assignment.foodie.R
import yashas.assignment.foodie.databaseorder.OrderDatabase
import yashas.assignment.foodie.databaseorder.OrderEntity
import yashas.assignment.foodie.model.RestaurantMenu

class MenuItemsRecyclerAdapter(val context: Context, val menuList: ArrayList<RestaurantMenu>, val listner: OnItemClickListener):
    RecyclerView.Adapter<MenuItemsRecyclerAdapter.MenuItemsViewHolder>() {
    class MenuItemsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFoodId: TextView = view.findViewById(R.id.txtFoodId)
        val txtFoodItem: TextView = view.findViewById(R.id.txtFoodItem)
        val txtFoodCost: TextView = view.findViewById(R.id.txtFoodCost)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_menu_items, parent, false)
        return MenuItemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    interface OnItemClickListener{
        fun cartCheck()
    }

    override fun onBindViewHolder(holder: MenuItemsViewHolder, position: Int) {
        val menu = menuList[position]
        holder.txtFoodItem.text = menu.name
        holder.txtFoodCost.text = "Rs. ${menu.cost}"
        holder.txtFoodId.text = (position + 1).toString()
        holder.btnAdd.setOnClickListener {
            val cart = OrderEntity(
                menu.itemId,
                menu.name,
                menu.cost,
                menu.resId
            )
            if (!DBCASyncTask(context, cart, 3).execute().get()) {
                val check = DBCASyncTask(context, cart, 1).execute()
                val result = check.get()
                if (result) {
                    holder.btnAdd.text = "Remove"
                    val favColor =
                        ContextCompat.getColor(context, R.color.colorAccentDark)
                    holder.btnAdd.setBackgroundColor(favColor)
                    listner.cartCheck()
                } else {
                    Toast.makeText(
                        context,
                        "Some Error Occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else{
                val check = DBCASyncTask(context, cart, 2).execute()
                val result = check.get()
                if (result) {
                    holder.btnAdd.text = "Add"
                    val favColor =
                        ContextCompat.getColor(context, R.color.colorAccent)
                    holder.btnAdd.setBackgroundColor(favColor)
                    listner.cartCheck()
                } else {
                    Toast.makeText(
                        context,
                        "Some Error Occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    class DBCASyncTask(val context: Context, val orderEntity: OrderEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "cart-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().addToCart(orderEntity)
                    db.close()
                    return true
                }
                2 -> {
                    db.orderDao().removeFromCart(orderEntity)
                    db.close()
                    return true
                }
                3 -> {
                    val check: OrderEntity? = db.orderDao().getById(orderEntity.food_item_id)
                    db.close()
                    return check != null
                }
                4 -> {
                    val cart = db.orderDao().getAllFoodId()
                    val size = cart.size
                    return size==0
                }
            }
            return false
        }
    }
}
