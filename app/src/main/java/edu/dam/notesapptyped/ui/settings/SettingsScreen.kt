package edu.dam.notesapptyped.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
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
import edu.dam.notesapptyped.navigation.ROUTE_SETTINGS
import edu.dam.notesapptyped.ui.components.AppBottomBar

private const val NAME_MIN = 3
private const val NAME_MAX = 30
private val NAME_REGEX = Regex("""^[\p{L}\p{N}_\- ]+$""") // letras/números/espacio/_-

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    nav: NavController,
    state: AppState
) {
    val currentName by state.userName.collectAsState()
    var tempName by rememberSaveable { mutableStateOf("") }

    // Prefill y sincronía si cambia el nombre global
    LaunchedEffect(currentName) { tempName = currentName }

    // Validación
    val trimmed = remember(tempName) { tempName.trim() }
    val lengthOk = trimmed.length in NAME_MIN..NAME_MAX
    val charsetOk =
        trimmed.isEmpty() || NAME_REGEX.matches(trimmed) // permite vacío mientras escribes
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
        bottomBar = { AppBottomBar(nav = nav, current = ROUTE_SETTINGS) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
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
                supportingText = {
                    val count = "${trimmed.length} / $NAME_MAX"
                    when {
                        tempName.isEmpty() -> Text(count)
                        !lengthOk -> Text("Entre $NAME_MIN y $NAME_MAX caracteres · $count")
                        !charsetOk -> Text("Solo letras, números, espacios, _ y - · $count")
                        else -> Text(count)
                    }
                },
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
        }
    }
}