package com.hjkarpet.ekomas.domain.usecase

import com.hjkarpet.ekomas.domain.model.Post
import com.hjkarpet.ekomas.domain.repository.PostRepository

class CreatePostUseCase(private val repository: PostRepository) {
    suspend operator fun invoke(post: Post) = repository.createPost(post)
}