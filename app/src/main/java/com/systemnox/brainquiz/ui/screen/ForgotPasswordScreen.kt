package com.systemnox.brainquiz.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.systemnox.brainquiz.utils.InputValidator
import com.systemnox.brainquiz.viewmodel.AuthViewModel
import com.systemnox.brainquiz.viewmodel.QuizViewModel
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    quizViewModel: QuizViewModel,
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by authViewModel.isLoading
    val message by authViewModel.authMessage

    BackHandler {
        onBackToLogin()
    }
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
//            if (it.contains("Reset link sent")) {
//                delay(1500)
//                onBackToLogin()
//            }
            authViewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Reset Password", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it.trim() },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!InputValidator.isValidEmail(email.value)) {
                        quizViewModel.showMessage("Enter a valid email")
                    } else {
                        authViewModel.sendPasswordReset(email.value)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Send Reset Link")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Back to Login",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onBackToLogin() }
            )
        }
    }
}
