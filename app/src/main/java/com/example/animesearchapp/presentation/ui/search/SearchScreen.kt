// File: app/src/main/java/com/example/animesearchapp/presentation/ui/search/SearchScreen.kt
package com.example.animesearchapp.presentation.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.animesearchapp.presentation.ui.components.AnimeCard
import com.example.animesearchapp.presentation.ui.components.EmptyContent
import com.example.animesearchapp.presentation.ui.components.ErrorContent
import com.example.animesearchapp.presentation.ui.components.LoadingContent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onAnimeClick: (Int) -> Unit,
    vm: SearchViewModel = hiltViewModel()
) {
    val state = vm.uiState.collectAsStateWithLifecycle().value
    val snackHost = remember { SnackbarHostState() }

    var text by rememberSaveable { mutableStateOf(state.query) }

    LaunchedEffect(Unit) {
        vm.snackbar.collectLatest { msg ->
            snackHost.showSnackbar(msg)
        }
    }

    LaunchedEffect(state.query) {
        if (text != state.query) text = state.query
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Поиск аниме") }) },
        snackbarHost = { SnackbarHost(snackHost) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { newValue ->
                    text = newValue
                    vm.onEvent(SearchEvent.QueryChanged(newValue))
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                singleLine = true,
                label = { Text("Введите запрос") },
                supportingText = {
                    if (state.isFromCache) {
                        Text(
                            text = "Показаны сохранённые данные",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            )

            when {
                state.isLoading && state.items.isEmpty() ->
                    LoadingContent(modifier = Modifier.fillMaxSize())

                state.errorMessage != null && state.items.isEmpty() ->
                    ErrorContent(
                        title = state.errorMessage,
                        actionLabel = "Повторить",
                        onAction = { vm.onEvent(SearchEvent.Retry) },
                        modifier = Modifier.fillMaxSize()
                    )

                state.items.isEmpty() ->
                    EmptyContent(
                        text = state.emptyHint ?: "Пусто",
                        modifier = Modifier.fillMaxSize()
                    )

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.items, key = { it.id }) { anime ->
                            AnimeCard(
                                anime = anime,
                                onClick = { onAnimeClick(anime.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}