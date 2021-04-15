package yashas.assignment.foodie.databaseorder

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrderEntity::class], version = 2)
abstract class OrderDatabase: RoomDatabase(){
    abstract fun orderDao(): OrderDao
}