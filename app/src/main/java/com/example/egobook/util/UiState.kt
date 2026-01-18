package com.example.egobook.util

sealed class UiState<out T> {
    object Idle: UiState<Nothing>()
    object Loading: UiState<Nothing>()
    data class Success<T>(val data: T): UiState<T>()
    data class Failure(val message: String?): UiState<Nothing>()
}