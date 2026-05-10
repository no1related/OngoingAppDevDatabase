package com.example.myapplication.ui.user

import com.example.myapplication.ui.user.UserSortType
import com.example.myapplication.data.entity.User

sealed interface UserEvent {
    data class SetUsername(val username: String): UserEvent
    data class SetEmail(val email: String): UserEvent

    //Dialog
    object ShowDialog : UserEvent
    object HideDialog : UserEvent

    //Sort, Delete, Save
    data class SortUser(val sortType: UserSortType): UserEvent
    data class DeleteUser(val user: User) : UserEvent
    object SaveUser : UserEvent
}