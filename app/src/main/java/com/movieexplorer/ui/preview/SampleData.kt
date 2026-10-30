package com.movieexplorer.ui.preview

import com.movieexplorer.domain.model.Genre
import com.movieexplorer.domain.model.Movie
import com.movieexplorer.domain.model.MovieDetail

object SampleData {
    val sampleGenres = listOf(
        Genre(id = 28, name = "Acción"),
        Genre(id = 12, name = "Aventura"),
        Genre(id = 16, name = "Animación"),
        Genre(id = 35, name = "Comedia"),
        Genre(id = 18, name = "Drama"),
        Genre(id = 878, name = "Ciencia ficción")
    )

    val sampleMovie = Movie(
        id = 550,
        title = "El club de la pelea",
        overview = "Un empleado de oficina insomne y un jabonero carismático forman un club de lucha subterráneo que evoluciona hacia algo mucho más grande.",
        posterUrl = "https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
        releaseDate = "1999-10-15",
        rating = 8.43
    )

    val sampleMovies = listOf(
        sampleMovie,
        Movie(
            id = 27205,
            title = "Inception",
            overview = "Un ladrón que roba secretos corporativos a través del uso de la tecnología de intercambio de sueños recibe la tarea inversa de plantar una idea en la mente de un C.E.O.",
            posterUrl = "https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
            releaseDate = "2010-07-16",
            rating = 8.36
        ),
        Movie(
            id = 157336,
            title = "Interstellar",
            overview = "Un equipo de exploradores viaja a través de un agujero de gusano en el espacio en un intento por garantizar la supervivencia de la humanidad.",
            posterUrl = "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBv9B.jpg",
            releaseDate = "2014-11-05",
            rating = 8.44
        ),
        Movie(
            id = 12345,
            title = "Dr. Strangelove or: How I Learned to Stop Worrying and Love the Bomb",
            overview = "Un general lunático desencadena un camino hacia el holocausto nuclear que una sala de guerra llena de políticos e ingenieros intenta desesperadamente detener.",
            posterUrl = "https://image.tmdb.org/t/p/w500/AAnO2mIe2Q4D2bS.jpg",
            releaseDate = "1964-01-29",
            rating = 8.5
        ),
        Movie(
            id = 67890,
            title = "Película Sin Póster Ni Sinopsis",
            overview = "",
            posterUrl = null,
            releaseDate = "2024-05-10",
            rating = 6.2
        )
    )

    val sampleMovieDetail = MovieDetail(
        id = 550,
        title = "El club de la pelea",
        tagline = "Miseria. Caos. Algo de alivio.",
        overview = "Un empleado de oficina insomne y un jabonero carismático forman un club de lucha subterráneo que evoluciona hacia algo mucho más grande.",
        posterUrl = "https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w1280/hZk2L1A33cR923ZdC1p3S.jpg",
        releaseDate = "1999-10-15",
        runtime = 139,
        rating = 8.43,
        voteCount = 26230,
        genres = listOf(
            Genre(id = 18, name = "Drama"),
            Genre(id = 53, name = "Thriller")
        )
    )
}
