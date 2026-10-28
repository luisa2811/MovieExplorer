package com.movieexplorer.util

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isOnline: Flow<Boolean>
}
