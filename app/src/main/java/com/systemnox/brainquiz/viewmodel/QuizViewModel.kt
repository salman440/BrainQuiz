package com.systemnox.brainquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.systemnox.brainquiz.data.model.Question
import com.systemnox.brainquiz.ui.screen.ScreenState
import com.systemnox.brainquiz.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor() : ViewModel() {
    private val _userAnswers = mutableListOf<Int?>()
    val userAnswers: List<Int?> get() = _userAnswers
    var screenState by mutableStateOf(ScreenState.SPLASH)
        private set
    var currentIndex by mutableStateOf(0)
        private set
    var score by mutableStateOf(0)
        private set

    //    timer value initialization
    var remainingTime by mutableStateOf(5) // set timer to 5 seconds
        private set
    private var timerJob: Job? = null
    private var pendingActionAfterAd: (() -> Unit)? = null
    var shouldShowAd by mutableStateOf(false)
        private set
    var selectedCategories by mutableStateOf(setOf<String>())
        private set
    val allSelected: Boolean
        get() = selectedCategories.size == Constants.CATEGORIES.size
    private var filteredQuestions: List<Question> = emptyList()

    val totalQuestions get() = filteredQuestions.size
    val currentQuestion get() = filteredQuestions[currentIndex]
    private val _uiMessage = mutableStateOf<String?>(null)
    val uiMessage: State<String?> get() = _uiMessage

    private val questions = listOf(
        Question("What is 2 + 2?", listOf("3", "4", "5", "6"), 1, "Math"),
        Question(
            "Remember the sequence: A-B-C",
            listOf("A-C-B", "A-B-C", "B-A-C", "C-B-A"),
            1,
            "Memory"
        ),
        Question(
            "Which shape has 3 sides?",
            listOf("Triangle", "Circle", "Square", "Pentagon"),
            0,
            "Logic"
        ),
    )


    fun toggleCategory(category: String) {
        selectedCategories = if (selectedCategories.contains(category)) {
            selectedCategories - category
        } else {
            selectedCategories + category
        }
    }

    fun toggleSelectAll() {
        selectedCategories = if (allSelected) {
            emptySet()
        } else {
            Constants.CATEGORIES.toSet()
        }
    }

    fun startQuizWithSelectedCategories() {
        // Check if selectedCategories is empty
        if (selectedCategories.isEmpty()) {
            showMessage("Please select at least one category.")
            return
        }

//        filtering questions according to categories and removing empty questions
        val validQuestions = questions.filter { question ->
            question.questionText.isNotBlank() &&
                    question.options.isNotEmpty() &&
                    selectedCategories.contains(question.category)
        }

        if (validQuestions.isNotEmpty()) {
            filteredQuestions = validQuestions
            currentIndex = 0
            score = 0
            screenState = ScreenState.QUIZ
            startTimer()
        } else {
//            show error that no questions found against the selected category
            showMessage("No questions found for the selected categories.")
        }
    }


    fun showHomeScreen() {
        if (Constants.ENABLE_INTERSTITIAL_ADS) {
            // Delegate to UI to show ad before showing result
            pendingActionAfterAd = { screenState = ScreenState.HOME }
            shouldShowAd = true
        } else {
            screenState = ScreenState.HOME
        }
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
            if (Constants.ENABLE_INTERSTITIAL_ADS) {
                // Delegate to UI to show ad before showing result
                pendingActionAfterAd = { screenState = ScreenState.RESULT }
                shouldShowAd = true
            } else {
                screenState = ScreenState.RESULT
            }

        }
    }

    fun onInterstitialAdCompleted() {
        pendingActionAfterAd?.invoke()
        pendingActionAfterAd = null
        shouldShowAd = false
    }

    fun resetQuiz() {
        currentIndex = 0
        score = 0
        _userAnswers.clear()
        selectedCategories = emptySet()
        showHomeScreen()
        filteredQuestions = emptyList()
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
        if (Constants.ENABLE_INTERSTITIAL_ADS) {
            // Delegate to UI to show ad before showing result
            pendingActionAfterAd = { screenState = ScreenState.REVIEW }
            shouldShowAd = true
        } else {
            screenState = ScreenState.REVIEW
        }

    }

    fun getAllQuestions(): List<Question> = questions
    fun getFilteredQuestions(): List<Question> = filteredQuestions
    fun showCategorySelectionScreen() {
        if (Constants.ENABLE_INTERSTITIAL_ADS) {
            // Delegate to UI to show ad before showing result
            pendingActionAfterAd = { screenState = ScreenState.CATEGORY_SELECTION }
            shouldShowAd = true
        } else {
            screenState = ScreenState.CATEGORY_SELECTION
        }
    }

    fun showMessage(message: String) {
        _uiMessage.value = message
    }

    fun clearMessage() {
        _uiMessage.value = null
    }
}