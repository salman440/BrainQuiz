package com.systemnox.brainquiz.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.systemnox.brainquiz.R
import com.systemnox.brainquiz.ui.theme.BrainQuizTheme
import com.systemnox.brainquiz.utils.InputValidator
import com.systemnox.brainquiz.viewmodel.AuthViewModel
import com.systemnox.brainquiz.viewmodel.QuizViewModel

@Composable
fun LoginScreen(
    vm: AuthViewModel? = hiltViewModel(),
    avm: QuizViewModel? = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    val viewModel = vm ?: return
    val appViewModel = avm ?: return
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading
    val user by viewModel.user
    val message by viewModel.authMessage
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(user) {
        if (user != null) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // App Logo
        Image(
            painter = painterResource(id = R.drawable.ic_icon), // Use your app's logo
            contentDescription = "Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 32.dp),
//            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        // Email Input
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it.trim() },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password Input with Toggle
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it.trim() },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            singleLine = true,
            maxLines = 1,
            visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (isPasswordVisible.value)
                    painterResource(id = R.drawable.ic_visibility_off)
                else painterResource(id = R.drawable.ic_visibility)

                IconButton(onClick = {
                    isPasswordVisible.value = !isPasswordVisible.value
                }) {
                    Icon(painter = image, contentDescription = "Toggle Password Visibility")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password
        Text(
            text = "Forgot Password?",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    onForgotPassword()
//                    if (email.value.isNotEmpty()) {
//                        onForgotPassword(email.value)
//                    } else {
//                        Toast.makeText(context, "Enter your email first", Toast.LENGTH_SHORT).show()
//                    }
                }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button with Loader
        Button(
            onClick = {
                when {
                    !InputValidator.isValidEmail(email.value) -> {
                        appViewModel.showMessage("Enter a valid email")
                    }
                    !InputValidator.isValidPassword(password.value) -> {
                        appViewModel.showMessage("Password must be at least 8 characters and include uppercase, lowercase, number, and special character")
                    }
                    else -> {
                        viewModel.login(email.value, password.value)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Don't have account
        Row {
            Text("Don't have an account?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Register",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BrainQuizTheme {
        LoginScreen(
            vm = null,
            avm = null,
            onLoginSuccess = { },
            onNavigateToRegister = { },
            onForgotPassword = { }
        )
    }
}
