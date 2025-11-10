package edu.dam.notesapptyped.navigation

const val ROUTE_LOGIN = "login"
const val ROUTE_HOME = "home"
const val ROUTE_FAVORITES = "favorites"
const val ROUTE_SETTINGS = "settings"
const val ROUTE_DETAIL = "detail/{id}"
fun detailRoute(id: String) = "detail/$id"
