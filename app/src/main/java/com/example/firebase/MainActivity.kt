package com.example.firebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firebase.ui.theme.FireBaseTheme

class MainActivity : ComponentActivity() {
    private val viewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FireBaseTheme {
                FirebaseApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseApp(viewModel: FirebaseViewModel) {
    val messages by viewModel.messages.collectAsState()
    val newMessage by viewModel.newMessage.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Lista de mensajes
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages) { message ->
                    Text(text = message, modifier = Modifier.padding(8.dp))
                }
            }

            // Campo para nuevo mensaje
            TextField(
                value = newMessage,
                onValueChange = { viewModel.updateNewMessage(it) },
                label = { Text("Nuevo mensaje") },
                modifier = Modifier.fillMaxWidth()
            )

            // Botón para enviar
            Button(
                onClick = { viewModel.addMessage() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar mensaje")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FirebaseAppPreview() {
    FireBaseTheme {
        FirebaseApp(FirebaseViewModel())
    }
}