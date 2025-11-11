package edu.dam.notesapptyped.ui.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.dam.notesapptyped.data.model.Note

/**
 * Componente que envuelve una NoteCard con funcionalidad de swipe-to-delete.
 * 
 * Conceptos educativos demostrados:
 * - SwipeToDismissBox: Gestión de gestos de deslizamiento
 * - confirmValueChange: Control de acciones basadas en gestos
 * - backgroundContent: Contenido revelado durante el swipe
 * - Animaciones progresivas: Alpha basado en el progreso del gesto
 * 
 * @param note La nota a mostrar
 * @param onOpen Callback cuando se hace click en la card
 * @param onToggleFavorite Callback para marcar/desmarcar como favorito
 * @param onSwipeToDelete Callback cuando se completa el gesto de swipe (muestra diálogo)
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SwipeableNoteCard(
    note: Note,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSwipeToDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado del swipe para esta nota específica
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                // Al completar el swipe, disparar el callback
                onSwipeToDelete()
                false // NO consumir el dismiss; la card vuelve a su posición
            } else {
                false
            }
        },
        // Umbral del 33% del ancho para activar el swipe
        positionalThreshold = { distance -> distance * 0.33f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false, // NO permitir swipe izquierda → derecha
        enableDismissFromEndToStart = true,  // Solo permitir swipe derecha → izquierda
        backgroundContent = {
            SwipeDeleteBackground(progress = dismissState.progress)
        },
        content = {
            NoteCard(
                note = note,
                onOpen = onOpen,
                onToggleFavorite = onToggleFavorite
            )
        }
    )
}

/**
 * Fondo rojo con icono de papelera que se muestra durante el swipe.
 * 
 * @param progress Progreso del gesto de swipe (0.0 a 1.0)
 */
@Composable
private fun SwipeDeleteBackground(progress: Float) {
    // Coercer el progreso entre 0 y 1 para evitar valores negativos
    val normalizedProgress = progress.coerceIn(0f, 1f)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        // Fondo rojo que aparece gradualmente con el progreso del gesto
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Red.copy(alpha = 0.6f * normalizedProgress)
            )
        ) { /* Card vacía solo para el fondo */ }

        // Icono de papelera que se desvanece con el progreso
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White.copy(alpha = normalizedProgress),
            modifier = Modifier
                .padding(end = 20.dp)
                .size(28.dp)
        )
    }
}
