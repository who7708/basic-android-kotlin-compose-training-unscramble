package com.example.unscramble.ui

/**
 * @author Chris
 * @version 1.0
 * @since 2024/05/29
 */
data class GameUiState(
    val currentScrambledWord: String = "",
    val currentWordCount: Int = 1,
    val isGuessedWordWrong: Boolean = false,
    val score: Int = 0,
)
