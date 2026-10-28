package com.movieexplorer.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieexplorer.domain.model.MovieDetail
import com.movieexplorer.domain.repository.MovieRepository
import com.movieexplorer.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val detail: MovieDetail) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = savedStateHandle.get<Int>("movieId")
        ?: savedStateHandle.get<String>("movieId")?.toIntOrNull()
        ?: 0

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetail()
    }

    fun retry() {
        loadMovieDetail()
    }

    private fun loadMovieDetail() {
        if (movieId <= 0) {
            _uiState.value = DetailUiState.Error("ID de película no válido.")
            return
        }
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            repository.getMovieDetail(movieId)
                .onSuccess { detail ->
                    _uiState.value = DetailUiState.Success(detail)
                }
                .onFailure { throwable ->
                    _uiState.value = DetailUiState.Error(throwable.toUserMessage())
                }
        }
    }
}
