// domain/repository/AuthRepository.kt
package com.hjkarpet.ekomas.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.hjkarpet.ekomas.domain.model.Masjid

interface AuthRepository {
    suspend fun registerMasjid(email: String, password: String, dataMasjid: Masjid): Result<Unit>
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    fun getCurrentUser(): FirebaseUser?
    suspend fun getMasjidProfile(uid: String): Result<Masjid?>
    suspend fun updateMasjidProfile(uid: String, updatedData: Map<String, Any>): Result<Unit>
    suspend fun uploadProfileImage(imageUri: android.net.Uri): Result<String> // Mengembalikan URL gambar
}
