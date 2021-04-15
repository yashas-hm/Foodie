package yashas.assignment.foodie.databaseorder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class OrderEntity(
    @PrimaryKey val food_item_id: String,
    @ColumnInfo(name = "name")val name: String,
    @ColumnInfo(name = "cost")val cost: Int,
    @ColumnInfo(name = "resId")val resId: String
)