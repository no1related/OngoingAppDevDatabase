package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.data.entity.SalesSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesDao {

    // ─── Write Operations ─────────────────────────────────────────────────

    @Upsert
    suspend fun upsertSalesSummary(summary: SalesSummary)

    // ─── Read Single ──────────────────────────────────────────────────────

    @Query("""
        SELECT * FROM sales_summary 
        WHERE branch_id = :branchId 
        AND summary_date = :date 
        LIMIT 1
    """)
    suspend fun getSummaryForDate(branchId: Int, date: Long): SalesSummary?

    // ─── Read List ────────────────────────────────────────────────────────

    // All summaries for a branch — for dashboard chart data
    @Query("""
        SELECT * FROM sales_summary 
        WHERE branch_id = :branchId 
        ORDER BY summary_date DESC
    """)
    fun getSummariesForBranch(branchId: Int): Flow<List<SalesSummary>>

    // Last 7 days — for weekly dashboard widget
    @Query("""
        SELECT * FROM sales_summary
        WHERE branch_id = :branchId
        AND summary_date >= :sevenDaysAgo
        ORDER BY summary_date ASC
    """)
    fun getWeeklySummary(branchId: Int, sevenDaysAgo: Long): Flow<List<SalesSummary>>

    // Last 30 days — for monthly dashboard widget
    @Query("""
        SELECT * FROM sales_summary
        WHERE branch_id = :branchId
        AND summary_date >= :thirtyDaysAgo
        ORDER BY summary_date ASC
    """)
    fun getMonthlySummary(branchId: Int, thirtyDaysAgo: Long): Flow<List<SalesSummary>>

    // ─── Cross-Branch Analytics (Admin view) ──────────────────────────────

    @Query("""
        SELECT * FROM sales_summary
        WHERE summary_date BETWEEN :startDate AND :endDate
        ORDER BY branch_id ASC, summary_date ASC
    """)
    fun getAllBranchSummariesByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<SalesSummary>>

    // Top performing branch by total sales
    @Query("""
        SELECT branch_id, SUM(total_sales) as total_sales,
               SUM(total_orders) as total_orders,
               SUM(total_items_sold) as total_items_sold,
               0 as summary_id, 0 as summary_date
        FROM sales_summary
        WHERE summary_date BETWEEN :startDate AND :endDate
        GROUP BY branch_id
        ORDER BY total_sales DESC
    """)
    fun getBranchRankingByPeriod(
        startDate: Long,
        endDate: Long
    ): Flow<List<SalesSummary>>
}