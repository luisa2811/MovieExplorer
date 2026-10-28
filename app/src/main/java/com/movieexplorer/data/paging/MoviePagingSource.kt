package com.movieexplorer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.movieexplorer.data.remote.dto.MovieResponseDto
import com.movieexplorer.data.remote.mapper.toDomain
import com.movieexplorer.domain.model.Movie
import retrofit2.HttpException
import java.io.IOException

class MoviePagingSource(
    private val fetchMovies: suspend (page: Int) -> MovieResponseDto
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = fetchMovies(page)
            val movies = response.results?.map { it.toDomain() } ?: emptyList()
            val totalPages = response.totalPages ?: 1

            val nextKey = if (movies.isEmpty() || page >= totalPages || page >= 500) {
                null
            } else {
                page + 1
            }

            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}
