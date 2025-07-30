// data/repository/PostRepositoryImpl.kt
package com.hjkarpet.ekomas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.hjkarpet.ekomas.domain.model.Post
import com.hjkarpet.ekomas.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PostRepositoryImpl : PostRepository {

    private val firestore = FirebaseFirestore.getInstance()

    override fun getAllPosts(): Flow<Result<List<Post>>> {
        return firestore.collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING) // Tampilkan yang terbaru di atas
            .snapshots() // Memberikan update real-time
            .map { snapshot ->
                try {
                    val posts = snapshot.toObjects(Post::class.java)
                    Result.success(posts)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }
}