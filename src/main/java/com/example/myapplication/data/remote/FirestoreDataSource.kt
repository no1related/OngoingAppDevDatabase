package com.example.myapplication.data.remote

import com.example.myapplication.data.entity.Order
import com.example.myapplication.data.entity.OrderItem
import com.example.myapplication.data.entity.Payment
import com.example.myapplication.data.entity.Product
import com.example.myapplication.data.entity.Role
import com.example.myapplication.data.entity.SalesSummary
import com.example.myapplication.data.entity.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreDataSource {

    private val db = FirebaseFirestore.getInstance()

    // ─── Collection References ─────────────────────────────────────────────
    // Global collections (shared across all branches)
    private val rolesCol    = db.collection("roles")
    private val productsCol = db.collection("products")
    private val usersCol    = db.collection("users")

    // Branch-scoped collections (data belongs to a specific branch)
    private fun branchDoc(branchId: Int) =
        db.collection("branches").document("$branchId")

    // ══════════════════════════════════════════════════════════════════════
    // UPLOAD — Room → Firestore
    // ══════════════════════════════════════════════════════════════════════

    suspend fun uploadRoles(roles: List<Role>) =
        batchSet(roles) { rolesCol.document("${it.roleId}") to it.toMap() }

    suspend fun uploadProducts(products: List<Product>) =
        batchSet(products) { productsCol.document("${it.productId}") to it.toMap() }

    suspend fun uploadUsers(users: List<User>) =
        batchSet(users) { usersCol.document("${it.userId}") to it.toMap() }

    suspend fun uploadOrders(branchId: Int, orders: List<Order>) =
        batchSet(orders) {
            branchDoc(branchId).collection("orders").document("${it.orderId}") to it.toMap()
        }

    suspend fun uploadOrderItems(branchId: Int, items: List<OrderItem>) =
        batchSet(items) {
            branchDoc(branchId).collection("order_items").document("${it.orderItemId}") to it.toMap()
        }

    suspend fun uploadPayments(branchId: Int, payments: List<Payment>) =
        batchSet(payments) {
            branchDoc(branchId).collection("payments").document("${it.paymentId}") to it.toMap()
        }

    suspend fun uploadSalesSummaries(branchId: Int, summaries: List<SalesSummary>) =
        batchSet(summaries) {
            branchDoc(branchId).collection("sales_summary").document("${it.summaryId}") to it.toMap()
        }

    // ══════════════════════════════════════════════════════════════════════
    // DOWNLOAD — Firestore → Room
    // ══════════════════════════════════════════════════════════════════════

    suspend fun downloadRoles(): List<Role> =
        rolesCol.get().await().mapNotNull { it.toRole() }

    suspend fun downloadProducts(): List<Product> =
        productsCol.get().await().mapNotNull { it.toProduct() }

    suspend fun downloadUsers(): List<User> =
        usersCol.get().await().mapNotNull { it.toUser() }

    suspend fun downloadOrders(branchId: Int): List<Order> =
        branchDoc(branchId).collection("orders").get().await().mapNotNull { it.toOrder() }

    suspend fun downloadOrderItems(branchId: Int): List<OrderItem> =
        branchDoc(branchId).collection("order_items").get().await().mapNotNull { it.toOrderItem() }

    suspend fun downloadPayments(branchId: Int): List<Payment> =
        branchDoc(branchId).collection("payments").get().await().mapNotNull { it.toPayment() }

    suspend fun downloadSalesSummaries(branchId: Int): List<SalesSummary> =
        branchDoc(branchId).collection("sales_summary").get().await().mapNotNull { it.toSalesSummary() }

    // ══════════════════════════════════════════════════════════════════════
    // BATCH HELPER
    // Firestore batches are capped at 500 ops — chunked(499) handles this
    // ══════════════════════════════════════════════════════════════════════

    private suspend fun <T> batchSet(
        items: List<T>,
        mapper: (T) -> Pair<com.google.firebase.firestore.DocumentReference, Map<String, Any?>>
    ) {
        if (items.isEmpty()) return
        items.chunked(499).forEach { chunk ->
            val batch = db.batch()
            chunk.forEach { item ->
                val (ref, data) = mapper(item)
                batch.set(ref, data)
            }
            batch.commit().await()
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// ENTITY → MAP  (Room @Entity → Firestore document)
// ══════════════════════════════════════════════════════════════════════════════

private fun Role.toMap(): Map<String, Any?> = mapOf(
    "role_id"   to roleId,
    "role_name" to roleName
)

private fun Product.toMap(): Map<String, Any?> = mapOf(
    "product_id"   to productId,
    "product_name" to productName,
    "category_id"  to categoryId,
    "base_price"   to basePrice,
    "status"       to status
)

private fun User.toMap(): Map<String, Any?> = mapOf(
    "user_id"       to userId,
    "username"      to username,
    "email"         to email,
    "password_hash" to passwordHash,
    "created_at"    to createdAt,
    "is_active"     to isActive,
    "role_id"       to roleId
)

private fun Order.toMap(): Map<String, Any?> = mapOf(
    "order_id"       to orderId,
    "branch_id"      to branchId,
    "cashier_id"     to cashierId,
    "order_datetime" to orderDatetime,
    "total_amount"   to totalAmount,
    "order_status"   to orderStatus
)

private fun OrderItem.toMap(): Map<String, Any?> = mapOf(
    "order_item_id" to orderItemId,
    "order_id"      to orderId,
    "product_id"    to productId,
    "quantity"      to quantity,
    "unit_price"    to unitPrice,
    "subtotal"      to subtotal
)

private fun Payment.toMap(): Map<String, Any?> = mapOf(
    "payment_id"       to paymentId,
    "order_id"         to orderId,
    "payment_method"   to paymentMethod,
    "amount_paid"      to amountPaid,
    "payment_datetime" to paymentDatetime
)

private fun SalesSummary.toMap(): Map<String, Any?> = mapOf(
    "summary_id"      to summaryId,
    "branch_id"       to branchId,
    "summary_date"    to summaryDate,
    "total_sales"     to totalSales,
    "total_orders"    to totalOrders,
    "total_items_sold" to totalItemsSold
)

// ══════════════════════════════════════════════════════════════════════════════
// DOCUMENTSNAPSHOT → ENTITY  (Firestore document → Room @Entity)
// mapNotNull() above silently skips documents that fail to parse
// ══════════════════════════════════════════════════════════════════════════════

private fun DocumentSnapshot.toRole(): Role? {
    return try {
        Role(
            roleId = getLong("role_id")?.toInt() ?: return null,
            roleName = getString("role_name") ?: return null
        )
    } catch (e: Exception) {
        null
    }
}


private fun DocumentSnapshot.toProduct(): Product? {
    return try {
        Product(
            productId   = getLong("product_id")?.toInt() ?: return null,
            productName = getString("product_name")       ?: return null,
            categoryId  = getLong("category_id")?.toInt(),
            basePrice   = getDouble("base_price")         ?: return null,
            status      = getString("status")             ?: "active"
        )
    } catch (e: Exception) { null }
}

private fun DocumentSnapshot.toUser(): User? {
    return try {
        User(
            userId       = getLong("user_id")?.toInt()  ?: return null,
            username     = getString("username")          ?: return null,
            email        = getString("email")             ?: return null,
            passwordHash = getString("password_hash")     ?: return null,
            createdAt    = getLong("created_at")          ?: System.currentTimeMillis(),
            isActive     = getBoolean("is_active")        ?: true,
            roleId       = getLong("role_id")?.toInt()
        )
    } catch (e: Exception) { null }
}

private fun DocumentSnapshot.toOrder(): Order? {
    return try {
        Order(
            orderId       = getLong("order_id")?.toInt()   ?: return null,
            branchId      = getLong("branch_id")?.toInt()  ?: return null,
            cashierId     = getLong("cashier_id")?.toInt() ?: return null,
            orderDatetime = getLong("order_datetime")       ?: System.currentTimeMillis(),
            totalAmount   = getDouble("total_amount")       ?: 0.0,
            orderStatus   = getString("order_status")       ?: "pending"
        )
    } catch (e: Exception) { null }
}

private fun DocumentSnapshot.toOrderItem(): OrderItem? {
    return try {
        OrderItem(
            orderItemId = getLong("order_item_id")?.toInt() ?: return null,
            orderId     = getLong("order_id")?.toInt()      ?: return null,
            productId   = getLong("product_id")?.toInt()    ?: return null,
            quantity    = getLong("quantity")?.toInt()       ?: return null,
            unitPrice   = getDouble("unit_price")            ?: return null,
            subtotal    = getDouble("subtotal")              ?: return null
        )
    } catch (e: Exception) { null }
}

private fun DocumentSnapshot.toPayment(): Payment? {
    return try {
        Payment(
            paymentId       = getLong("payment_id")?.toInt() ?: return null,
            orderId         = getLong("order_id")?.toInt()   ?: return null,
            paymentMethod   = getString("payment_method")     ?: return null,
            amountPaid      = getDouble("amount_paid")        ?: return null,
            paymentDatetime = getLong("payment_datetime")     ?: System.currentTimeMillis()
        )
    } catch (e: Exception) { null }
}

private fun DocumentSnapshot.toSalesSummary(): SalesSummary? {
    return try {
        SalesSummary(
            summaryId      = getLong("summary_id")?.toInt()      ?: return null,
            branchId       = getLong("branch_id")?.toInt()       ?: return null,
            summaryDate    = getLong("summary_date")              ?: return null,
            totalSales     = getDouble("total_sales")             ?: 0.0,
            totalOrders    = getLong("total_orders")?.toInt()     ?: 0,
            totalItemsSold = getLong("total_items_sold")?.toInt() ?: 0
        )
    } catch (e: Exception) { null }
}