package com.systemnox.brainquiz.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.systemnox.brainquiz.utils.Constants

@Composable
fun CategorySelectionScreen(
    selectedCategories: Set<String>,
    onCategoryToggled: (String) -> Unit,
    onStartQuiz: () -> Unit,
    onSelectAllToggle: () -> Unit,
    allSelected: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Categories",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (allSelected) "Clear All" else "Select All",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp)) // Apply rounded corners
                        .background(MaterialTheme.colorScheme.surfaceVariant) // Set background color
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable(onClick = onSelectAllToggle)
                        .align(Alignment.End),
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Constants.CATEGORIES.forEach { category ->
                    FilterChip(
                        selected = selectedCategories.contains(category),
                        onClick = { onCategoryToggled(category) },
                        label = { Text(category) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = onStartQuiz,
                enabled = selectedCategories.isNotEmpty()
            ) {
                Text("Start Quiz")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategorySelectionScreenPreview() {
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    val allSelected = selectedCategories.size == Constants.CATEGORIES.size
    CategorySelectionScreen(
        selectedCategories = selectedCategories,
        allSelected = allSelected,
        onCategoryToggled = { category ->
            selectedCategories = if (selectedCategories.contains(category)) {
                selectedCategories - category
            } else {
                selectedCategories + category
            }
        },
        onSelectAllToggle = {
            selectedCategories = if (allSelected) emptySet()
            else Constants.CATEGORIES.toSet()
        },
        onStartQuiz = {}
    )
}
