// data/repository/AuthRepositoryImpl.kt
package com.hjkarpet.ekomas.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.hjkarpet.ekomas.domain.model.Masjid
import com.hjkarpet.ekomas.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import android.net.Uri // Pastikan import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    override suspend fun getMasjidProfile(uid: String): Result<Masjid?> {
        return try {
            val document = firestore.collection("masjid").document(uid).get().await()
            val masjid = document.toObject(Masjid::class.java)
            Result.success(masjid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMasjidProfile(uid: String, updatedData: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("masjid").document(uid).update(updatedData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfileImage(imageUri: Uri): Result<String> = suspendCoroutine { continuation ->
        MediaManager.get().upload(imageUri)
            .unsigned("p2ekomas") // PENTING: Buat upload preset di Cloudinary
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
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
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }
}