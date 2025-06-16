package com.systemnox.brainquiz.utils

import android.util.Patterns

object InputValidator {

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false

        val uppercase = Regex("[A-Z]")
        val lowercase = Regex("[a-z]")
        val digit = Regex("[0-9]")
        val special = Regex("[!@#\$%^&*(),.?\":{}|<>]")

        return uppercase.containsMatchIn(password) &&
                lowercase.containsMatchIn(password) &&
                digit.containsMatchIn(password) &&
                special.containsMatchIn(password)
    }
}
