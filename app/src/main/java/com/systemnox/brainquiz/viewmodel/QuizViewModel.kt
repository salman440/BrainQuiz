package com.systemnox.brainquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.systemnox.brainquiz.data.model.Question
import com.systemnox.brainquiz.ui.screen.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor() : ViewModel() {
    private val questions = listOf(
        Question("What is 2 + 2?", listOf("3", "4", "5", "6"), 1),
        Question("What is the capital of France?", listOf("Paris", "London", "Berlin", "Madrid"), 0),
        Question("Which planet is known as the Red Planet?", listOf("Mars", "Venus", "Jupiter", "Earth"), 0),
    )

    var screenState by mutableStateOf(ScreenState.SPLASH)
        private set
//    var screenState by mutableStateOf(ScreenState.HOME)
//        private set
    var currentIndex by mutableStateOf(0)
        private set
    var score by mutableStateOf(0)
        private set

    val totalQuestions = questions.size
    val currentQuestion: Question get() = questions[currentIndex]

//    timer value initialization
    var remainingTime by mutableStateOf(5) // set timer to 5 seconds
        private set

    private var timerJob: Job? = null


    fun showHomeScreen() {
        screenState = ScreenState.HOME
    }

    fun startQuiz() {
        currentIndex = 0
        score = 0
        screenState = ScreenState.QUIZ
        startTimer()
    }

    fun submitAnswer(selected: Int? = null) {
        timerJob?.cancel()

        if (selected != null && selected == currentQuestion.correctAnswerIndex) {
            score++
        }

        if (currentIndex < totalQuestions - 1) {
            currentIndex++
            screenState = ScreenState.QUIZ
            startTimer()
        } else {
            screenState = ScreenState.RESULT
        }
    }

    fun resetQuiz() {
        currentIndex = 0
        score = 0
        screenState = ScreenState.HOME
    }

    private fun startTimer() {
        remainingTime = 5 // Reset timer to 5 seconds
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (remainingTime > 0) {
                delay(1000)
                remainingTime--
            }
            submitAnswer(null) // Auto-submit when time is up
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}