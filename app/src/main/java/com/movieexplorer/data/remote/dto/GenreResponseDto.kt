package com.movieexplorer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GenreResponseDto(
    @SerializedName("genres") val genres: List<GenreDto>?
)
