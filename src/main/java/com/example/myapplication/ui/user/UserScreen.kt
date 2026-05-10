package com.example.myapplication.ui.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun UserScreen(
    state: UserState,
    onEvent: (UserEvent) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(UserEvent.ShowDialog) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = CenterVertically
                    ) {
                        UserSortType.values().forEach { sortType ->
                            Row(
                                modifier = Modifier
                                    .clickable{
                                        onEvent(UserEvent.SortUser(sortType))
                                    },
                                verticalAlignment = CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.sortType == sortType,
                                    onClick = {
                                        onEvent(UserEvent.SortUser(sortType))
                                    }
                                )
                                Text(text = sortType.name)
                            }
                        }
                    }
                }
                items(state.user) { user ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = user.username,
                                fontSize =  20.sp
                            )
                            Text(text = user.email, fontSize = 12.sp)
                        }
                        IconButton(onClick = {
                            onEvent(UserEvent.DeleteUser(user))
                        }) {
                            Icon(imageVector = Icons.Default.Delete,
                                contentDescription = "Delete User")
                        }
                    }

                }
            }

            if (state.isAddingUser) {
                AddUserDialog(
                    state = state,
                    onEvent = onEvent
                )
            }
        }
    }
}
