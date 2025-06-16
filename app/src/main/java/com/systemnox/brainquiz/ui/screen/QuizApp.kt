package com.systemnox.brainquiz.ui.screen

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.systemnox.brainquiz.ads.AdBannerView
import com.systemnox.brainquiz.ads.InterstitialAdManager
import com.systemnox.brainquiz.utils.Constants
import com.systemnox.brainquiz.viewmodel.AuthViewModel
import com.systemnox.brainquiz.viewmodel.QuizViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizApp(viewModel: QuizViewModel = hiltViewModel()) {

    val context: Context = LocalContext.current
    val activity = context as Activity
    val authViewModel: AuthViewModel = hiltViewModel()
    val user by authViewModel.user
    val message by viewModel.uiMessage
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Preload Interstitial ad once
    LaunchedEffect(Unit) {
        InterstitialAdManager.loadAd(context, Constants.INTERSTITIAL_AD_UNIT_ID)
    }

    // Show interstitial ad if needed
    LaunchedEffect(viewModel.shouldShowAd) {
        if (viewModel.shouldShowAd) {
            InterstitialAdManager.showAd(activity) {
                viewModel.onInterstitialAdCompleted()
            }
        } else {
            viewModel.onInterstitialAdCompleted()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = viewModel.screenState,
                transitionSpec = {
                    fadeIn(tween(1000)) togetherWith fadeOut(tween(500))
                },
                label = "Screen Transition",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { screen ->

                when (screen) {
//                    ScreenState.SPLASH -> SplashScreen { viewModel.showHomeScreen() }
                    ScreenState.SPLASH -> SplashScreen { viewModel.proceedAfterSplash(user != null) }
                    ScreenState.LOGIN -> LoginScreen(
                        onLoginSuccess = { viewModel.showHomeScreen() },
                        onNavigateToRegister = { viewModel.showRegisterScreen() },
                        onForgotPassword = {viewModel.showForgotPasswordScreen()}
                    )

                    ScreenState.REGISTER -> RegisterScreen(
                        onRegisterSuccess = { viewModel.showHomeScreen() },
                        onNavigateToLogin = { viewModel.showLoginScreen() }
                    )

                    ScreenState.FORGOT_PASSWORD -> ForgotPasswordScreen(
                        authViewModel = authViewModel,
                        quizViewModel = viewModel,
                        onBackToLogin = { viewModel.showLoginScreen() }
                    )

                    ScreenState.HOME -> HomeScreen(
                        onStartClick = { viewModel.showCategorySelectionScreen() },
                        onLogoutClick = {
                            authViewModel.logout()
                            viewModel.proceedAfterSplash(user != null)
                        })

                    ScreenState.CATEGORY_SELECTION -> CategorySelectionScreen(
                        selectedCategories = viewModel.selectedCategories,
                        onCategoryToggled = { viewModel.toggleCategory(it) },
                        onStartQuiz = { viewModel.startQuizWithSelectedCategories() },
                        onSelectAllToggle = { viewModel.toggleSelectAll() },
                        allSelected = viewModel.allSelected
                    )

                    ScreenState.QUIZ -> QuizScreen(viewModel)
                    ScreenState.RESULT -> ResultScreen(
                        viewModel.score,
                        totalQuestions = viewModel.totalQuestions,
                        onRestart = { viewModel.resetQuiz() },
                        onReview = { viewModel.showReviewScreen() })

                    ScreenState.REVIEW -> ReviewScreen(
                        questions = viewModel.getFilteredQuestions(),
                        userAnswers = viewModel.userAnswers,
                        onBackToHome = { viewModel.resetQuiz() }
                    )

                }
            }
            // Show bottom banner ad on all screens except Splash & Quiz
            if (viewModel.screenState != ScreenState.SPLASH && viewModel.screenState != ScreenState.QUIZ) {
                AdBannerView(
                    context = context,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
            }
        }
    }
}
