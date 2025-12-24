package com.example.animesearchapp.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.animesearchapp.domain.model.Anime

@Composable
fun AnimeCard(
    anime: Anime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = anime.imagePreviewUrl,
                contentDescription = anime.displayTitle,
                modifier = Modifier
                    .width(90.dp)
                    .height(128.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                Text(anime.displayTitle, style = MaterialTheme.typography.titleMedium)
                if (anime.genres.isNotEmpty()) {
                    Text(
                        anime.genres.joinToString(", ") { it.displayName },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                val meta = buildString {
                    if (anime.score != null) append("Score: ${anime.score}")
                    if (!anime.status.isNullOrBlank()) {
                        if (isNotEmpty()) append(" • ")
                        append(anime.status)
                    }
                }
                if (meta.isNotBlank()) {
                    Text(meta, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}
