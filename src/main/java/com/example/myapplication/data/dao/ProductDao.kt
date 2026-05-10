package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.data.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // ─── Write Operations ─────────────────────────────────────────────────

    @Upsert
    suspend fun upsertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    // Soft delete — preserves order history that references this product
    @Query("UPDATE products SET status = 'inactive' WHERE product_id = :productId")
    suspend fun deactivateProduct(productId: Int)

    // ─── Read Single ──────────────────────────────────────────────────────

    @Query("SELECT * FROM products WHERE product_id = :productId")
    suspend fun getProductById(productId: Int): Product?

    // ─── Read List ────────────────────────────────────────────────────────

    @Query("SELECT * FROM products WHERE status = 'active' ORDER BY product_name ASC")
    fun getActiveProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY product_name ASC")
    fun getAllProducts(): Flow<List<Product>>

    // Products filtered by category — for menu display
    @Query("""
        SELECT * FROM products 
        WHERE category_id = :categoryId 
        AND status = 'active' 
        ORDER BY product_name ASC
    """)
    fun getProductsByCategory(categoryId: Int): Flow<List<Product>>

    // Branch-specific products with overridden price — core POS query
    @Query("""
        SELECT p.product_id, p.product_name, p.category_id,
               bp.price AS base_price, p.status
        FROM products p
        INNER JOIN branch_products bp ON p.product_id = bp.product_id
        WHERE bp.branch_id = :branchId
        AND bp.availability = 1
        AND p.status = 'active'
        ORDER BY p.product_name ASC
    """)
    fun getProductsForBranch(branchId: Int): Flow<List<Product>>

    // ─── Search ───────────────────────────────────────────────────────────

    @Query("""
        SELECT * FROM products 
        WHERE product_name LIKE '%' || :query || '%' 
        AND status = 'active'
    """)
    fun searchProducts(query: String): Flow<List<Product>>
}