package edu.dam.notesapptyped.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import edu.dam.notesapptyped.data.AppState
import edu.dam.notesapptyped.ui.detail.DetailScreen
import edu.dam.notesapptyped.ui.home.HomeScreen
import edu.dam.notesapptyped.ui.login.LoginScreen
import edu.dam.notesapptyped.ui.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController, state: AppState) {
    NavHost(navController, startDestination = Login) {
        composable<Login> { LoginScreen(navController, state) }
        composable<Home> { HomeScreen(navController, state) }
        composable<Favorites> {
            HomeScreen(
                nav = navController,
                state = state,
                onlyFavorites = true
            )
        }
        composable<Settings> { SettingsScreen(navController, state) }
        composable<Detail> { backStack ->
            val args = backStack.toRoute<Detail>()  // args.id
            DetailScreen(navController, state, id = args.id)
        }
    }
}