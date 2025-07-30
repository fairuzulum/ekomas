package com.hjkarpet.ekomas.presentation.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjkarpet.ekomas.data.repository.PostRepositoryImpl
import com.hjkarpet.ekomas.domain.model.Post
import com.hjkarpet.ekomas.domain.usecase.GetAllPostsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

// State untuk merepresentasikan kondisi UI Beranda
sealed class BerandaState {
    object Loading : BerandaState()
    data class Success(val posts: List<Post>) : BerandaState()
    data class Error(val message: String) : BerandaState()
}

class BerandaViewModel : ViewModel() {

    // Inisialisasi use case. Di proyek besar, ini dilakukan dengan Dependency Injection.
    private val repository = PostRepositoryImpl()
    private val getAllPostsUseCase = GetAllPostsUseCase(repository)

    private val _uiState = MutableStateFlow<BerandaState>(BerandaState.Loading)
    val uiState: StateFlow<BerandaState> = _uiState

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            getAllPostsUseCase()
                .onStart {
                    // Saat mulai mengambil data, set state ke Loading
                    _uiState.value = BerandaState.Loading
                }
                .catch { exception ->
                    // Jika terjadi error saat pengumpulan data
                    _uiState.value = BerandaState.Error(exception.message ?: "Terjadi kesalahan")
                }
                .collect { result ->
                    // Hasil dari repository (berisi Result.success atau Result.failure)
                    result.onSuccess { posts ->
                        _uiState.value = BerandaState.Success(posts)
                    }.onFailure { exception ->
                        _uiState.value = BerandaState.Error(exception.message ?: "Terjadi kesalahan")
                    }
                }
        }
    }
}