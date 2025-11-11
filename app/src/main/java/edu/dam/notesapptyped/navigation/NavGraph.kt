package edu.dam.notesapptyped.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import edu.dam.notesapptyped.data.AppState
import edu.dam.notesapptyped.data.prefs.UserPrefsRepository
import edu.dam.notesapptyped.ui.detail.DetailScreen
import edu.dam.notesapptyped.ui.home.HomeScreen
import edu.dam.notesapptyped.ui.login.LoginScreen
import edu.dam.notesapptyped.ui.settings.SettingsScreen
import edu.dam.notesapptyped.ui.notes.NotesViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    state: AppState,
    prefs: UserPrefsRepository,
    notesViewModel: NotesViewModel
) {
    NavHost(navController, startDestination = Login) {
        composable<Login> { LoginScreen(navController, state, prefs) }
        composable<Home> { HomeScreen(navController, state, prefs, notesViewModel) }
        composable<Favorites> {
            HomeScreen(
                nav = navController,
                state = state,
                prefs = prefs,
                notesViewModel = notesViewModel,
                onlyFavorites = true
            )
        }
        composable<Settings> { SettingsScreen(navController, state, prefs) }
        composable<Detail> { backStack ->
            val args = backStack.toRoute<Detail>()  // args.id
            DetailScreen(navController, notesViewModel, id = args.id)
        }
    }
}