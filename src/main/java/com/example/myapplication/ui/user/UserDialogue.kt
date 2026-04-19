import com.example.myapplication.ui.user.UserEvent
import com.example.myapplication.ui.user.UserState

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog        // ← Compose version
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddUserDialog(
    state: UserState,
    onEvent: (UserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(UserEvent.HideDialog)   // ← dismiss closes dialog
        },
        title = { Text(text = "Add User") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)  // ← capital B
            ) {
                TextField(
                    value = state.username,         // ← comma not bracket
                    onValueChange = {
                        onEvent(UserEvent.SetUsername(it))
                    },
                    placeholder = {
                        Text(text = "Username")
                    }
                )
                TextField(
                    value = state.email,
                    onValueChange = {
                        onEvent(UserEvent.SetEmail(it))
                    },
                    placeholder = {
                        Text(text = "Email")
                    }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onEvent(UserEvent.SaveUser)
            }) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            Button(onClick = {
                onEvent(UserEvent.HideDialog)
            }) {
                Text(text = "Cancel")
            }
        }
    )
}