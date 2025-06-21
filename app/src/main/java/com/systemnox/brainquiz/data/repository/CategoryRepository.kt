package com.systemnox.brainquiz.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.systemnox.brainquiz.data.model.Category
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchCategories(): List<Category> {
        val snapshot = db.collection("categories").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Category::class.java)?.copy(id = doc.id)
        }
    }
}