package com.movieexplorer.data.remote.mapper

import com.movieexplorer.data.remote.dto.GenreDto
import com.movieexplorer.data.remote.dto.MovieDetailDto
import com.movieexplorer.data.remote.dto.MovieDto
import com.movieexplorer.domain.model.Genre
import com.movieexplorer.domain.model.Movie
import com.movieexplorer.domain.model.MovieDetail

private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"
private const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780"

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id ?: 0,
        title = title ?: "",
        overview = overview ?: "",
        posterUrl = posterPath?.takeIf { it.isNotBlank() }?.let { "$POSTER_BASE_URL$it" },
        releaseDate = releaseDate ?: "",
        rating = voteAverage ?: 0.0
    )
}

fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id ?: 0,
        name = name ?: ""
    )
}

fun MovieDetailDto.toDomain(): MovieDetail {
    return MovieDetail(
        id = id ?: 0,
        title = title ?: "",
        tagline = tagline ?: "",
        overview = overview ?: "",
        posterUrl = posterPath?.takeIf { it.isNotBlank() }?.let { "$POSTER_BASE_URL$it" },
        backdropUrl = backdropPath?.takeIf { it.isNotBlank() }?.let { "$BACKDROP_BASE_URL$it" },
        releaseDate = releaseDate ?: "",
        runtime = runtime,
        rating = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        genres = genres?.map { it.toDomain() } ?: emptyList()
    )
}
