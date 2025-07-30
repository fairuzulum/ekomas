// domain/repository/PostRepository.kt
package com.hjkarpet.ekomas.domain.repository

import com.hjkarpet.ekomas.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getAllPosts(): Flow<Result<List<Post>>>
}