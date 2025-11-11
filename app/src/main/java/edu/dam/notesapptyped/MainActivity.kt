package edu.dam.notesapptyped

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import edu.dam.notesapptyped.data.AppState
import edu.dam.notesapptyped.data.prefs.UserPrefsRepository
import edu.dam.notesapptyped.navigation.NavGraph
import edu.dam.notesapptyped.theme.NotesAppTypedTheme
import edu.dam.notesapptyped.ui.notes.NotesViewModel
import edu.dam.notesapptyped.ui.notes.NotesViewModelFactory

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val nav = rememberNavController()
            val state = remember { AppState() }
            val prefs = remember { UserPrefsRepository(applicationContext) }
            val notesViewModel: NotesViewModel = viewModel(
                factory = NotesViewModelFactory(applicationContext)
            )

            // Leer dark mode desde DataStore
            val darkMode = prefs.darkModeFlow.collectAsState(initial = false).value

            // Sincronizar el nombre guardado con el AppState para autores de notas
            val savedName = prefs.userNameFlow.collectAsState(initial = "").value
            LaunchedEffect(savedName) {
                state.userName.value = savedName
            }

            NotesAppTypedTheme(darkTheme = darkMode) {
                NavGraph(nav, state, prefs, notesViewModel)
            }
        }
    }
}