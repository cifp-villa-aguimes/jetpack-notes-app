package edu.dam.notesapptyped.data

import kotlinx.coroutines.flow.MutableStateFlow

class AppState {
    val userName = MutableStateFlow("")
    
    fun resetForLogout() {
        userName.value = ""
    }
}