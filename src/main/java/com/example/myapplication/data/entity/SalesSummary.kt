package com.example.myapplication.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "sales_summary",
    foreignKeys = [
        ForeignKey(
            entity = Branch::class,
            parentColumns = ["branch_id"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["branch_id"])]
)
data class SalesSummary(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "summary_id")
    val summaryId: Int = 0,

    @ColumnInfo(name = "branch_id")
    val branchId: Int,

    @ColumnInfo(name = "summary_date")
    val summaryDate: Long,              // store as epoch — date only, not datetime

    @ColumnInfo(name = "total_sales")
    val totalSales: Double = 0.0,

    @ColumnInfo(name = "total_orders")
    val totalOrders: Int = 0,

    @ColumnInfo(name = "total_items_sold")
    val totalItemsSold: Int = 0
)
