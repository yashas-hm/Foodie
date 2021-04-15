package yashas.assignment.foodie.databaseorder

import androidx.room.*
import yashas.assignment.foodie.activity.Cart
import yashas.assignment.foodie.model.StoreId
import yashas.assignment.foodie.model.Total

@Dao
interface OrderDao{
    @Insert
    fun addToCart(orderEntity: OrderEntity)

    @Delete
    fun removeFromCart(orderEntity: OrderEntity)

    @Query("DELETE FROM cart")
    fun emptyCart()

    @Query("SELECT * FROM cart WHERE food_item_id = :id")
    fun getById(id: String): OrderEntity

    @Query("SELECT food_item_id FROM cart")
    fun getAllFoodId(): List<StoreId>

    @Query("SELECT * FROM cart")
    fun getAllItems(): List<OrderEntity>

    @Query("SELECT SUM(cost) as Total FROM cart")
    fun total(): Total

}