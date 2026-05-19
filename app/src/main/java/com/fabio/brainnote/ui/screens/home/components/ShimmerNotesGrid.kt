package com.fabio.brainnote.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerNotesGrid(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    val heights = listOf(200.dp, 240.dp, 180.dp, 220.dp, 200.dp, 260.dp)

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(
            items = (0 until itemCount).toList(),
            key = { it }
        ) { index ->
            ShimmerNoteCard(
                modifier = Modifier.fillMaxWidth(),
                cardHeight = heights[index % heights.size]
            )
        }
    }
}