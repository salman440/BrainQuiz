package com.systemnox.brainquiz.data.model

data class Question(
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = -1,
    val category: String = ""
)
