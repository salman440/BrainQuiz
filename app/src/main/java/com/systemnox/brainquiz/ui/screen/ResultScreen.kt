package com.systemnox.brainquiz.ui.screen

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.systemnox.brainquiz.R
import com.systemnox.brainquiz.ads.AdBannerView
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    score: Int,
    totalQuestions: Int = 10, // default for preview/demo
    onRestart: () -> Unit,
    onReview: () -> Unit
) {
    val context: Context = LocalContext.current

    val correctAnswers = score
    val wrongAnswers = totalQuestions - score
    val percentage = (correctAnswers / totalQuestions.toFloat()) * 100

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Result") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.quiz_completed),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            CircularScoreIndicator(percentage = percentage.roundToInt())

            Spacer(modifier = Modifier.height(24.dp))

            // Score Summary
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.total_questions, totalQuestions), fontSize = 18.sp)
                Text(
                    stringResource(R.string.correct_answers, correctAnswers),
                    color = Color(0xFF388E3C),
                    fontSize = 18.sp
                )
                Text(stringResource(R.string.wrong_answers, wrongAnswers), color = Color.Red, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = onRestart,
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.play_again))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onReview,
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.review_answers))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

//            AdBannerView(context = context)
        }
    }
}

@Composable
fun CircularScoreIndicator(percentage: Int) {
    val animatedSweep = remember { Animatable(0f) }
    val animatedCount = remember { Animatable(0f) }

    LaunchedEffect(percentage) {
        animatedSweep.animateTo(
            targetValue = percentage / 100f,
            animationSpec = tween(durationMillis = 1000)
        )
        animatedCount.animateTo(
            targetValue = percentage.toFloat(),
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
        Canvas(modifier = Modifier.size(150.dp)) {
            drawArc(
                color = Color.LightGray,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 16f)
            )
            drawArc(
                color = if (percentage >= 50) Color(0xFF388E3C) else Color.Red,
                startAngle = -90f,
                sweepAngle = 360f * animatedSweep.value,
                useCenter = false,
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${animatedCount.value.roundToInt()}%",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewResultScreen() {
    ResultScreen(3,10, onRestart = {}, onReview = {})
}