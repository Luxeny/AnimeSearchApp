package com.example.animesearchapp.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animesearchapp.core.result.AppResult
import com.example.animesearchapp.domain.model.Anime
import com.example.animesearchapp.domain.repository.AnimeRepository
import com.example.animesearchapp.presentation.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val items: List<Anime> = emptyList(),
    val isLoading: Boolean = false,
    val isFromCache: Boolean = false,
    val emptyHint: String? = "Введите запрос для поиска",
    val errorMessage: String? = null
)

sealed class SearchEvent {
    data class QueryChanged(val value: String) : SearchEvent()
    data object Retry : SearchEvent()
    data object ClearQuery : SearchEvent()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _snackbar = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbar = _snackbar.asSharedFlow()

    private val queryFlow = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    private val resultsFlow = queryFlow
        .debounce(500)
        .map { it.trim() }
        .distinctUntilChanged()
        .flatMapLatest { q ->
            if (q.isEmpty()) repo.observeRecentResults(limit = 5)
            else repo.observeSearchResults(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            resultsFlow.collect { list ->
                val q = _uiState.value.query.trim()
                val emptyHint = when {
                    q.isEmpty() -> if (list.isEmpty()) "Недавних результатов нет" else null
                    list.isEmpty() -> "Ничего не найдено"
                    else -> null
                }
                _uiState.value = _uiState.value.copy(items = list, emptyHint = emptyHint)
            }
        }

        // initial load of recent
        queryFlow.value = ""
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                _uiState.value = _uiState.value.copy(
                    query = event.value,
                    errorMessage = null,
                    isFromCache = false,
                    isLoading = event.value.trim().isNotEmpty()
                )
                queryFlow.value = event.value

                val q = event.value.trim()
                if (q.isNotEmpty()) {
                    viewModelScope.launch {
                        val res = repo.refreshSearch(q, force = false)
                        handleRefreshResult(res, hasAnyUiItems = _uiState.value.items.isNotEmpty())
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }

            SearchEvent.Retry -> {
                val q = _uiState.value.query.trim()
                if (q.isEmpty()) return
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isFromCache = false)
                    val res = repo.refreshSearch(q, force = true)
                    handleRefreshResult(res, hasAnyUiItems = _uiState.value.items.isNotEmpty())
                }
            }

            SearchEvent.ClearQuery -> {
                _uiState.value = SearchUiState()
                queryFlow.value = ""
            }
        }
    }

    private fun handleRefreshResult(res: AppResult<Unit>?, hasAnyUiItems: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = false)
        when (res) {
            null -> Unit // skipped due to TTL or empty query
            is AppResult.Success -> {
                _uiState.value = _uiState.value.copy(errorMessage = null, isFromCache = false)
            }
            is AppResult.Error -> {
                val msg = res.error.toUserMessage()
                _snackbar.tryEmit(msg)

                // offline-first behavior:
                // if we already have cached list shown -> mark as from cache
                // otherwise show visible error block
                if (hasAnyUiItems) {
                    _uiState.value = _uiState.value.copy(isFromCache = true, errorMessage = null)
                    if (msg.contains("Нет подключения")) {
                        _snackbar.tryEmit("Показаны сохранённые данные")
                    }
                } else {
                    _uiState.value = _uiState.value.copy(errorMessage = msg, isFromCache = false)
                }
            }
        }
    }
}
