package com.systemnox.brainquiz.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreSummaryCard(
    totalQuestions: Int,
    correctAnswers: Int,
    wrongAnswers: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScoreCard(
            label = "Total Questions",
            value = totalQuestions.toString(),
            icon = Icons.Default.List,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
        ScoreCard(
            label = "Correct Answers",
            value = correctAnswers.toString(),
            icon = Icons.Default.CheckCircle,
            containerColor = Color(0xFFE8F5E9), // Light green
            contentColor = Color(0xFF2E7D32) // Green text
        )
        ScoreCard(
            label = "Wrong Answers",
            value = wrongAnswers.toString(),
            icon = Icons.Default.Cancel,
            containerColor = Color(0xFFFFEBEE), // Light red
            contentColor = Color(0xFFD32F2F) // Red text
        )
    }
}

@Composable
fun ScoreCard(
    label: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {

    Card(
        modifier = Modifier.fillMaxWidth(0.85f),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = label, color = contentColor, fontSize = 14.sp)
                Text(text = value, color = contentColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

