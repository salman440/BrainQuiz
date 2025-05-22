package com.systemnox.brainquiz.ui.components

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ErrorSnackbarHost(
    hostState: SnackbarHostState
) {
    SnackbarHost(hostState = hostState) { data ->
        Snackbar(
            containerColor = Color(0xFFB00020), // Red background
            contentColor = Color.White // Text color
        ) {
            Text(data.visuals.message)
        }
    }
}
