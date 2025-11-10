package edu.dam.notesapptyped.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import edu.dam.notesapptyped.data.AppState
import edu.dam.notesapptyped.ui.detail.DetailScreen
import edu.dam.notesapptyped.ui.home.HomeScreen
import edu.dam.notesapptyped.ui.login.LoginScreen
import edu.dam.notesapptyped.ui.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController, state: AppState) {
    NavHost(navController, startDestination = ROUTE_LOGIN) {
        composable(ROUTE_LOGIN) { LoginScreen(navController, state) }
        composable(ROUTE_HOME) { HomeScreen(navController, state) }
        composable(ROUTE_FAVORITES) {
            HomeScreen(
                nav = navController,
                state = state,
                onlyFavorites = true
            )
        }
        composable(ROUTE_SETTINGS) { SettingsScreen(navController, state) }
        composable(ROUTE_DETAIL) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            DetailScreen(navController, state, id)
        }
    }
}