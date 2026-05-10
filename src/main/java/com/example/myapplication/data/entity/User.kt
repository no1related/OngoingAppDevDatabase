package com.example.myapplication.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    foreignKeys = [
        ForeignKey(
            entity = Role::class,
            parentColumns = ["role_id"],
            childColumns = ["role_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["role_id"])]
)


data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val userId: Int = 0,                        // ① moved to top, non-nullable

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,               // ② soft delete flag

    // ─── FK COLUMN (leave nullable until ROLES is ready) ──────────────────
    @ColumnInfo(name = "role_id")
    val roleId: Int? = null                     // ③ Int not String, nullable is correct here
)