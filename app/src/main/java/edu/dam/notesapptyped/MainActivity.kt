package edu.dam.notesapptyped

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import edu.dam.notesapptyped.data.AppState
import edu.dam.notesapptyped.navigation.NavGraph
import edu.dam.notesapptyped.theme.NotesAppTypedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppTypedTheme {
                val nav = rememberNavController()
                val state = remember { AppState() }
                NavGraph(nav, state)
            }
        }
    }
}