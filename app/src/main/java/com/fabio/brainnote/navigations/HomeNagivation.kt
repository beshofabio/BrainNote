package com.fabio.brainnote.navigations

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fabio.brainnote.ui.screens.home.HomeScreen
import com.fabio.brainnote.ui.screens.home.HomeViewModel
import com.fabio.brainnote.ui.screens.noteedit.NoteEditorViewModel

fun NavGraphBuilder.homeComposable(
    navController: NavController,
    modifier: Modifier = Modifier,
){
    composable(Screen.HomeScreen.route) {
        val homeScreenViewModel: HomeViewModel = hiltViewModel()
        val homeState by homeScreenViewModel.uiState.collectAsStateWithLifecycle()

        HomeScreen(
            modifier = modifier,
            uiState = homeState,
            onNoteClick = { noteId ->
                Log.d("NoteClick", "homeComposable: ${noteId}")
                navController.navigate("${Screen.NoteEditorScreen.route}/$noteId")
            },
            onCategorySelected = homeScreenViewModel::onCategorySelected,
            onSearchQueryChanged = homeScreenViewModel::onSearchQueryChanged,
            onAddNoteClick = { },

            editorContent = { closeEditorCallback ->
                val quickAddViewModel: NoteEditorViewModel = hiltViewModel()
                NoteEditorRoute(
                    viewModel = quickAddViewModel,
                    onBackClick = {
                        quickAddViewModel.resetState()
                        closeEditorCallback()
                    }
                )
            }
        )
    }
}