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
    private val _userAnswers = mutableListOf<Int?>()
    val userAnswers: List<Int?> get() = _userAnswers
    var screenState by mutableStateOf(ScreenState.SPLASH)
        private set
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
    private var pendingActionAfterAd: (() -> Unit)? = null
    var shouldShowAd by mutableStateOf(false)
        private set

    fun showHomeScreen() {
        // Delegate to UI to show ad before showing result
        pendingActionAfterAd = { screenState = ScreenState.HOME }
        shouldShowAd = true
//        screenState = ScreenState.HOME
    }

    fun startQuiz() {
        currentIndex = 0
        score = 0
        screenState = ScreenState.QUIZ
        startTimer()
    }

    fun submitAnswer(selected: Int? = null) {
        timerJob?.cancel()
        _userAnswers.add(selected)

        if (selected != null && selected == currentQuestion.correctAnswerIndex) {
            score++
        }

        if (currentIndex < totalQuestions - 1) {
            currentIndex++
            screenState = ScreenState.QUIZ
            startTimer()
        } else {
            // Delegate to UI to show ad before showing result
            pendingActionAfterAd = { screenState = ScreenState.RESULT }
            shouldShowAd = true
//            show results screen
//            screenState = ScreenState.RESULT
        }
    }

    fun onInterstitialAdCompleted() {
        pendingActionAfterAd?.invoke()
        pendingActionAfterAd = null
        shouldShowAd = false
    }

    fun shouldShowAdBeforeResult(): Boolean = pendingActionAfterAd != null

    fun resetQuiz() {
        currentIndex = 0
        score = 0
        _userAnswers.clear()
//        screenState = ScreenState.HOME
        showHomeScreen()
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

    fun showReviewScreen() {
        // Delegate to UI to show ad before showing result
        pendingActionAfterAd = { screenState = ScreenState.REVIEW }
        shouldShowAd = true

//        show question review screen
//        screenState = ScreenState.REVIEW
    }

    fun getAllQuestions(): List<Question> = questions
}