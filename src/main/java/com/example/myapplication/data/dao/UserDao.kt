package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ─── Write Operations ─────────────────────────────────────────────────

    @Upsert
    suspend fun upsertUser(user: User)          // renamed — operates on com.example.myapplication.data.entity.User not ID

    @Delete
    suspend fun deleteUser(user: User)          // renamed for clarity

    // Soft delete — preferred over @Delete for POS data integrity
    @Query("UPDATE users SET is_active = 0 WHERE user_id = :userId")
    suspend fun deactivateUser(userId: Int)

    // ─── Read Single ──────────────────────────────────────────────────────

    @Query("SELECT * FROM users WHERE user_id = :userId")
    suspend fun getUserById(userId: Int): User?  // suspend not Flow — fetched once

    // ─── Read List (observe changes via Flow) ─────────────────────────────

    @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY username ASC")
    fun getUsersOrderedByUsername(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY email ASC")
    fun getUsersOrderedByEmail(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY created_at ASC")
    fun getUsersOrderedByCreatedAt(): Flow<List<User>>

    // ─── FK Query (uncomment when ROLES table is ready) ───────────────────
    // @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY role_id ASC")
    // fun getUsersOrderedByRole(): Flow<List<com.example.myapplication.data.entity.User>>

}