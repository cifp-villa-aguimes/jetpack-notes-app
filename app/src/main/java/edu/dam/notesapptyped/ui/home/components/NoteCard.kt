package edu.dam.notesapptyped.ui.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.dam.notesapptyped.data.model.Note
import edu.dam.notesapptyped.theme.animatedFavoriteColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun NoteCard(
    note: Note,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    }
    val prettyDate = remember(note.updatedAt) {
        Instant.ofEpochMilli(note.updatedAt)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }

    // Color animado para el icono favorito
    val starColor = animatedFavoriteColor(note.isFavorite)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        onClick = onOpen
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .heightIn(min = 120.dp)
        ) {
            // TOP: título + estrella
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(note.title, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (note.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (note.isFavorite) "Quitar de favoritos" else "Marcar como favorito",
                        tint = starColor
                    )
                }
            }

            // MIDDLE: cuerpo (opcional)
            if (note.body.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(note.body, style = MaterialTheme.typography.bodyMedium)
            }

            // EMPUJA el footer al fondo del card
            Spacer(Modifier.weight(1f, fill = true))

            // BOTTOM: meta + eliminar
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "por ${note.author} • $prettyDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar nota",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}