// domain/usecase/GetAllPostsUseCase.kt
package com.hjkarpet.ekomas.domain.usecase

import com.hjkarpet.ekomas.domain.repository.PostRepository

class GetAllPostsUseCase(private val repository: PostRepository) {
    operator fun invoke() = repository.getAllPosts()
}