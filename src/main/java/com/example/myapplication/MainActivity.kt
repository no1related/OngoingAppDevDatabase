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
import com.example.myapplication.worker.SyncWorker   // ADD

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = UserDatabase.getInstance(applicationContext)
        val dao = database.userDao
        val viewModel = UserViewModel(dao)

        DataSeeder.seed(database)                      // ← seeds Room
        SyncWorker.runNow(this, branchId = 1)          // ← immediately pushes to Firestore

        setContent {
            MaterialTheme {
                val state by viewModel.state.collectAsState()
                UserScreen(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
        SyncWorker.schedule(this, branchId = 1)        // ADD
    }
}