package com.example.myapplication.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branches")
data class Branch(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "branch_id")
    val branchId: Int = 0,

    @ColumnInfo(name = "branch_name")
    val branchName: String,

    @ColumnInfo(name = "branch_location")
    val branchLocation: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
