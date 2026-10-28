package com.movieexplorer.domain.repository

import androidx.paging.PagingData
import com.movieexplorer.domain.model.Genre
import com.movieexplorer.domain.model.Movie
import com.movieexplorer.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMovies(): Flow<PagingData<Movie>>
    fun searchMovies(query: String): Flow<PagingData<Movie>>
    fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>>
    suspend fun getGenres(): Result<List<Genre>>
    suspend fun getMovieDetail(movieId: Int): Result<MovieDetail>
}
