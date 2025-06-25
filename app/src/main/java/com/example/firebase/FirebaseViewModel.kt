package com.example.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirebaseViewModel : ViewModel() {
    private val database = Firebase.database.reference

    // Estado para los mensajes
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    // Estado para el nuevo mensaje
    private val _newMessage = MutableStateFlow("")
    val newMessage = _newMessage.asStateFlow()

    init {
        // Escuchar cambios en la base de datos
        database.child("messages").addValueEventListener(object : ValueEventListener {
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

    fun updateNewMessage(text: String) {
        _newMessage.value = text
    }

    fun addMessage() {
        if (_newMessage.value.isNotBlank()) {
            viewModelScope.launch {
                // Añadir nuevo mensaje a la base de datos
                database.child("messages").push().setValue(_newMessage.value)
                _newMessage.value = ""
            }
        }
    }
}