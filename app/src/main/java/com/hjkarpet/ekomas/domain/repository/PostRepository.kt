package com.hjkarpet.ekomas.domain.repository

import android.net.Uri
import com.hjkarpet.ekomas.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getAllPosts(): Flow<Result<List<Post>>>

    // Fungsi baru untuk membuat postingan
    suspend fun createPost(post: Post): Result<Unit>

    // Fungsi baru untuk mengunggah gambar postingan
    suspend fun uploadPostImage(imageUri: Uri): Result<String>
}