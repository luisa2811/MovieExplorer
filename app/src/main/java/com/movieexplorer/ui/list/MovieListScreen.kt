package com.movieexplorer.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.movieexplorer.domain.model.Genre
import com.movieexplorer.domain.model.Movie
import com.movieexplorer.ui.components.EmptyState
import com.movieexplorer.ui.components.ErrorState
import com.movieexplorer.ui.components.GenreChips
import com.movieexplorer.ui.components.LoadingFooter
import com.movieexplorer.ui.components.MovieCard
import com.movieexplorer.ui.components.OfflineBanner
import com.movieexplorer.ui.components.SearchBar
import com.movieexplorer.ui.preview.SampleData
import com.movieexplorer.ui.theme.MovieExplorerTheme
import com.movieexplorer.util.toUserMessage
import kotlinx.coroutines.flow.flowOf

@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit
) {
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val genres by viewModel.genres.collectAsStateWithLifecycle()
    val genresError by viewModel.genresError.collectAsStateWithLifecycle()

    val moviesPagingItems = viewModel.movies.collectAsLazyPagingItems()

    LaunchedEffect(isOnline) {
        if (isOnline) {
            moviesPagingItems.retry()
        }
    }

    MovieListContent(
        isOnline = isOnline,
        query = query,
        selectedGenre = selectedGenre,
        genres = genres,
        genresError = genresError,
        moviesPagingItems = moviesPagingItems,
        onQueryChange = viewModel::onQueryChange,
        onGenreSelected = viewModel::onGenreSelected,
        onRetryGenres = viewModel::retryGenres,
        onMovieClick = onMovieClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListContent(
    isOnline: Boolean,
    query: String,
    selectedGenre: Genre?,
    genres: List<Genre>,
    genresError: String?,
    moviesPagingItems: LazyPagingItems<Movie>,
    onQueryChange: (String) -> Unit,
    onGenreSelected: (Genre?) -> Unit,
    onRetryGenres: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "MovieExplorer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OfflineBanner(visible = !isOnline)

            SearchBar(
                query = query,
                onQueryChange = onQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (genresError != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Error de géneros: $genresError",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onRetryGenres) {
                        Text("Reintentar", color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else {
                GenreChips(
                    genres = genres,
                    selectedGenre = selectedGenre,
                    onGenreSelected = onGenreSelected
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val refreshState = moviesPagingItems.loadState.refresh

                when {
                    refreshState is LoadState.Loading && moviesPagingItems.itemCount == 0 -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    refreshState is LoadState.Error && moviesPagingItems.itemCount == 0 -> {
                        ErrorState(
                            message = refreshState.error.toUserMessage(),
                            onRetry = { moviesPagingItems.retry() }
                        )
                    }
                    refreshState is LoadState.NotLoading && moviesPagingItems.itemCount == 0 -> {
                        EmptyState(message = "Intenta con otro título o género.")
                    }
                    else -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Indicador de recarga cuando ya existen items
                            if (refreshState is LoadState.Loading) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            } else if (refreshState is LoadState.Error) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = refreshState.error.toUserMessage(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = { moviesPagingItems.retry() }) {
                                        Text("Reintentar", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    count = moviesPagingItems.itemCount,
                                    key = { index ->
                                        val movie = moviesPagingItems.peek(index)
                                        if (movie != null) "${movie.id}_$index" else index
                                    }
                                ) { index ->
                                    val movie = moviesPagingItems[index]
                                    if (movie != null) {
                                        MovieCard(
                                            movie = movie,
                                            onClick = { onMovieClick(movie.id) }
                                        )
                                    }
                                }

                                when (val appendState = moviesPagingItems.loadState.append) {
                                    is LoadState.Loading -> {
                                        item {
                                            LoadingFooter()
                                        }
                                    }
                                    is LoadState.Error -> {
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = appendState.error.toUserMessage(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Button(
                                                    onClick = { moviesPagingItems.retry() },
                                                    shape = CircleShape,
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.primary,
                                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                ) {
                                                    Text("Reintentar")
                                                }
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieListContentPreview() {
    val samplePagingItems = flowOf(PagingData.from(SampleData.sampleMovies)).collectAsLazyPagingItems()
    MovieExplorerTheme {
        MovieListContent(
            isOnline = true,
            query = "",
            selectedGenre = null,
            genres = SampleData.sampleGenres,
            genresError = null,
            moviesPagingItems = samplePagingItems,
            onQueryChange = {},
            onGenreSelected = {},
            onRetryGenres = {},
            onMovieClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieListContentOfflinePreview() {
    val samplePagingItems = flowOf(PagingData.from(SampleData.sampleMovies)).collectAsLazyPagingItems()
    MovieExplorerTheme {
        MovieListContent(
            isOnline = false,
            query = "",
            selectedGenre = null,
            genres = SampleData.sampleGenres,
            genresError = null,
            moviesPagingItems = samplePagingItems,
            onQueryChange = {},
            onGenreSelected = {},
            onRetryGenres = {},
            onMovieClick = {}
        )
    }
}
