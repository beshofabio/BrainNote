package com.fabio.brainnote.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fabio.brainnote.ui.screens.ScreenState
import com.fabio.brainnote.ui.components.CategoriesLazyRow
import com.fabio.brainnote.ui.screens.home.components.ElegantSearchBar
import com.fabio.brainnote.ui.screens.home.components.LinkableNoteGridItem
import com.fabio.brainnote.ui.screens.home.components.ShimmerNotesGrid
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onNoteClick: (Long) -> Unit,
    onNoteLongPress: (Long) -> Unit,
    onNoteToggleForLinking: (Long) -> Unit,
    onConfirmLinking: () -> Unit,
    onCancelLinking: () -> Unit,
    onCategorySelected: (Long) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onAddNoteClick: () -> Unit,
    editorContent: @Composable (closeEditor: () -> Unit) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

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

    BackHandler(enabled = uiState.isLinkingMode) {
        onCancelLinking()
    }
    BackHandler(enabled = isExpanded && !uiState.isLinkingMode) {
        scope.launch { draggableState.animateTo(ScreenState.Collapsed) }
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
                        containerColor = colorScheme.background
                    ),
                    title = {
                        if (uiState.isLinkingMode) {
                            Text(
                                text = "Select notes to link",
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "Note",
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        if (uiState.isLinkingMode) {
                            IconButton(onClick = onCancelLinking) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel linking",
                                    tint = colorScheme.onSurface
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                if (!uiState.isLinkingMode) {
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
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ) {
                        if (progress > 0.5f) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        } else {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
                        }
                    }
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = uiState.isLinkingMode,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    BottomAppBar(
                        containerColor = colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                            Text(
                                text = if (uiState.selectedForLinking.isEmpty()) {
                                    "Tap notes to select"
                                } else {
                                    "${uiState.selectedForLinking.size} selected"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = onCancelLinking) {
                                Text("Cancel", color = colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                            Button(
                                onClick = onConfirmLinking,
                                enabled = uiState.selectedForLinking.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.primary
                                ),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = if (uiState.selectedForLinking.isEmpty()) "Link"
                                    else "Link ${uiState.selectedForLinking.size}",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
                    .padding(paddingValues)
            ) {
                if (!uiState.isLinkingMode) {
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
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Long-pressed note will be the cluster root. Tap other notes to link them.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

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
                                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        else -> {
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 80.dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalItemSpacing = 12.dp
                            ) {
                                items(uiState.filteredNotes, key = { it.id }) { note ->
                                    LinkableNoteGridItem(
                                        modifier = Modifier.fillMaxWidth(),
                                        note = note,
                                        isLinkingMode = uiState.isLinkingMode,
                                        isRootNote = note.id == uiState.linkingRootNoteId,
                                        isSelectedForLinking = note.id in uiState.selectedForLinking,
                                        hasSelectedCluster = uiState.hasSelectedCluster,
                                        onClick = { id ->
                                            if (uiState.isLinkingMode) onNoteToggleForLinking(id)
                                            else onNoteClick(id)
                                        },
                                        onLongPress = { id -> onNoteLongPress(id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}