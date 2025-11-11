package edu.dam.notesapptyped.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.dam.notesapptyped.data.AppState
import edu.dam.notesapptyped.data.prefs.SortBy
import edu.dam.notesapptyped.data.prefs.UserPrefsRepository
import edu.dam.notesapptyped.navigation.Detail
import edu.dam.notesapptyped.navigation.Favorites
import edu.dam.notesapptyped.navigation.Home
import edu.dam.notesapptyped.navigation.Login
import edu.dam.notesapptyped.ui.components.AppBottomBar
import edu.dam.notesapptyped.ui.home.components.AddNoteSheet
import edu.dam.notesapptyped.ui.home.components.SwipeableNoteCard
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nav: NavController,
    state: AppState,
    prefs: UserPrefsRepository,
    onlyFavorites: Boolean = false
) {
    // Conectar los StateFlow a Compose
    val name by state.userName.collectAsState()
    val notes by state.notes.collectAsState()
    val sortBy by prefs.sortByFlow.collectAsState(initial = SortBy.DATE)
    val welcomeShown by prefs.welcomeShownFlow.collectAsState(initial = false)

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var toDeleteId by remember { mutableStateOf<String?>(null) }  // id pendiente de confirmar
    var loggingOut by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    var newTitle by rememberSaveable { mutableStateOf("") }
    var newBody by rememberSaveable { mutableStateOf("") }

    fun resetForm() {
        newTitle = ""
        newBody = ""
    }
    // Diálogo de bienvenida (solo cuando aún no se ha mostrado)
    if (!welcomeShown && !onlyFavorites) {
        AlertDialog(
            onDismissRequest = {
                scope.launch { prefs.setWelcomeShown(true) }
            },
            title = { Text("¡Bienvenido!") },
            text = {
                Text("Esta app demuestra navegación tipada, estado con Flow y persistencia.")
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { prefs.setWelcomeShown(true) }
                }) {
                    Text("Entendido")
                }
            }
        )
    }

    // Título dinámico
    val topTitle by remember(onlyFavorites, name) {
        mutableStateOf(
            (if (onlyFavorites) "Favoritos" else "Notas") +
                    " — " + name.ifBlank { "invitado" }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(topTitle) },
                actions = {
                    IconButton(
                        onClick = {
                            nav.navigate(Login) {
                                // popUpTo indica hasta dónde “desapilar” la navegación
                                // Es decir: elimina del back stack todas las pantallas
                                // hasta llegar a Home (sin incluirla, por defecto).
                                // Así evitamos que al pulsar atrás desde Login,
                                // el usuario vuelva a Home.
                                popUpTo(Home) {
                                    inclusive = true
                                    // inclusive = true → también elimina la pantalla Home.
                                    // En este caso, Home se borra completamente de la pila.
                                    // Resultado: Login se convierte en la pantalla raíz.
                                }
                            }
                            loggingOut = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!onlyFavorites) {
                FloatingActionButton(onClick = { showSheet = true }) { Text("+") }
            }
        },
        bottomBar = { AppBottomBar(nav = nav, current = if (onlyFavorites) Favorites else Home) }
    ) { innerPadding ->
        val sortedNotes by remember(notes, onlyFavorites, sortBy) {
            derivedStateOf {
                val base = if (onlyFavorites) notes.filter { it.isFavorite } else notes
                when (sortBy) {
                    SortBy.DATE -> base.sortedByDescending { it.updatedAt }
                    SortBy.TITLE -> base.sortedBy { it.title.lowercase() }
                    SortBy.FAVORITE -> base.sortedWith(
                        compareByDescending<edu.dam.notesapptyped.data.model.Note> { it.isFavorite }
                            .thenByDescending { it.updatedAt }
                    )
                }
            }
        }
        val (emptyTitle, emptySubtitle) = remember(onlyFavorites) {
            if (onlyFavorites)
                "No hay favoritos aún" to "Marca notas con ★ para verlas aquí"
            else
                "Aún no hay notas" to "Pulsa + para crear la primera"
        }
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (sortedNotes.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(emptyTitle, style = MaterialTheme.typography.titleMedium)
                        Text(emptySubtitle, style = MaterialTheme.typography.bodyMedium)

                        if (!onlyFavorites) {
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(onClick = { showSheet = true }) { Text("Crear nota") }
                        }
                    }
                }
            } else {
                items(
                    items = sortedNotes,
                    key = { it.id },
                    contentType = { "note" }
                ) { note ->
                    SwipeableNoteCard(
                        note = note,
                        onOpen = { nav.navigate(Detail(id = note.id)) },
                        onToggleFavorite = { state.toggleFavorite(note.id) },
                        onSwipeToDelete = { toDeleteId = note.id },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
    // Sheet
    if (showSheet) {
        AddNoteSheet(
            sheetState = sheetState,
            title = newTitle,
            body = newBody,
            onTitleChange = { newTitle = it },
            onBodyChange = { newBody = it },
            onCancel = {
                showSheet = false
                resetForm()
            },
            onSave = { isFav ->
                state.addNote(newTitle.trim(), newBody.trim(), isFavorite = isFav)
                showSheet = false
                resetForm()
                scope.launch { snackbarHostState.showSnackbar("Nota creada") }
            },
            onDismissRequest = {
                showSheet = false
                resetForm()
            }
        )
    }
    // Diálogo de confirmación
    if (toDeleteId != null) {
        AlertDialog(
            onDismissRequest = { toDeleteId = null },
            title = { Text("Eliminar nota") },
            text = { Text("¿Seguro que quieres eliminar esta nota? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    val id = toDeleteId!!
                    state.deleteNote(id)
                    toDeleteId = null
                    scope.launch {
                        snackbarHostState.showSnackbar("Nota eliminada")
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { toDeleteId = null }) { Text("Cancelar") }
            }
        )
    }
    // Resetear el User Name
    DisposableEffect(Unit) {
        onDispose {
            if (loggingOut)
            // Se ejecuta cuando Home se elimina del árbol (tras navegar a Login)
                state.resetForLogout()

        }
    }
}
