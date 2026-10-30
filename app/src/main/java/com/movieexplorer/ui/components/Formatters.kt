package com.movieexplorer.ui.components

import java.util.Locale

fun formatRuntime(minutes: Int?): String {
    if (minutes == null || minutes <= 0) return "Duración desconocida"
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return when {
        hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}min"
        hours > 0 -> "${hours}h"
        else -> "${remainingMinutes}min"
    }
}

fun formatVotes(count: Int): String {
    if (count <= 0) return "0"
    return when {
        count >= 1_000_000 -> {
            val millions = count / 1_000_000.0
            val formatted = String.format(Locale.US, "%.1f", millions)
            if (formatted.endsWith(".0")) {
                "${count / 1_000_000}M"
            } else {
                "${formatted}M"
            }
        }
        count >= 1_000 -> "${count / 1_000}k"
        else -> count.toString()
    }
}

fun extractYear(date: String): String {
    return if (date.length >= 4) date.substring(0, 4) else ""
}
