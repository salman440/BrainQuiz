@file:OptIn(ExperimentalMaterial3Api::class)

package com.systemnox.brainquiz.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.systemnox.brainquiz.R
import com.systemnox.brainquiz.ads.AdBannerView
import com.systemnox.brainquiz.data.model.Question
import com.systemnox.brainquiz.ui.theme.BrainQuizTheme


@Composable
fun ReviewScreen(
    questions: List<Question>,
    userAnswers: List<Int?>,
    onBackToHome: () -> Unit
) {
    val context : Context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.review_answers)) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                itemsIndexed(questions) { index, question ->
                    val userAnswer = userAnswers.getOrNull(index)
                    val correctAnswer = question.correctAnswerIndex
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = MaterialTheme.shapes.medium,
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Q${index + 1}: ${question.questionText}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                question.options.forEachIndexed { i, option ->
                                    val isCorrect = i == correctAnswer
                                    val isSelected = i == userAnswer

                                    val backgroundColor = when {
                                        userAnswer == null && isCorrect -> Color(0xFFE3F2FD) // Highlight correct if skipped
                                        isCorrect && isSelected -> Color(0xFFC8E6C9) // Green for correct selection
                                        isCorrect -> Color(0xFFBBDEFB) // Blue for correct answer
                                        isSelected -> Color(0xFFFFCDD2) // Red for wrong selection
                                        else -> Color.Transparent
                                    }

                                    val icon = when {
                                        userAnswer == null && isCorrect -> Icons.Default.Check
                                        isCorrect && isSelected -> Icons.Default.Check
                                        isCorrect -> Icons.Default.Check
                                        isSelected -> Icons.Default.Clear
                                        else -> null
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(
                                                color = backgroundColor,
                                                shape = MaterialTheme.shapes.small
                                            )
                                            .padding(12.dp)
                                    ) {
                                        Text(text = option, modifier = Modifier.weight(1f))
                                        if (icon != null) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (icon == Icons.Default.Check) Color(
                                                    0xFF388E3C
                                                ) else Color.Red,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                    }
                                }
                            }
                        }

                        // Status badge at top-right corner
                        val statusText = when (userAnswer) {
                            null -> "Skipped"
                            correctAnswer -> "Correct"
                            else -> "Wrong"
                        }

                        val statusColor = when (statusText) {
                            "Correct" -> Color(0xFF388E3C)
                            "Wrong" -> Color.Red
                            "Skipped" -> Color.Gray
                            else -> Color.Unspecified
                        }

                        Text(
                            text = statusText,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(color = statusColor, shape = MaterialTheme.shapes.small)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBackToHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(stringResource(R.string.back_to_home))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

//            AdBannerView(context = context)

        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReviewScreen() {
    val mockQuestions = listOf(
        Question(
            "What is the capital of France?",
            listOf("Berlin", "Madrid", "Paris", "Lisbon"),
            2
        ),
        Question("What is 2 + 2?", listOf("3", "4", "5", "6"), 1),
        Question(
            "Which language is used for Android development?",
            listOf("Swift", "Kotlin", "JavaScript", "Python"),
            1
        )
    )
    val mockUserAnswers = listOf(2, 0, null) // Correct, Wrong, Skipped

    BrainQuizTheme {
        ReviewScreen(
            questions = mockQuestions,
            userAnswers = mockUserAnswers,
            onBackToHome = {}
        )
    }
}