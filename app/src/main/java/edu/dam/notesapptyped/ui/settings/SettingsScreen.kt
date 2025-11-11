package edu.dam.notesapptyped.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.dam.notesapptyped.data.AppState
import edu.dam.notesapptyped.data.prefs.SortBy
import edu.dam.notesapptyped.data.prefs.UserPrefsRepository
import edu.dam.notesapptyped.navigation.Settings
import edu.dam.notesapptyped.ui.components.AppBottomBar
import edu.dam.notesapptyped.ui.common.USERNAME_MAX
import edu.dam.notesapptyped.ui.common.USERNAME_MIN
import edu.dam.notesapptyped.ui.common.UserNameSupportingText
import edu.dam.notesapptyped.ui.common.validateUserName
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    nav: NavController,
    state: AppState,
    prefs: UserPrefsRepository
) {
    val currentName by state.userName.collectAsState()
    val savedName by prefs.userNameFlow.collectAsState(initial = "")
    val currentDark by prefs.darkModeFlow.collectAsState(initial = false)
    val currentSort by prefs.sortByFlow.collectAsState(initial = SortBy.DATE)
    val scope = rememberCoroutineScope()
    var tempName by rememberSaveable { mutableStateOf("") }
    var tempDark by rememberSaveable { mutableStateOf(false) }

    // Prefill inicial desde DataStore (y sincronía si cambia)
    LaunchedEffect(savedName) { tempName = savedName }
    LaunchedEffect(currentDark) { tempDark = currentDark }

    // Validación
    val validation = remember(tempName) { validateUserName(tempName) }
    val trimmed = validation.trimmed
    val lengthOk = validation.lengthAllowed
    val charsetOk = validation.charsetAllowed
    val hasChanges = trimmed != currentName
    val canSave = trimmed.isNotEmpty() && lengthOk && charsetOk && hasChanges

    val focus = LocalFocusManager.current

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = { AppBottomBar(nav = nav, current = Settings) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Perfil", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = tempName,
                onValueChange = { tempName = it },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                isError = tempName.isNotEmpty() && (!lengthOk || !charsetOk),
                supportingText = { UserNameSupportingText(validation) },
                trailingIcon = {
                    if (tempName.isNotEmpty()) {
                        IconButton(onClick = { tempName = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (canSave) {
                            state.userName.value = trimmed
                            focus.clearFocus()
                            nav.popBackStack()
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Botones
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        state.userName.value = trimmed
                        scope.launch { prefs.setUserName(trimmed) }
                        focus.clearFocus()
                        nav.popBackStack()
                    },
                    enabled = canSave,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar y volver") }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { tempName = currentName }, // revertir cambios locales
                        enabled = hasChanges,
                        modifier = Modifier.weight(1f)
                    ) { Text("Revertir") }

                    OutlinedButton(
                        onClick = { tempName = "" },
                        modifier = Modifier.weight(1f)
                    ) { Text("Limpiar") }
                }

                TextButton(
                    onClick = { focus.clearFocus(); nav.popBackStack() },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Cancelar") }
            }

            SettingsSection(
                title = "Orden de notas",
                subtitle = "Define cómo se listan tus notas en la pantalla principal"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortOption(
                        label = "Por fecha (recientes primero)",
                        selected = currentSort == SortBy.DATE,
                        onSelect = { scope.launch { prefs.setSortBy(SortBy.DATE) } }
                    )
                    SortOption(
                        label = "Por título (A–Z)",
                        selected = currentSort == SortBy.TITLE,
                        onSelect = { scope.launch { prefs.setSortBy(SortBy.TITLE) } }
                    )
                    SortOption(
                        label = "Favoritas primero",
                        selected = currentSort == SortBy.FAVORITE,
                        onSelect = { scope.launch { prefs.setSortBy(SortBy.FAVORITE) } }
                    )
                }
            }

            SettingsSection(
                title = "Tema de la aplicación",
                subtitle = "Activa el modo oscuro en todos los apartados"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Tema oscuro", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Afecta a las pantallas Home, Favoritos y Ajustes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = tempDark,
                        onCheckedChange = { checked ->
                            tempDark = checked
                            scope.launch { prefs.setDarkMode(checked) }
                        }
                    )
                }
            }

            SettingsSection(
                title = "Mensaje de bienvenida",
                subtitle = "Controla si quieres volver a ver la introducción"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Column(Modifier.weight(1f)) {
                        Text(
                            "Vuelve a mostrar el diálogo en el próximo inicio.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(onClick = { scope.launch { prefs.setWelcomeShown(false) } }) {
                        Text("Reiniciar")
                    }
                }
            }
        }
    }
}

@Composable
private fun SortOption(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect
        )
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
        }
    }
}