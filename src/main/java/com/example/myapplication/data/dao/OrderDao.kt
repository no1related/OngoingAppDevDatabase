package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.data.entity.Order
import com.example.myapplication.data.entity.OrderItem
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    // ─── Write Operations ─────────────────────────────────────────────────

    @Upsert
    suspend fun upsertOrder(order: Order): Long    // returns generated order_id

    @Upsert
    suspend fun upsertOrderItem(orderItem: OrderItem)

    @Upsert
    suspend fun upsertOrderItems(orderItems: List<OrderItem>)

    @Delete
    suspend fun deleteOrder(order: Order)

    // Update order status — used when completing or cancelling a transaction
    @Query("UPDATE orders SET order_status = :status WHERE order_id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)

    // Update total — called after all items are added
    @Query("UPDATE orders SET total_amount = :total WHERE order_id = :orderId")
    suspend fun updateOrderTotal(orderId: Int, total: Double)

    // ─── Read Single ──────────────────────────────────────────────────────

    @Query("SELECT * FROM orders WHERE order_id = :orderId")
    suspend fun getOrderById(orderId: Int): Order?

    @Query("SELECT * FROM order_items WHERE order_id = :orderId")
    fun getItemsForOrder(orderId: Int): Flow<List<OrderItem>>

    // ─── Read List ────────────────────────────────────────────────────────

    @Query("""
        SELECT * FROM orders 
        WHERE branch_id = :branchId 
        ORDER BY order_datetime DESC
    """)
    fun getOrdersByBranch(branchId: Int): Flow<List<Order>>

    @Query("""
        SELECT * FROM orders 
        WHERE cashier_id = :cashierId 
        ORDER BY order_datetime DESC
    """)
    fun getOrdersByCashier(cashierId: Int): Flow<List<Order>>

    @Query("""
        SELECT * FROM orders 
        WHERE order_status = :status 
        ORDER BY order_datetime DESC
    """)
    fun getOrdersByStatus(status: String): Flow<List<Order>>

    // Orders within a date range — for daily/period reports
    @Query("""
        SELECT * FROM orders 
        WHERE branch_id = :branchId
        AND order_datetime BETWEEN :startDate AND :endDate
        ORDER BY order_datetime DESC
    """)
    fun getOrdersByDateRange(
        branchId: Int,
        startDate: Long,
        endDate: Long
    ): Flow<List<Order>>

    // ─── Analytics Queries ────────────────────────────────────────────────

    // Total sales for a branch on a specific day
    @Query("""
        SELECT COALESCE(SUM(total_amount), 0.0)
        FROM orders
        WHERE branch_id = :branchId
        AND order_status = 'completed'
        AND order_datetime BETWEEN :startOfDay AND :endOfDay
    """)
    suspend fun getDailyTotalSales(
        branchId: Int,
        startOfDay: Long,
        endOfDay: Long
    ): Double

    // Total order count for a branch on a specific day
    @Query("""
        SELECT COUNT(*) FROM orders
        WHERE branch_id = :branchId
        AND order_status = 'completed'
        AND order_datetime BETWEEN :startOfDay AND :endOfDay
    """)
    suspend fun getDailyOrderCount(
        branchId: Int,
        startOfDay: Long,
        endOfDay: Long
    ): Int

    // Total items sold in a day — feeds into SALES_SUMMARY
    @Query("""
        SELECT COALESCE(SUM(oi.quantity), 0)
        FROM order_items oi
        INNER JOIN orders o ON oi.order_id = o.order_id
        WHERE o.branch_id = :branchId
        AND o.order_status = 'completed'
        AND o.order_datetime BETWEEN :startOfDay AND :endOfDay
    """)
    suspend fun getDailyItemsSold(
        branchId: Int,
        startOfDay: Long,
        endOfDay: Long
    ): Int
}