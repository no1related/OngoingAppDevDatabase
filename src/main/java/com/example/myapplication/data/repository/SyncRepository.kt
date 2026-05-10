package com.example.myapplication.data.repository

import com.example.myapplication.data.database.UserDatabase
import com.example.myapplication.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.first

class SyncRepository(
    private val database: UserDatabase,
    private val firestore: FirestoreDataSource
) {

    // ══════════════════════════════════════════════════════════════════════
    // PUBLIC API
    // Call these from SyncWorker or anywhere in your app
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Push all local Room data up to Firestore.
     * Call this after any significant write (e.g. completing an order).
     */
    suspend fun uploadAll(branchId: Int) {
        uploadRoles()
        uploadProducts()
        uploadUsers()
        uploadOrders(branchId)
        uploadOrderItems(branchId)
        uploadPayments(branchId)
        uploadSalesSummaries(branchId)

    }

    /**
     * Pull all Firestore data down into Room.
     * Call this on app start or when switching branches.
     */
    suspend fun downloadAll(branchId: Int) {
        downloadRoles()
        downloadProducts()
        downloadUsers()
        downloadOrders(branchId)
        downloadOrderItems(branchId)
        downloadPayments(branchId)
        downloadSalesSummaries(branchId)
    }

    /**
     * Full two-way sync: upload first, then download.
     * Used by SyncWorker for periodic background sync.
     */
    suspend fun syncAll(branchId: Int) {
        uploadAll(branchId)
        downloadAll(branchId)
    }

    // ══════════════════════════════════════════════════════════════════════
    // UPLOAD HELPERS — Room → Firestore
    // .first() collects one emission from the Flow, then stops
    // ══════════════════════════════════════════════════════════════════════

    private suspend fun uploadRoles() {
        val roles = database.roleDao.getAllRoles().first()
        firestore.uploadRoles(roles)
    }

    private suspend fun uploadProducts() {
        val products = database.productDao.getAllProducts().first()
        firestore.uploadProducts(products)
    }

    private suspend fun uploadUsers() {
        val users = database.userDao.getUsersOrderedByUsername().first()
        firestore.uploadUsers(users)
    }

    private suspend fun uploadOrders(branchId: Int) {
        val orders = database.orderDao.getOrdersByBranch(branchId).first()
        firestore.uploadOrders(branchId, orders)
    }

    private suspend fun uploadOrderItems(branchId: Int) {
        // No getAll for order_items — fetch items per order
        val orders = database.orderDao.getOrdersByBranch(branchId).first()
        val allItems = orders.flatMap { order ->
            database.orderDao.getItemsForOrder(order.orderId).first()
        }
        firestore.uploadOrderItems(branchId, allItems)
    }

    private suspend fun uploadSalesSummaries(branchId: Int) {
        val summaries = database.salesDao.getSummariesForBranch(branchId).first()
        firestore.uploadSalesSummaries(branchId, summaries)
    }

    // ─── GAP: PaymentDao does not exist yet ───────────────────────────────
    // TODO: Create PaymentDao with upsertPayment() and getPaymentsByBranch()

    private suspend fun uploadPayments(branchId: Int) {
         val payments = database.paymentDao.getPaymentsByBranch(branchId).first()
         firestore.uploadPayments(branchId, payments)
    }

    // ══════════════════════════════════════════════════════════════════════
    // DOWNLOAD HELPERS — Firestore → Room
    // Uses @Upsert (via existing DAO methods) — safe to call repeatedly
    // ══════════════════════════════════════════════════════════════════════

    private suspend fun downloadRoles() {
        firestore.downloadRoles().forEach {
            database.roleDao.upsertRole(it)
        }
    }

    private suspend fun downloadProducts() {
        firestore.downloadProducts().forEach {
            database.productDao.upsertProduct(it)
        }
    }

    private suspend fun downloadUsers() {
        firestore.downloadUsers().forEach {
            database.userDao.upsertUser(it)
        }
    }

    private suspend fun downloadOrders(branchId: Int) {
        firestore.downloadOrders(branchId).forEach {
            database.orderDao.upsertOrder(it)
        }
    }

    private suspend fun downloadOrderItems(branchId: Int) {
        firestore.downloadOrderItems(branchId).forEach {
            database.orderDao.upsertOrderItem(it)
        }
    }

    private suspend fun downloadPayments(branchId: Int) {
        firestore.downloadPayments(branchId).forEach {
            database.paymentDao.upsertPayment(it)
        }
    }

    private suspend fun downloadSalesSummaries(branchId: Int) {
        firestore.downloadSalesSummaries(branchId).forEach {
            database.salesDao.upsertSalesSummary(it)
        }
    }
}