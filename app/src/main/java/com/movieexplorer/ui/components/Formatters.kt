package com.movieexplorer.ui.components

import java.text.NumberFormat
import java.util.Locale

fun formatRuntime(minutes: Int?): String {
    if (minutes == null || minutes <= 0) return "N/A"
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return when {
        hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}min"
        hours > 0 -> "${hours}h"
        else -> "${remainingMinutes}min"
    }
}

fun formatVotes(count: Int): String {
    val formattedCount = NumberFormat.getInstance(Locale("es", "ES")).format(count)
    return if (count == 1) "$formattedCount voto" else "$formattedCount votos"
}

fun extractYear(date: String): String {
    return if (date.length >= 4) date.substring(0, 4) else date
}
