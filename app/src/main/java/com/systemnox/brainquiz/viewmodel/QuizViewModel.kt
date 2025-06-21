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

import com.systemnox.brainquiz.data.model.Category
import com.systemnox.brainquiz.data.repository.CategoryRepository
import com.systemnox.brainquiz.data.repository.QuestionRepository


@HiltViewModel
class QuizViewModel @Inject constructor() : ViewModel() {

    private val questionRepository = QuestionRepository()

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

    var isLoading by mutableStateOf(false)
        private set
    private val categoryRepository = CategoryRepository()
    var firebaseCategories by mutableStateOf<List<Category>>(emptyList())
        private set

    var selectedCategories by mutableStateOf(setOf<String>())
        private set
    val allSelected: Boolean
        //        get() = selectedCategories.size == Constants.CATEGORIES.size
        get() = selectedCategories.size == firebaseCategories.size && firebaseCategories.isNotEmpty()
    private var filteredQuestions: List<Question> = emptyList()

    val totalQuestions get() = filteredQuestions.size

    //    val currentQuestion get() = filteredQuestions[currentIndex]
    val currentQuestion: Question?
        get() = if (filteredQuestions.isNotEmpty() && currentIndex in filteredQuestions.indices)
            filteredQuestions[currentIndex]
        else null

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

    //    fetch categories from firebase
    fun loadCategories() {
        viewModelScope.launch {
            isLoading = true
            try {
                firebaseCategories = categoryRepository.fetchCategories()
            } catch (e: Exception) {
                showMessage("Failed to load categories: ${e.localizedMessage}")
            } finally {
                isLoading = false
            }
        }
    }

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
//            Constants.CATEGORIES.toSet()
            firebaseCategories.map { it.name }.toSet()
        }
    }

    fun startQuizWithSelectedCategories() {
        // Check if selectedCategories is empty
        if (selectedCategories.isEmpty()) {
            showMessage("Please select at least one category.")
            return
        }

//        fetching questions from firebase
        viewModelScope.launch {
            isLoading = true
            try {
                val questions = questionRepository.fetchQuestionsByCategories(selectedCategories)
                if (questions.isEmpty()) {
                    showMessage("No questions found for the selected categories.")
                } else {
                    filteredQuestions = questions
                    currentIndex = 0
                    score = 0
                    screenState = ScreenState.QUIZ
                    startTimer()
                }
            } catch (e: Exception) {
                showMessage("Failed to load questions: ${e.localizedMessage}")
            } finally {
                isLoading = false
            }
        }

//
////        filtering questions according to categories and removing empty questions
//        val validQuestions = questions.filter { question ->
//            question.questionText.isNotBlank() &&
//                    question.options.isNotEmpty() &&
//                    selectedCategories.contains(question.category)
//        }
//
//        if (validQuestions.isNotEmpty()) {
//            filteredQuestions = validQuestions
//            currentIndex = 0
//            score = 0
//            screenState = ScreenState.QUIZ
//            startTimer()
//        } else {
////            show error that no questions found against the selected category
//            showMessage("No questions found for the selected categories.")
//        }
    }

    fun proceedAfterSplash(isUserLoggedIn: Boolean) {
        loadCategories() //fetch categories from firebase
        screenState = if (isUserLoggedIn) {
            ScreenState.HOME
        } else {
            ScreenState.LOGIN
        }
    }

    fun showRegisterScreen() {
        screenState = ScreenState.REGISTER
    }

    fun showLoginScreen() {
        screenState = ScreenState.LOGIN
    }

    fun showForgotPasswordScreen() {
        screenState = ScreenState.FORGOT_PASSWORD
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

        if (selected != null && selected == currentQuestion?.correctAnswerIndex) {
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

    fun showResultScreen() {
        screenState = ScreenState.RESULT
    }

    fun onInterstitialAdCompleted() {
        pendingActionAfterAd?.invoke()
        pendingActionAfterAd = null
        shouldShowAd = false
    }

    fun stopQuiz() {
        timerJob?.cancel()        // Stop the timer
        _userAnswers.clear()      // Clear answers
        currentIndex = 0          // Reset index
        score = 0                 // Reset score
        selectedCategories = emptySet()
        filteredQuestions = emptyList()
        showHomeScreen()
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