package com.example.myapplication.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.user.UserSortType
import com.example.myapplication.data.dao.UserDao
import com.example.myapplication.data.entity.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModel(
    private val dao: UserDao
): ViewModel() {

    private val _sortType = MutableStateFlow(UserSortType.USERNAME)
    private val _users = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                UserSortType.USERNAME -> dao.getUsersOrderedByUsername()
                UserSortType.EMAIL -> dao.getUsersOrderedByEmail()
                UserSortType.CREATED_AT -> dao.getUsersOrderedByCreatedAt()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(UserState())
    val state = combine(_state, _sortType, _users) { state, sortType, users ->
        state.copy(
            user = users,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserState())

    fun onEvent(event: UserEvent) {
        when(event) {
            is UserEvent.DeleteUser -> {
                viewModelScope.launch {
                    dao.deleteUser(event.user)
                }
            }
            UserEvent.HideDialog -> {
                _state.update {it.copy(
                    isAddingUser = false
                ) }
            }
            UserEvent.SaveUser -> {
                val username = state.value.username
                val email = state.value.email

                if(username.isBlank() || email.isBlank()) {
                    return
                }

                val user = User(
                    username = username,
                    email = email,
                    passwordHash = ""
                )
                viewModelScope.launch {
                    dao.upsertUser(user)
                }
                _state.update { it.copy(
                    isAddingUser = false,
                    username = "",
                    email = ""
,                )}
            }
            is UserEvent.SetEmail -> {
                _state.update {it.copy(
                    email = event.email
                ) }
            }
            is UserEvent.SetUsername -> {
                _state.update {it.copy(
                    username = event.username
                )}
            }
            UserEvent.ShowDialog -> {
                _state.update {it.copy(
                    isAddingUser = true
                )}
            }
            is UserEvent.SortUser -> {
                _sortType.value = event.sortType

            }
        }
    }

}