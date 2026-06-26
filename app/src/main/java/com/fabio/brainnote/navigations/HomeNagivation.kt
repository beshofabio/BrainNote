package com.fabio.brainnote.navigations

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fabio.brainnote.ui.screens.home.HomeScreen
import com.fabio.brainnote.ui.screens.home.HomeViewModel
import com.fabio.brainnote.ui.screens.noteedit.NoteEditorViewModel

fun NavGraphBuilder.homeComposable(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    composable(Screen.HomeScreen.route) {
        val homeScreenViewModel: HomeViewModel = hiltViewModel()
        val homeState by homeScreenViewModel.uiState.collectAsStateWithLifecycle()

        HomeScreen(
            modifier = modifier,
            uiState = homeState,
            onNoteClick = { noteId ->
                val clickedNote = homeState.filteredNotes.find { it.id == noteId }
                if (clickedNote != null && clickedNote.linkedNotes.isNotEmpty()) {
                    navController.navigate("${Screen.ClusterScreen.route}/$noteId")
                } else {
                    navController.navigate("${Screen.NoteEditorScreen.route}/$noteId")
                }
            },
            onNoteLongPress = homeScreenViewModel::onNoteLongPress,
            onNoteToggleForLinking = homeScreenViewModel::onNoteToggleForLinking,
            onConfirmLinking = homeScreenViewModel::onConfirmLinking,
            onCancelLinking = homeScreenViewModel::onCancelLinking,
            onCategorySelected = homeScreenViewModel::onCategorySelected,
            onSearchQueryChanged = homeScreenViewModel::onSearchQueryChanged,
            onAddNoteClick = { },
            editorContent = { closeEditorCallback ->
                NoteEditorRoute(
                    onBackClick = {
                        closeEditorCallback()
                    }
                )
            }
        )
    }
}