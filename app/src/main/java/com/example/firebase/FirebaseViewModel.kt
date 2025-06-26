package com.example.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Jugador(
    val nombre: String = "",
    val edad: Int = 0,
    val grandSlams: Int = 0
)

class FirebaseViewModel : ViewModel() {
    // Para Realtime Database (mensajes)
    private val realtimeDb = Firebase.database.reference
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()
    private val _newMessage = MutableStateFlow("")
    val newMessage = _newMessage.asStateFlow()

    // Para Firestore (Jugadores)
    private val firestoreDb = Firebase.firestore
    private val _Jugadores = MutableStateFlow<List<Jugador>>(emptyList())
    val Jugadores = _Jugadores.asStateFlow()

    init {
        // Escuchar mensajes de Realtime Database
        setupRealtimeDatabaseListener()

        // Obtener Jugadores de Firestore
        setupFirestoreListener()
    }

    private fun setupRealtimeDatabaseListener() {
        realtimeDb.child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = mutableListOf<String>()
                for (child in snapshot.children) {
                    child.getValue(String::class.java)?.let { messageList.add(it) }
                }
                _messages.value = messageList
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }

    private fun setupFirestoreListener() {
        firestoreDb.collection("Jugadores").document("Jugadores")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Manejar error
                    return@addSnapshotListener
                }

                snapshot?.let { document ->
                    val JugadoresList = mutableListOf<Jugador>()
                    // Si el documento contiene un array de Jugadores
                    val JugadoresArray = document.get("Jugadores") as? List<Map<String, Any>>
                    JugadoresArray?.forEach { JugadorData ->
                        JugadoresList.add(
                            Jugador(
                                nombre = JugadorData["Nombre"] as? String ?: "",
                                edad = (JugadorData["Edad"] as? Long)?.toInt() ?: 0,
                                grandSlams = (JugadorData["Grand Slams"] as? Long)?.toInt() ?: 0
                            )
                        )
                    }
                    // O si los campos están directamente en el documento
                    if (JugadoresList.isEmpty()) {
                        val nombre = document.getString("Nombre") ?: ""
                        val edad = document.getLong("Edad")?.toInt() ?: 0
                        val grandSlams = document.getLong("Grand Slams")?.toInt() ?: 0
                        if (nombre.isNotEmpty()) {
                            JugadoresList.add(Jugador(nombre, edad, grandSlams))
                        }
                    }

                    _Jugadores.value = JugadoresList
                }
            }
    }

    fun updateNewMessage(text: String) {
        _newMessage.value = text
    }

    fun addMessage() {
        if (_newMessage.value.isNotBlank()) {
            viewModelScope.launch {
                realtimeDb.child("messages").push().setValue(_newMessage.value)
                _newMessage.value = ""
            }
        }
    }
}
