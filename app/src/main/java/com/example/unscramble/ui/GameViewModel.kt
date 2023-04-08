package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    // Game UI state
    private val _uiState = MutableStateFlow(GameUiState())

    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    var userGuess by mutableStateOf("")
        private set


    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    init {
        resetGame()
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, true)) {
            updateUserGuess("")
            updateGameState()
        } else {
            updateUserGuess("")
            _uiState.update { currentState ->
                currentState.copy(isGuessedWrong = true)

            }
        }
    }

    private fun updateGameState() {
        if (_uiState.value.wordCount != MAX_NO_OF_WORDS) {


            _uiState.update { currentState ->
                currentState.copy(
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    isGuessedWrong = false,
                    score = currentState.score.plus(SCORE_INCREASE),
                    wordCount = currentState.wordCount.inc()
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWrong = false,
                    score = currentState.score.plus(SCORE_INCREASE),
                    isGameOver = true
                )
            }
        }
    }


    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
        }
        return shuffleCurrentWord(currentWord)
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun skipWord() {
        updateGameState()
        // Reset user guess
        updateUserGuess("")
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    fun updateUserGuess(guessWord: String) {
        userGuess = guessWord
    }
}