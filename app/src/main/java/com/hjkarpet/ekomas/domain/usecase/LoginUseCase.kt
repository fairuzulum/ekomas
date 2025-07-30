// domain/usecase/LoginUseCase.kt
package com.hjkarpet.ekomas.domain.usecase

import com.hjkarpet.ekomas.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}