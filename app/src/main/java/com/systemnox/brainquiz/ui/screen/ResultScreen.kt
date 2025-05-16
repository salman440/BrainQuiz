package com.systemnox.brainquiz.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.systemnox.brainquiz.ads.AdBannerView

@Composable
fun ResultScreen(score: Int, onRestart: () -> Unit, onReview: () -> Unit) {
    val context: Context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(48.dp))
            Text("🎉 Quiz Completed!", style = MaterialTheme.typography.headlineMedium)
            Text("Your Score: $score", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRestart) {
                Text("Play Again")
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Review Answers Button
            Button(onClick = onReview) {
                Text("Review Answers")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        AdBannerView(context = context)
    }
}