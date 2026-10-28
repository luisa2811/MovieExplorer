package com.movieexplorer.util

import retrofit2.HttpException
import java.io.IOException

fun Throwable.toUserMessage(): String {
    return when (this) {
        is IOException -> "Sin conexión a internet. Revisa tu red e inténtalo de nuevo."
        is HttpException -> when (code()) {
            401 -> "Error de autenticación con la API."
            404 -> "No se encontró la información solicitada."
            else -> "Error del servidor (código ${code()})."
        }
        else -> "Ocurrió un error inesperado."
    }
}
