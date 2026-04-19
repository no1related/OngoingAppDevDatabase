package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.myapplication.data.database.UserDatabase
import com.example.myapplication.ui.user.UserScreen
import com.example.myapplication.ui.user.UserViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create database + DAO (exactly how the tutorial does it)
        val database = UserDatabase.getInstance(applicationContext)
        val dao = database.userDao

        // Create ViewModel manually
        val viewModel = UserViewModel(dao)

        setContent {
            MaterialTheme {
                val state by viewModel.state.collectAsState()

                UserScreen(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}
