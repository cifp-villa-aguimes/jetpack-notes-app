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
import edu.dam.notesapptyped.theme.animatedFavoriteColor
import edu.dam.notesapptyped.ui.common.formatNoteTimestamp
import edu.dam.notesapptyped.ui.detail.components.EditNoteSheet
import edu.dam.notesapptyped.ui.notes.NotesViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    nav: NavController,
    notesViewModel: NotesViewModel,
    id: String
) {
    val noteState by notesViewModel.observeNote(id).collectAsState(initial = null)
    val currentNote = noteState

    val shortId = remember(id) { id.take(8).uppercase() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showConfirm by remember { mutableStateOf(false) }

    // --- Edit sheet state ---
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showEdit by remember { mutableStateOf(false) }
    var editTitle by rememberSaveable(currentNote?.title) { mutableStateOf(currentNote?.title.orEmpty()) }
    var editBody by rememberSaveable(currentNote?.body) { mutableStateOf(currentNote?.body.orEmpty()) }

    // formateo fecha
    val prettyDate = remember(currentNote?.updatedAt) {
        currentNote?.let { formatNoteTimestamp(it.updatedAt) } ?: ""
    }

    // color animado para la estrella
    val starTint = animatedFavoriteColor(currentNote?.isFavorite == true)

    // Si la nota cambia (navegaste a otra, etc.), refresca los campos
    LaunchedEffect(currentNote?.id) {
        editTitle = currentNote?.title.orEmpty()
        editBody = currentNote?.body.orEmpty()
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
                    currentNote?.let { note ->
                        IconButton(onClick = {
                            scope.launch {
                                val success = notesViewModel.toggleFavorite(note.id)
                                if (!success) {
                                    snackbarHostState.showSnackbar("No se pudo guardar")
                                }
                            }
                        }) {
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
            if (currentNote == null) {
                Text("Nota no encontrada", style = MaterialTheme.typography.titleLarge)
                OutlinedButton(onClick = { nav.popBackStack() }) { Text("Volver") }
            } else {
                val note = currentNote
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
    if (showEdit && currentNote != null) {
        val note = currentNote
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
                scope.launch {
                    val success = notesViewModel.updateNote(
                        id = note.id,
                        title = editTitle.trim(),
                        body = editBody.trim().ifBlank { null }
                    )
                    if (success) {
                        showEdit = false
                        snackbarHostState.showSnackbar("Nota actualizada")
                    } else {
                        snackbarHostState.showSnackbar("No se pudo guardar")
                    }
                }
            },
            onDismissRequest = { showEdit = false }
        )
    }

    // Confirmación de borrado
    if (showConfirm && currentNote != null) {
        val note = currentNote
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Eliminar nota") },
            text = { Text("¿Seguro que quieres eliminarla? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val success = notesViewModel.deleteNote(note.id)
                        showConfirm = false
                        if (success) {
                            nav.popBackStack()
                            snackbarHostState.showSnackbar("Nota eliminada")
                        } else {
                            snackbarHostState.showSnackbar("No se pudo eliminar")
                        }
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}

