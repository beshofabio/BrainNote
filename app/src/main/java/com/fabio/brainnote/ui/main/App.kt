package com.fabio.brainnote.ui.main

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fabio.brainnote.navigations.NoteEditorRoute
import com.fabio.brainnote.navigations.Screen
import com.fabio.brainnote.navigations.clusterComposable
import com.fabio.brainnote.navigations.homeComposable

@Composable
fun BrainNoteApp() {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(200)) + slideIntoContainer(
                    animationSpec = tween(500, easing = {
                        OvershootInterpolator(1.2f).getInterpolation(it)
                    }),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                    animationSpec = tween(300, easing = FastOutLinearInEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            homeComposable(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )

            composable(
                route = "${Screen.NoteEditorScreen.route}/{noteId}?rootNoteId={rootNoteId}",
                arguments = listOf(
                    navArgument("noteId") { type = NavType.LongType },
                    navArgument("rootNoteId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                NoteEditorRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }

            clusterComposable(navController = navController)
        }
    }
}