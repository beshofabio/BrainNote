package com.fabio.brainnote.navigations

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fabio.brainnote.ui.screens.cluster.ClusterScreen
import com.fabio.brainnote.ui.screens.cluster.ClusterViewModel

fun NavGraphBuilder.clusterComposable(
    navController: NavController
) {
    composable(
        route = "${Screen.ClusterScreen.route}/{rootNoteId}",
        arguments = listOf(
            navArgument("rootNoteId") { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val rootNoteId = backStackEntry.arguments?.getLong("rootNoteId") ?: 0L
        val viewModel: ClusterViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        ClusterScreen(
            uiState = uiState,
            rootNoteId = rootNoteId,
            onBackClick = navController::navigateUp,
            dismissUnlinkDialog = viewModel::dismissUnlinkDialog,
            onConfirmUnlink = viewModel::confirmUnlink,
            onNoteLongPressed = viewModel::onNoteLongPressed,
            onNoteClick = { noteId ->
                navController.navigate(
                    "${Screen.NoteEditorScreen.route}/$noteId?rootNoteId=$rootNoteId"
                )
            }
        )
    }
}