package com.example.animesearchapp.presentation.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animesearchapp.core.result.AppResult
import com.example.animesearchapp.domain.model.Anime
import com.example.animesearchapp.domain.repository.AnimeRepository
import com.example.animesearchapp.presentation.navigation.Destinations
import com.example.animesearchapp.presentation.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailsUiState(
    val animeId: Int,
    val anime: Anime? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed class DetailsEvent {
    data object Refresh : DetailsEvent()
}

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: AnimeRepository
) : ViewModel() {

    private val animeId: Int = requireNotNull(savedStateHandle[Destinations.ARG_ANIME_ID])

    private val _uiState = MutableStateFlow(DetailsUiState(animeId = animeId))
    val uiState: StateFlow<DetailsUiState> = _uiState

    private val _snackbar = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbar = _snackbar.asSharedFlow()

    private val cachedFlow = repo.observeAnimeDetails(animeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch {
            cachedFlow.collect { cached ->
                _uiState.value = _uiState.value.copy(anime = cached, isLoading = _uiState.value.isLoading && cached == null)
            }
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = repo.refreshAnimeDetails(animeId, force = false)
            handle(res)
        }
    }

    fun onEvent(event: DetailsEvent) {
        when (event) {
            DetailsEvent.Refresh -> {
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    val res = repo.refreshAnimeDetails(animeId, force = true)
                    handle(res)
                }
            }
        }
    }

    private fun handle(res: AppResult<Unit>?) {
        _uiState.value = _uiState.value.copy(isLoading = false)
        when (res) {
            null -> Unit
            is AppResult.Success -> _uiState.value = _uiState.value.copy(errorMessage = null)
            is AppResult.Error -> {
                val msg = res.error.toUserMessage()
                _snackbar.tryEmit(msg)
                // visible error block if no cached details
                if (_uiState.value.anime == null) {
                    _uiState.value = _uiState.value.copy(errorMessage = msg)
                }
            }
        }
    }
}
