package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.data.entity.Role
import kotlinx.coroutines.flow.Flow

@Dao
interface RoleDao {

    // ─── Write Operations ─────────────────────────────────────────────────

    @Upsert
    suspend fun upsertRole(role: Role)

    @Delete
    suspend fun deleteRole(role: Role)

    // ─── Read Single ──────────────────────────────────────────────────────

    @Query("SELECT * FROM roles WHERE role_id = :roleId")
    suspend fun getRoleById(roleId: Int): Role?

    // ─── Read List ────────────────────────────────────────────────────────

    @Query("SELECT * FROM roles ORDER BY role_name ASC")
    fun getAllRoles(): Flow<List<Role>>

    // ─── Utility ──────────────────────────────────────────────────────────

    @Query("SELECT * FROM roles WHERE role_name = :roleName LIMIT 1")
    suspend fun getRoleByName(roleName: String): Role?
}