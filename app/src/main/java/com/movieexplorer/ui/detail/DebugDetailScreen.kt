// TEMPORAL: será reemplazada por la UI final
package com.movieexplorer.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DebugDetailScreen(
    viewModel: MovieDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is DetailUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = viewModel::retry) {
                        Text("Reintentar")
                    }
                }
            }
            is DetailUiState.Success -> {
                val detail = state.detail
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Button(onClick = onBackClick) {
                        Text("Volver")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = detail.title, style = MaterialTheme.typography.headlineMedium)

                    if (detail.tagline.isNotBlank()) {
                        Text(
                            text = "\"${detail.tagline}\"",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Fecha de estreno: ${detail.releaseDate}")
                    Text(text = "Puntuación: ${detail.rating} (${detail.voteCount} votos)")
                    Text(text = "Duración: ${detail.runtime?.let { "$it min" } ?: "Desconocida"}")
                    Text(text = "Géneros: ${detail.genres.joinToString { it.name }}")

                    Spacer(modifier = Modifier.height(8.dp))

                    if (detail.posterUrl != null) {
                        Text(text = "Poster URL: ${detail.posterUrl}")
                    }
                    if (detail.backdropUrl != null) {
                        Text(text = "Backdrop URL: ${detail.backdropUrl}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Sinopsis:", style = MaterialTheme.typography.titleSmall)
                    Text(text = detail.overview)
                }
            }
        }
    }
}
