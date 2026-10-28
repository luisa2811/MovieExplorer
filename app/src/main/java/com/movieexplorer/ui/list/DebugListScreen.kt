// TEMPORAL: será reemplazada por la UI final
package com.movieexplorer.ui.list

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.movieexplorer.util.toUserMessage

@Composable
fun DebugListScreen(
    viewModel: MovieListViewModel,
    onMovieClick: (Int) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val genres by viewModel.genres.collectAsState()
    val genresError by viewModel.genresError.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    val moviesPagingItems = viewModel.movies.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Status Bar: Online / Offline
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isOnline) "Estado: Online" else "Estado: Offline (Sin conexión)",
                color = if (isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Field
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Buscar película...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Genres Row
        if (genresError != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Error géneros: $genresError", color = MaterialTheme.colorScheme.error)
                Button(onClick = viewModel::retryGenres) {
                    Text("Reintentar")
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(genres, key = { it.id }) { genre ->
                    FilterChip(
                        selected = selectedGenre?.id == genre.id,
                        onClick = { viewModel.onGenreSelected(genre) },
                        label = { Text(genre.name) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Movies List
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMovieClick(movie.id) }
                                .padding(8.dp)
                        ) {
                            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Fecha de estreno: ${movie.releaseDate}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Append loading state
                when (val appendState = moviesPagingItems.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
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
                                    color = MaterialTheme.colorScheme.error
                                )
                                Button(onClick = { moviesPagingItems.retry() }) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }

            // Refresh loading & error state
            when (val refreshState = moviesPagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    if (moviesPagingItems.itemCount == 0) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                is LoadState.Error -> {
                    if (moviesPagingItems.itemCount == 0) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = refreshState.error.toUserMessage(),
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { moviesPagingItems.retry() }) {
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
