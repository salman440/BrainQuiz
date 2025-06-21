package com.systemnox.brainquiz.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.systemnox.brainquiz.data.model.Question
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class QuestionRepository {

    private val db = FirebaseFirestore.getInstance()
    private val questionCollection = db.collection("questions")

    suspend fun fetchQuestionsByCategories(categories: Set<String>, limit: Int = 30): List<Question> {
        val allQuestions = mutableListOf<Question>()
        val batchSize = 50  // Adjust if needed

        for (category in categories) {
            val snapshot = questionCollection
                .whereEqualTo("category", category)
                .limit(batchSize.toLong())
                .get()
                .await()

            val questions = snapshot.toObjects(Question::class.java)
            allQuestions.addAll(questions)
        }

        // Shuffle and limit to max 30 questions
        return allQuestions.shuffled().take(limit)
    }

    suspend fun addQuestion(question: Question) {
        questionCollection.add(question).await()
    }
}
