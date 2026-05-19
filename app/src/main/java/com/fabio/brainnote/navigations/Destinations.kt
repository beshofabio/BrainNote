package com.fabio.brainnote.navigations

sealed class Screen(val route: String, val title: String) {
    data object SplashScreen : Screen(route = "SplashScreen", title = "Splash")
    data object HomeScreen : Screen(route = "HomeScreen", title = "Home")
    data object NoteEditorScreen : Screen(route = "NoteEditorScreen", title = "NoteEditor")

}
