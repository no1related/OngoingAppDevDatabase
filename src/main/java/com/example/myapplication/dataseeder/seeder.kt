package com.example.myapplication

import com.example.myapplication.data.database.UserDatabase
import com.example.myapplication.data.entity.Order
import com.example.myapplication.data.entity.OrderItem
import com.example.myapplication.data.entity.Payment
import com.example.myapplication.data.entity.Product
import com.example.myapplication.data.entity.Role
import com.example.myapplication.data.entity.SalesSummary
import com.example.myapplication.data.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DataSeeder {

    fun seed(database: UserDatabase) {
        CoroutineScope(Dispatchers.IO).launch {

            // ─── Roles ────────────────────────────────────────────────
            database.roleDao.upsertRole(Role(roleId = 1, roleName = "Admin"))
            database.roleDao.upsertRole(Role(roleId = 2, roleName = "Cashier"))

            // ─── Branch ───────────────────────────────────────────────
            // NOTE: No BranchDao yet — insert via UserDatabase workaround
            // Skip for now, branchId = 1 is assumed by SyncWorker

            // ─── Users ────────────────────────────────────────────────
            database.userDao.upsertUser(User(
                userId = 1,
                username = "admin",
                email = "admin@boysabaw.com",
                passwordHash = "hashed_password_1",
                roleId = 1
            ))
            database.userDao.upsertUser(User(
                userId = 2,
                username = "cashier1",
                email = "cashier1@boysabaw.com",
                passwordHash = "hashed_password_2",
                roleId = 2
            ))

            // ─── Categories ───────────────────────────────────────────
            // NOTE: No CategoryDao yet — skip for now

            // ─── Products ─────────────────────────────────────────────
            database.productDao.upsertProduct(Product(
                productId = 1,
                productName = "Beef Mami",
                basePrice = 85.0
            ))
            database.productDao.upsertProduct(Product(
                productId = 2,
                productName = "Pork Siopao",
                basePrice = 45.0
            ))
            database.productDao.upsertProduct(Product(
                productId = 3,
                productName = "Lomi",
                basePrice = 95.0
            ))

            // ─── Orders ───────────────────────────────────────────────
            val orderId = database.orderDao.upsertOrder(Order(
                orderId = 1,
                branchId = 1,
                cashierId = 2,
                totalAmount = 130.0,
                orderStatus = "completed"
            )).toInt()

            // ─── Order Items ──────────────────────────────────────────
            database.orderDao.upsertOrderItems(listOf(
                OrderItem(
                    orderItemId = 1,
                    orderId = orderId,
                    productId = 1,
                    quantity = 1,
                    unitPrice = 85.0,
                    subtotal = 85.0
                ),
                OrderItem(
                    orderItemId = 2,
                    orderId = orderId,
                    productId = 2,
                    quantity = 1,
                    unitPrice = 45.0,
                    subtotal = 45.0
                )
            ))

            // ─── Payment ──────────────────────────────────────────────
            database.paymentDao.upsertPayment(Payment(
                paymentId = 1,
                orderId = orderId,
                paymentMethod = "cash",
                amountPaid = 150.0
            ))

            // ─── Sales Summary ────────────────────────────────────────
            database.salesDao.upsertSalesSummary(SalesSummary(
                summaryId = 1,
                branchId = 1,
                summaryDate = System.currentTimeMillis(),
                totalSales = 130.0,
                totalOrders = 1,
                totalItemsSold = 2
            ))
        }
    }
}