package edu.dam.notesapptyped.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import edu.dam.notesapptyped.navigation.*

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppBottomBar(
    nav: NavController,
    current: String
) {
    val items = listOf(
        BottomItem(ROUTE_HOME, "Inicio", Icons.Filled.Home),
        BottomItem(ROUTE_FAVORITES, "Favoritos", Icons.Filled.Favorite),
        BottomItem(ROUTE_SETTINGS, "Ajustes", Icons.Filled.Settings),
    )

    NavigationBar {
        items.forEach { item ->
            val selected = current == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        nav.navigate(item.route) {
                            // evita duplicados si ya estás en ese destino
                            launchSingleTop = true
                            // restaura estado al volver
                            restoreState = true
                            // deja Home como “root” guardando estados de pestañas
                            popUpTo(ROUTE_HOME) { saveState = true }
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}