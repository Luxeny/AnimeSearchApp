package com.example.animesearchapp.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.animesearchapp.domain.model.Genre

@Composable
fun GenreChipsRow(
    genres: List<Genre>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    if (genres.isEmpty()) return
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        genres.forEach { g ->
            AssistChip(
                onClick = { },
                label = { Text(g.displayName) }
            )
        }
    }
}
