// data/repository/PostRepositoryImpl.kt
package com.hjkarpet.ekomas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.hjkarpet.ekomas.domain.model.Post
import com.hjkarpet.ekomas.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    override suspend fun createPost(post: Post): Result<Unit> {
        return try {
            firestore.collection("posts").add(post).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadPostImage(imageUri: Uri): Result<String> = suspendCoroutine { continuation ->
        // Pastikan Anda sudah membuat "unsigned" upload preset di dashboard Cloudinary
        MediaManager.get().upload(imageUri)
            .unsigned("p2ekomas") // Ganti dengan nama preset Anda
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("secure_url") as? String
                    if (url != null) {
                        continuation.resume(Result.success(url))
                    } else {
                        continuation.resume(Result.failure(Exception("URL tidak ditemukan dari Cloudinary.")))
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    continuation.resume(Result.failure(Exception(error?.description ?: "Gagal mengunggah gambar.")))
                }

                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }
}
