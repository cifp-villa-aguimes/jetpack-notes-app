package edu.dam.notesapptyped.navigation

sealed class NavScreen(val route: String) {
    data object Login : NavScreen("login")
    data object Home : NavScreen("home")
    data object Favorites : NavScreen("favorites")
    data object Settings : NavScreen("settings")
    data object Detail : NavScreen("detail/{id}") {
        fun createRoute(id: String) = "detail/$id"
    }
}
