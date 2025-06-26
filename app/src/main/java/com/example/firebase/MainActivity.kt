package com.example.firebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val jugadores by viewModel.jugadores.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Sección de jugadores (Firestore)
            Text(
                text = "jugadores",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (jugadores.isEmpty()) {
                Text("No hay jugadores registrados")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {
                    items(jugadores) { jugador ->
                        jugadorCard(jugador = jugador)
                    }
                }
            }

            // Divider
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Sección de Mensajes (Realtime Database)
            Text(
                text = "Mensajes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
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

@Composable
fun jugadorCard(jugador: jugador) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = jugador.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Edad: ${jugador.edad}")
            Text(text = "Grand Slams: ${jugador.grandSlams}")
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
