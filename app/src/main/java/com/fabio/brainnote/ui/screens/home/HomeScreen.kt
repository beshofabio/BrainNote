package com.fabio.brainnote.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fabio.brainnote.R
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.LinkedNote
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.model.fakeNote
import com.fabio.brainnote.ui.screens.ScreenState
import com.fabio.brainnote.ui.components.CategoriesLazyRow
import com.fabio.brainnote.ui.screens.home.components.ElegantSearchBar
import com.fabio.brainnote.ui.screens.home.components.NotesStaggeredGrid
import com.fabio.brainnote.ui.screens.home.components.ShimmerNotesGrid
import com.fabio.brainnote.ui.theme.BrainNoteTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onNoteClick: (Long) -> Unit,
    onCategorySelected: (Long) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onAddNoteClick: () -> Unit,
    editorContent: @Composable (closeEditor: () -> Unit) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val screenHeightPx = with(density) {
        (LocalConfiguration.current.screenHeightDp.dp * 1f).toPx()
    }

    val draggableState = remember {
        AnchoredDraggableState(initialValue = ScreenState.Collapsed)
    }

    draggableState.updateAnchors(
        DraggableAnchors {
            ScreenState.Collapsed at 0f
            ScreenState.Expanded at screenHeightPx
        }
    )

    val currentOffset = if (draggableState.offset.isNaN()) 0f else draggableState.offset
    val progress = if (screenHeightPx > 0f) (currentOffset / screenHeightPx).coerceIn(0f, 1f) else 0f

    val dragFlingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = draggableState,
        positionalThreshold = { distance -> distance * 0.5f },
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )

    val isExpanded = draggableState.targetValue == ScreenState.Expanded ||
            draggableState.currentValue == ScreenState.Expanded

    BackHandler(enabled = isExpanded) {
        scope.launch {
            draggableState.animateTo(ScreenState.Collapsed)
        }
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(progress)
                .scale(0.9f + (0.1f * progress))
        ) {
            editorContent {
                scope.launch { draggableState.animateTo(ScreenState.Collapsed) }
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, currentOffset.roundToInt()) },
            topBar = {
                TopAppBar(
                    modifier = Modifier.anchoredDraggable(
                        state = draggableState,
                        orientation = Orientation.Vertical,
                        flingBehavior = dragFlingBehavior
                    ),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    title = {
                        Text(
                            text = "Note",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            if (draggableState.currentValue == ScreenState.Collapsed) {
                                draggableState.animateTo(ScreenState.Expanded)
                            } else {
                                draggableState.animateTo(ScreenState.Collapsed)
                            }
                        }
                        onAddNoteClick()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    if (progress > 0.5f) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    } else {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                ElegantSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                CategoriesLazyRow(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.selectedCategoryId,
                    onCategoryClick = onCategorySelected
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        uiState.isLoading -> ShimmerNotesGrid()
                        uiState.filteredNotes.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No notes in this category yet.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        else -> NotesStaggeredGrid(
                            notes = uiState.filteredNotes,
                            onNoteClick = onNoteClick
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Home Screen - Populated")
@Composable
fun HomeScreenPreview() {
    val mockCategories = listOf(
        Category(id = 0, name = "Work", color = 0xFF4285F4, icon = R.drawable.work),
        Category(id = 1, name = "Ideas", color = 0xFFFBBC05, icon = R.drawable.ideas),
        Category(id = 2, name = "Plans", color = 0xFF34A853, icon = R.drawable.plans),
        Category(id = 3, name = "Birthday", color = 0xFFEA4335, icon = R.drawable.birthday),
        Category(id = 4, name = "Others", color = 0xFF70757A, icon = R.drawable.others)
    )

    val shortNote = Note(
        id = 2,
        title = "Groceries",
        content = "Buy milk, eggs, and coffee beans.",
        category = mockCategories[0],
        colorPriority = 0xFF4285F4.toInt(),
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    val clusterNote = Note(
        id = 3,
        title = "Project BrainNote",
        content = "Main folder for app architecture and design notes.",
        category = mockCategories[0],
        colorPriority = 0xFF34A853.toInt(),
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        linkedNotes = listOf(
            LinkedNote(id = 101, title = "Database Schema", imagePath = null),
            LinkedNote(id = 102, title = "UI Color Palette", imagePath = null),
            LinkedNote(id = 103, title = "Feature List", imagePath = null),
            LinkedNote(id = 104, title = "Marketing Ideas", imagePath = null)
        )
    )

    val mockNotes = listOf(fakeNote, shortNote, clusterNote)

    val uiState = HomeUiState(
        isLoading = false,
        categories = mockCategories,
        filteredNotes = mockNotes,
        selectedCategoryId = 1L
    )

    BrainNoteTheme {
        HomeScreen(
            uiState = uiState,
            onNoteClick = {},
            onCategorySelected = {},
            onAddNoteClick = {},
            onSearchQueryChanged = {},
            editorContent = {}
        )
    }
}


@Preview(showBackground = true, name = "Home Screen - Empty Category")
@Composable
fun HomeScreenEmptyPreview() {
    val mockCategories = listOf(
        Category(id = 0, name = "Work", color = 0xFF4285F4, icon = R.drawable.work),
        Category(id = 1, name = "Ideas", color = 0xFFFBBC05, icon = R.drawable.ideas),
        Category(id = 2, name = "Plans", color = 0xFF34A853, icon = R.drawable.plans),
        Category(id = 3, name = "Birthday", color = 0xFFEA4335, icon = R.drawable.birthday),
        Category(id = 4, name = "Others", color = 0xFF70757A, icon = R.drawable.others)
    )

    val uiState = HomeUiState(
        isLoading = false,
        categories = mockCategories,
        filteredNotes = emptyList(),
        selectedCategoryId = 1L
    )

    BrainNoteTheme {
        HomeScreen(
            uiState = uiState,
            onNoteClick = {},
            onCategorySelected = {},
            onAddNoteClick = {},
            onSearchQueryChanged = {},
            editorContent = {}
        )
    }
}

@Preview(showBackground = true, name = "Home Screen - Loading")
@Composable
fun HomeScreenLoadingPreview() {
    val uiState = HomeUiState(
        isLoading = true,
        categories = emptyList(),
        filteredNotes = emptyList(),
        selectedCategoryId = 0L
    )

    BrainNoteTheme {
        HomeScreen(
            uiState = uiState,
            onNoteClick = {},
            onCategorySelected = {},
            onAddNoteClick = {},
            onSearchQueryChanged = {},
            editorContent = {}
        )
    }
}

