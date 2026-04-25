package com.example.myapplication.data.entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "branch_products",
    foreignKeys = [
        ForeignKey(
            entity = Branch::class,
            parentColumns = ["branch_id"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["branch_id"]),
        Index(value = ["product_id"])
    ]
)
data class BranchProduct(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "branch_product_id")
    val branchProductId: Int = 0,

    @ColumnInfo(name = "branch_id")
    val branchId: Int,

    @ColumnInfo(name = "product_id")
    val productId: Int,

    @ColumnInfo(name = "price")
    val price: Double,                  // overrides base_price for this branch

    @ColumnInfo(name = "stock_quantity")
    val stockQuantity: Int = 0,

    @ColumnInfo(name = "availability")
    val availability: Boolean = true
)
