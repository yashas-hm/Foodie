package yashas.assignment.foodie.model

import org.json.JSONArray

data class OrderHistory(
    val order_id: String,
    val restaurant_name: String,
    val cost: String,
    val order_placed_at: String,
    val food: JSONArray
)