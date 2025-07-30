// domain/usecase/RegisterMasjidUseCase.kt
package com.hjkarpet.ekomas.domain.usecase

import com.hjkarpet.ekomas.domain.model.Masjid
import com.hjkarpet.ekomas.domain.repository.AuthRepository

class RegisterMasjidUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, dataMasjid: Masjid) =
        repository.registerMasjid(email, password, dataMasjid)
}