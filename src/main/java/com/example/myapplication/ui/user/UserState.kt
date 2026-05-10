package com.example.myapplication.ui.user

import com.example.myapplication.ui.user.UserSortType
import com.example.myapplication.data.entity.User

data class UserState(
    val user: List<User> = emptyList(),
    val username: String = "",
    val email: String = "",
    val isAddingUser: Boolean = false,      // ← controls dialog visibility
    val sortType: UserSortType = UserSortType.USERNAME  // ← default sort
)
