package com.example.myapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.dao.UserDao
import com.example.myapplication.data.entity.Branch
import com.example.myapplication.data.entity.BranchProduct
import com.example.myapplication.data.entity.Category
import com.example.myapplication.data.entity.Order
import com.example.myapplication.data.entity.OrderItem
import com.example.myapplication.data.entity.Payment
import com.example.myapplication.data.entity.Product
import com.example.myapplication.data.entity.Role
import com.example.myapplication.data.entity.SalesSummary
import com.example.myapplication.data.entity.User
import com.example.myapplication.data.entity.UserBranch

@Database(
    entities = [
        User::class,
        User::class,
        Role::class,
        Branch::class,
        UserBranch::class,
        Category::class,
        Product::class,
        BranchProduct::class,
        Order::class,
        OrderItem::class,
        Payment::class,
        SalesSummary::class,
    ],
    version = 1,
    exportSchema = false // Was previously true
)
abstract class UserDatabase : RoomDatabase() {

    abstract val userDao: UserDao
    // abstract val roleDao: RoleDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "pos_database.db"           // ← name it after the whole app, not one table
                )
                    .fallbackToDestructiveMigration()   // ← remove before production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}