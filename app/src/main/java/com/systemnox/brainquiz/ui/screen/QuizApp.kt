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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.systemnox.brainquiz.ads.AdBannerView
import com.systemnox.brainquiz.ads.InterstitialAdManager
import com.systemnox.brainquiz.utils.Constants
import com.systemnox.brainquiz.viewmodel.QuizViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizApp(viewModel: QuizViewModel = hiltViewModel()) {

//    val currentScreen = viewModel.screenState
    val context : Context = LocalContext.current
    val activity = context as Activity

    // Preload Interstitial ad once
    LaunchedEffect(Unit) {
        InterstitialAdManager.loadAd(context, Constants.INTERSTITIAL_AD_UNIT_ID)
    }

    // Show interstitial ad if needed
    LaunchedEffect(viewModel.shouldShowAd) {
        Log.e("QuizApp", "shouldShowAd: ${viewModel.shouldShowAd}")
        if (viewModel.shouldShowAd && activity != null) {
            InterstitialAdManager.showAd(activity) {
                viewModel.onInterstitialAdCompleted()
            }
        }
    }

    Column (modifier = Modifier.fillMaxWidth()) {
        AnimatedContent(
            targetState = viewModel.screenState,
            transitionSpec = {
                fadeIn(tween(1000)) togetherWith fadeOut(tween(500))
            },
            label = "Screen Transition",
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { screen ->
//        Box (modifier = Modifier.fillMaxWidth()) {
            when (screen) {
                ScreenState.SPLASH -> SplashScreen { viewModel.showHomeScreen() }
                ScreenState.HOME -> HomeScreen(onStartClick = { viewModel.startQuiz() })
                ScreenState.QUIZ -> QuizScreen(viewModel)
                ScreenState.RESULT -> ResultScreen(
                    viewModel.score,
                    totalQuestions = viewModel.totalQuestions,
                    onRestart = { viewModel.resetQuiz() },
                    onReview = { viewModel.showReviewScreen() })

                ScreenState.REVIEW -> ReviewScreen(
                    questions = viewModel.getAllQuestions(),
                    userAnswers = viewModel.userAnswers,
                    onBackToHome = { viewModel.resetQuiz() }
                )
            }
//            // Show bottom banner ad on all screens except Splash & Quiz
//            if(screen != ScreenState.SPLASH && screen != ScreenState.QUIZ) {
//                AdBannerView(
//                    context = context,
//                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
//                )
//            }

//        }

        }
        // Show bottom banner ad on all screens except Splash & Quiz
        if(viewModel.screenState != ScreenState.SPLASH && viewModel.screenState != ScreenState.QUIZ) {
            AdBannerView(
                context = context,
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
            )
        }
    }
}
