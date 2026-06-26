package com.fabio.brainnote.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.fabio.brainnote.domain.model.Category
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesLazyRow(
    categories: ImmutableList<Category>,
    selectedCategoryId: Long,
    onCategoryClick: (Long) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories, key = { it.id }) { category ->
            FilterChip(
                selected = category.id == selectedCategoryId,
                onClick = { onCategoryClick(category.id) },
                label = { Text(category.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorScheme.primary,
                    selectedLabelColor = colorScheme.onPrimary,
                    containerColor = colorScheme.surface,
                    labelColor = colorScheme.onSurface
                ),
                shape = RoundedCornerShape(25.dp),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = category.id == selectedCategoryId,
                    borderColor = colorScheme.surfaceVariant,
                    selectedBorderColor = colorScheme.primary
                ),
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = ImageVector.vectorResource(category.icon),
                        contentDescription = "Category Icon",
                        tint = if (category.id == selectedCategoryId) colorScheme.onPrimary else colorScheme.onSurface
                    )
                }
            )
        }
    }
}