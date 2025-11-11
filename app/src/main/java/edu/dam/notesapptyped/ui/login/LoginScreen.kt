package edu.dam.notesapptyped.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.dam.notesapptyped.data.AppState
import edu.dam.notesapptyped.data.prefs.UserPrefsRepository
import edu.dam.notesapptyped.navigation.Home
import edu.dam.notesapptyped.navigation.Login
import kotlinx.coroutines.launch

private const val NAME_MIN = 3
private const val NAME_MAX = 30
private val NAME_REGEX = Regex("""^[\p{L}\p{N}_\- ]+$""")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(nav: NavController, state: AppState, prefs: UserPrefsRepository) {
    // --- Nick ---
    var nick by rememberSaveable { mutableStateOf("") }
    val trimmed = remember(nick) { nick.trim() }
    val lengthOk = trimmed.length in NAME_MIN..NAME_MAX
    val charsetOk = trimmed.isEmpty() || NAME_REGEX.matches(trimmed)
    val nickOk = trimmed.isNotEmpty() && lengthOk && charsetOk

    // Precargar desde DataStore si existe
    val savedName by prefs.userNameFlow.collectAsState(initial = "")
    LaunchedEffect(savedName) {
        if (nick.isEmpty() && savedName.isNotEmpty()) nick = savedName
    }

    // --- Captcha (+ / -) sin resultado negativo en '-'
    data class Captcha(val a: Int, val b: Int, val op: Char) {
        val result: Int = if (op == '+') a + b else a - b
        override fun toString() = "$a $op $b ="
    }

    fun newCaptcha(): Captcha {
        val plus = listOf(true, false).random()
        return if (plus) {
            Captcha((1..9).random(), (1..9).random(), '+')
        } else {
            val x = (1..9).random()
            val y = (1..x).random() // asegura x - y >= 0
            Captcha(x, y, '-')
        }
    }

    var cap by remember { mutableStateOf(newCaptcha()) }
    var answer by rememberSaveable { mutableStateOf("") }
    val capOk = answer.toIntOrNull() == cap.result

    val canEnter = nickOk && capOk
    val focus = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { CenterAlignedTopAppBar(title = { Text("Iniciar sesión") }) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Demostración de navegación + estado",
                style = MaterialTheme.typography.titleMedium
            )

            // Nick
            OutlinedTextField(
                value = nick,
                onValueChange = { nick = it },
                label = { Text("Tu nick/usuario") },
                singleLine = true,
                isError = nick.isNotEmpty() && (!lengthOk || !charsetOk),
                supportingText = {
                    when {
                        nick.isEmpty() -> Text("${trimmed.length} / $NAME_MAX")
                        !lengthOk -> Text("Entre $NAME_MIN y $NAME_MAX caracteres · ${trimmed.length} / $NAME_MAX")
                        !charsetOk -> Text("Solo letras, números, espacios, _ y - · ${trimmed.length} / $NAME_MAX")
                        else -> Text("${trimmed.length} / $NAME_MAX")
                    }
                },
                trailingIcon = {
                    if (nick.isNotEmpty()) {
                        IconButton(onClick = { nick = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpiar nick")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Captcha
            Text("Demuestra que eres humano 😉")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(cap.toString(), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    placeholder = { Text("resultado") },
                    singleLine = true,
                    isError = answer.isNotEmpty() && !capOk,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (canEnter) {
                                state.userName.value = trimmed
                                scope.launch { prefs.setUserName(trimmed) }
                                focus.clearFocus()
                                nav.navigate(Home) {
                                    popUpTo(Login) { inclusive = true }
                                }
                            }
                        }
                    ),
                    modifier = Modifier.width(140.dp)
                )
                IconButton(onClick = {
                    cap = newCaptcha()
                    answer = ""
                }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Nuevo ejercicio")
                }
            }

            // Entrar
            Button(
                enabled = canEnter,
                onClick = {
                    state.userName.value = trimmed
                    scope.launch { prefs.setUserName(trimmed) }
                    focus.clearFocus()
                    nav.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Entrar") }

            // Help de error
            if (!canEnter && (answer.isNotBlank() || nick.isNotBlank())) {
                val msg = when {
                    !nickOk -> "Revisa tu nick (longitud/caracteres)."
                    !capOk -> "Resultado incorrecto."
                    else -> ""
                }
                if (msg.isNotEmpty()) {
                    Text(
                        "❌ $msg",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}