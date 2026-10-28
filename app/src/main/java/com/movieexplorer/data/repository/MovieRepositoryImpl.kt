package com.movieexplorer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.movieexplorer.data.paging.MoviePagingSource
import com.movieexplorer.data.remote.TmdbApi
import com.movieexplorer.data.remote.mapper.toDomain
import com.movieexplorer.domain.model.Genre
import com.movieexplorer.domain.model.Movie
import com.movieexplorer.domain.model.MovieDetail
import com.movieexplorer.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : MovieRepository {

    override fun getPopularMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MoviePagingSource { page ->
                    api.getPopularMovies(page)
                }
            }
        ).flow
    }

    override fun searchMovies(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MoviePagingSource { page ->
                    api.searchMovies(query = query, page = page)
                }
            }
        ).flow
    }

    override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MoviePagingSource { page ->
                    api.getMoviesByGenre(genreId = genreId, page = page)
                }
            }
        ).flow
    }

    override suspend fun getGenres(): Result<List<Genre>> {
        return runCatching {
            val response = api.getGenres()
            response.genres?.map { it.toDomain() } ?: emptyList()
        }
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> {
        return runCatching {
            val dto = api.getMovieDetail(movieId)
            dto.toDomain()
        }
    }
}
