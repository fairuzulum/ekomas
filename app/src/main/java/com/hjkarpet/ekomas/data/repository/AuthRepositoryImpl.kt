// data/repository/AuthRepositoryImpl.kt
package com.hjkarpet.ekomas.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.hjkarpet.ekomas.domain.model.Masjid
import com.hjkarpet.ekomas.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun registerMasjid(email: String, password: String, dataMasjid: Masjid): Result<Unit> {
        return try {
            // 1. Buat user di Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                // 2. Jika berhasil, simpan data detail masjid ke Firestore
                // Menggunakan UID dari Auth sebagai ID dokumen di Firestore
                val masjidWithUid = dataMasjid.copy(uid = user.uid)
                firestore.collection("masjid").document(user.uid).set(masjidWithUid).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Gagal membuat user."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}