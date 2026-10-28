package com.movieexplorer.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.movieexplorer.domain.model.Genre
import com.movieexplorer.domain.model.Movie
import com.movieexplorer.domain.repository.MovieRepository
import com.movieexplorer.util.ConnectivityObserver
import com.movieexplorer.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val repository: MovieRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedGenre = MutableStateFlow<Genre?>(null)
    val selectedGenre: StateFlow<Genre?> = _selectedGenre.asStateFlow()

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    private val _genresError = MutableStateFlow<String?>(null)
    val genresError: StateFlow<String?> = _genresError.asStateFlow()

    val isOnline: StateFlow<Boolean> = connectivityObserver.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val movies: Flow<PagingData<Movie>> = combine(_query, _selectedGenre) { q, genre ->
        q to genre
    }.debounce { (q, _) ->
        if (q.isNotBlank()) 400L else 0L
    }.flatMapLatest { (q, genre) ->
        val trimmedQuery = q.trim()
        when {
            trimmedQuery.isNotEmpty() -> repository.searchMovies(trimmedQuery)
            genre != null -> repository.getMoviesByGenre(genre.id)
            else -> repository.getPopularMovies()
        }
    }.cachedIn(viewModelScope)

    init {
        loadGenres()
    }

    fun onQueryChange(text: String) {
        _query.value = text
        _selectedGenre.value = null
    }

    fun onGenreSelected(genre: Genre?) {
        if (genre == null || _selectedGenre.value?.id == genre.id) {
            _selectedGenre.value = null
        } else {
            _selectedGenre.value = genre
            _query.value = ""
        }
    }

    fun retryGenres() {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            _genresError.value = null
            repository.getGenres()
                .onSuccess { genreList ->
                    _genres.value = genreList
                }
                .onFailure { throwable ->
                    _genresError.value = throwable.toUserMessage()
                }
        }
    }
}
