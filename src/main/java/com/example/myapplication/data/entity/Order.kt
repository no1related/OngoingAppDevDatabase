package com.example.myapplication.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = Branch::class,
            parentColumns = ["branch_id"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.RESTRICT   // don't delete branch if orders exist
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["cashier_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["branch_id"]),
        Index(value = ["cashier_id"])
    ]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "order_id")
    val orderId: Int = 0,

    @ColumnInfo(name = "branch_id")
    val branchId: Int,

    @ColumnInfo(name = "cashier_id")
    val cashierId: Int,

    @ColumnInfo(name = "order_datetime")
    val orderDatetime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "total_amount")
    val totalAmount: Double = 0.0,

    @ColumnInfo(name = "order_status")
    val orderStatus: String = "pending"  // "pending" / "completed" / "cancelled"
)
