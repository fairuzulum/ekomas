package com.hjkarpet.ekomas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.hjkarpet.ekomas.domain.model.Post
import com.hjkarpet.ekomas.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnalyticsRepositoryImpl : AnalyticsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    override fun getMyPosts(uid: String): Flow<Result<List<Post>>> {
        return firestore.collection("posts")
            .whereEqualTo("authorUid", uid) // Filter berdasarkan UID penulis
            .snapshots()
            .map { snapshot ->
                try {
                    val posts = snapshot.toObjects(Post::class.java)
                    Result.success(posts)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }

    override fun getMyLikedPostsCount(uid: String): Flow<Result<Int>> {
        // Asumsi kita punya sub-collection "likedPosts" di dalam dokumen masjid
        return firestore.collection("masjid").document(uid).collection("likedPosts")
            .snapshots()
            .map { snapshot ->
                try {
                    Result.success(snapshot.size())
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }

    override fun getMySavedPostsCount(uid: String): Flow<Result<Int>> {
        // Asumsi kita punya sub-collection "savedPosts" di dalam dokumen masjid
        return firestore.collection("masjid").document(uid).collection("savedPosts")
            .snapshots()
            .map { snapshot ->
                try {
                    Result.success(snapshot.size())
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }
}