package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.data.entity.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Upsert
    suspend fun upsertPayment(payment: Payment)

    @Query("SELECT * FROM payments WHERE order_id IN (SELECT order_id FROM orders WHERE branch_id = :branchId)")
    fun getPaymentsByBranch(branchId: Int): Flow<List<Payment>>
}