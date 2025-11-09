package edu.dam.notesapptyped.navigation

import kotlinx.serialization.Serializable

// Todas las rutas
sealed interface AppRoute

// Subconjunto: destinos raíz que irán en la bottom bar
sealed interface RootDestination : AppRoute

@Serializable
data object Login : AppRoute

@Serializable
data object Home : RootDestination

@Serializable
data object Favorites : RootDestination

@Serializable
data object Settings : RootDestination

@Serializable
data class Detail(val id: String) : AppRoute