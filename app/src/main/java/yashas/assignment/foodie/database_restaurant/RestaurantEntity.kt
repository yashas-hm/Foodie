package yashas.assignment.foodie.database_restaurant

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant")
data class RestaurantEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "res_name") val name: String,
    @ColumnInfo(name = "res_rating") val rating: String,
    @ColumnInfo(name = "res_cost_for_one") val cost_for_one: String,
    @ColumnInfo(name = "res_image_url") val image_url: String
)