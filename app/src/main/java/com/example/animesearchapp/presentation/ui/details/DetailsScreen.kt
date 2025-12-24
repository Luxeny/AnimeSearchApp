package com.example.animesearchapp.presentation.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.animesearchapp.presentation.ui.components.ErrorContent
import com.example.animesearchapp.presentation.ui.components.GenreChipsRow
import com.example.animesearchapp.presentation.ui.components.LoadingContent
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.example.animesearchapp.core.util.sanitizeDescription

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    onBack: () -> Unit,
    vm: DetailsViewModel = hiltViewModel()
) {
    val state = vm.uiState.collectAsStateWithLifecycle().value
    val snackHost = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.snackbar.collectLatest { snackHost.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        snackbarHost = { SnackbarHost(snackHost) }
    ) { padding ->
        when {
            state.isLoading && state.anime == null -> LoadingContent(Modifier.fillMaxSize().padding(padding))
            state.errorMessage != null && state.anime == null -> ErrorContent(
                title = state.errorMessage,
                actionLabel = "Обновить",
                onAction = { vm.onEvent(DetailsEvent.Refresh) },
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            state.anime != null -> {
                val a = state.anime
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // <-- add
                ) {
                    AsyncImage(
                        model = a!!.imageOriginalUrl.ifBlank { a.imagePreviewUrl },
                        contentDescription = a.displayTitle,
                        modifier = Modifier
                            .width(280.dp)
                            .height(360.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Text(a.displayTitle, style = MaterialTheme.typography.headlineSmall)
                    if (!a.name.equals(a.displayTitle, ignoreCase = true)) {
                        Text(a.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    GenreChipsRow(genres = a.genres, contentPadding = PaddingValues(0.dp))

                    Text(
                        text = buildString {
                            if (a.score != null) append("Score: ${a.score}\n")
                            if (!a.status.isNullOrBlank()) append("Статус: ${a.status}\n")
                            if (a.episodes != null || a.episodesAired != null) {
                                append("Эпизоды: ${a.episodesAired ?: "?"}/${a.episodes ?: "?"}\n")
                            }
                            if (!a.airedOn.isNullOrBlank()) append("Aired on: ${a.airedOn}\n")
                            if (!a.releasedOn.isNullOrBlank()) append("Released on: ${a.releasedOn}\n")
                        }.trim(),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    val cleanDescription = sanitizeDescription(a.description)

                    if (cleanDescription.isNotBlank()) {
                        Text("Описание", style = MaterialTheme.typography.titleMedium)
                        Text(cleanDescription, style = MaterialTheme.typography.bodyMedium)
                    }

                    Button(onClick = { vm.onEvent(DetailsEvent.Refresh) }) {
                        Text("Обновить")
                    }

                    if (state.isLoading) {
                        Text("Обновление…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
