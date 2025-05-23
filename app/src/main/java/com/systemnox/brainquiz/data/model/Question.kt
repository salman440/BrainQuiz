package com.systemnox.brainquiz.data.model

data class Question(
    val questionText : String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val category: String
)
