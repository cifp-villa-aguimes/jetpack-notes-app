package edu.dam.notesapptyped.ui.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.dam.notesapptyped.data.AppState
import edu.dam.notesapptyped.theme.animatedFavoriteColor
import edu.dam.notesapptyped.ui.detail.components.EditNoteSheet
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    nav: NavController,
    state: AppState,
    id: String
) {
    val notes by state.notes.collectAsState()
    val note = notes.firstOrNull { it.id == id }

    val shortId = remember(id) { id.take(8).uppercase() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showConfirm by remember { mutableStateOf(false) }

    // --- Edit sheet state ---
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showEdit by remember { mutableStateOf(false) }
    var editTitle by rememberSaveable(note?.title) { mutableStateOf(note?.title.orEmpty()) }
    var editBody by rememberSaveable(note?.body) { mutableStateOf(note?.body.orEmpty()) }

    // formateo fecha
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm") }
    val prettyDate = remember(note?.updatedAt) {
        note?.let {
            Instant.ofEpochMilli(it.updatedAt)
                .atZone(ZoneId.systemDefault())
                .format(formatter)
        } ?: ""
    }

    // color animado para la estrella
    val starTint = animatedFavoriteColor(note?.isFavorite == true)

    // Si la nota cambia (navegaste a otra, etc.), refresca los campos
    LaunchedEffect(note?.id) {
        editTitle = note?.title.orEmpty()
        editBody = note?.body.orEmpty()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle #$shortId") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (note != null) {
                        IconButton(onClick = { state.toggleFavorite(note.id) }) {
                            Icon(
                                imageVector = if (note.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = if (note.isFavorite) "Quitar de favoritos" else "Marcar como favorito",
                                tint = starTint
                            )
                        }
                        IconButton(onClick = { showEdit = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { showConfirm = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Eliminar nota",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (note == null) {
                Text("Nota no encontrada", style = MaterialTheme.typography.titleLarge)
                OutlinedButton(onClick = { nav.popBackStack() }) { Text("Volver") }
            } else {
                // Título
                Text(note.title, style = MaterialTheme.typography.headlineSmall)

                // Meta
                Text(
                    text = "por ${note.author} • $prettyDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                // Cuerpo
                if (note.body.isNotBlank()) {
                    Text(note.body, style = MaterialTheme.typography.bodyLarge)
                } else {
                    Text(
                        "Esta nota no tiene contenido.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    // --- Sheet de edición (state hoisting) ---
    if (showEdit && note != null) {
        EditNoteSheet(
            sheetState = sheetState,
            title = editTitle,
            body = editBody,
            onTitleChange = { editTitle = it },
            onBodyChange = { editBody = it },
            onCancel = {
                showEdit = false
                editTitle = note.title
                editBody = note.body
            },
            onSave = {
                state.updateNote(
                    id = note.id,
                    title = editTitle.trim(),
                    body = editBody.trim()
                )
                showEdit = false
                scope.launch { snackbarHostState.showSnackbar("Nota actualizada") }
            },
            onDismissRequest = { showEdit = false }
        )
    }

    // Confirmación de borrado
    if (showConfirm && note != null) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Eliminar nota") },
            text = { Text("¿Seguro que quieres eliminarla? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    state.deleteNote(note.id)
                    showConfirm = false
                    nav.popBackStack()
                    scope.launch { snackbarHostState.showSnackbar("Nota eliminada") }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}

