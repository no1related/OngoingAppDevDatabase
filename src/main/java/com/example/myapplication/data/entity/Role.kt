package com.example.myapplication.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roles")
data class Role(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "role_id")
    val roleId: Int = 0,

    @ColumnInfo(name = "role_name")
    val roleName: String
)
