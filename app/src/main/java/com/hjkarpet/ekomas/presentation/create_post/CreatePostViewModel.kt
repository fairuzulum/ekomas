package com.hjkarpet.ekomas.presentation.create_post

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hjkarpet.ekomas.data.repository.AuthRepositoryImpl
import com.hjkarpet.ekomas.data.repository.PostRepositoryImpl
import com.hjkarpet.ekomas.domain.model.Masjid
import com.hjkarpet.ekomas.domain.model.Post
import com.hjkarpet.ekomas.domain.model.PostType
import com.hjkarpet.ekomas.domain.usecase.CreatePostUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CreatePostState {
    object Idle : CreatePostState()
    object Loading : CreatePostState()
    object Success : CreatePostState()
    data class Error(val message: String) : CreatePostState()
}

class CreatePostViewModel : ViewModel() {

    private val postRepository = PostRepositoryImpl()
    private val authRepository = AuthRepositoryImpl()
    private val createPostUseCase = CreatePostUseCase(postRepository)
    private val auth = FirebaseAuth.getInstance()

    private val _createPostState = MutableStateFlow<CreatePostState>(CreatePostState.Idle)
    val createPostState: StateFlow<CreatePostState> = _createPostState

    fun publishPost(title: String, content: String, type: PostType, imageUri: Uri?) {
        _createPostState.value = CreatePostState.Loading

        // Validasi
        if (title.isBlank() || content.isBlank()) {
            _createPostState.value = CreatePostState.Error("Judul dan konten tidak boleh kosong.")
            return
        }
        if (imageUri == null) {
            _createPostState.value = CreatePostState.Error("Harap pilih gambar sampul.")
            return
        }

        viewModelScope.launch {
            // Ambil data profil masjid saat ini
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _createPostState.value = CreatePostState.Error("Gagal mendapatkan data user.")
                return@launch
            }
            val profileResult = authRepository.getMasjidProfile(currentUser.uid)
            var currentMasjid: Masjid? = null
            profileResult.onSuccess { currentMasjid = it }
                .onFailure {
                    _createPostState.value = CreatePostState.Error("Gagal mendapatkan profil masjid.")
                    return@launch
                }

            if (currentMasjid == null) {
                _createPostState.value = CreatePostState.Error("Profil masjid tidak valid.")
                return@launch
            }

            // Unggah gambar ke Cloudinary
            postRepository.uploadPostImage(imageUri).onSuccess { imageUrl ->
                // Jika berhasil, buat objek Post
                val newPost = Post(
                    title = title,
                    content = content,
                    imageUrl = imageUrl,
                    type = type,
                    authorUid = currentMasjid!!.uid,
                    authorName = currentMasjid!!.namaMasjid,
                    authorPhotoUrl = currentMasjid!!.fotoProfilUrl
                )

                // Simpan objek Post ke Firestore
                createPostUseCase(newPost).onSuccess {
                    _createPostState.value = CreatePostState.Success
                }.onFailure {
                    _createPostState.value = CreatePostState.Error("Gagal menyimpan postingan: ${it.message}")
                }

            }.onFailure {
                // Jika gagal unggah gambar
                _createPostState.value = CreatePostState.Error("Gagal mengunggah gambar: ${it.message}")
            }
        }
    }
}