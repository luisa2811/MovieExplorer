package com.movieexplorer.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val tagline: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String,
    val runtime: Int?,
    val rating: Double,
    val voteCount: Int,
    val genres: List<Genre>
)
