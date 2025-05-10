package com.systemnox.brainquiz.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.systemnox.brainquiz.viewmodel.QuizViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizApp(viewModel: QuizViewModel = hiltViewModel()) {

    val currentScreen = viewModel.screenState

    AnimatedContent(
        targetState = viewModel.screenState,
        transitionSpec = {
            fadeIn(tween(1000)) togetherWith fadeOut(tween(500))
        },
        label = "Screen Transition"
    ) { screen ->
        when (screen) {
            ScreenState.SPLASH -> SplashScreen { viewModel.showHomeScreen() }
            ScreenState.HOME -> HomeScreen(onStartClick = { viewModel.startQuiz() })
            ScreenState.QUIZ -> QuizScreen(viewModel)
            ScreenState.RESULT -> ResultScreen(
                viewModel.score,
                onRestart = { viewModel.resetQuiz() })
        }
    }
}