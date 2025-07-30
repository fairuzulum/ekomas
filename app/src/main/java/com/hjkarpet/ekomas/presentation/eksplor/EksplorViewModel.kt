package com.hjkarpet.ekomas.presentation.eksplor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjkarpet.ekomas.data.repository.PostRepositoryImpl
import com.hjkarpet.ekomas.domain.model.Post
import com.hjkarpet.ekomas.domain.model.PostType
import com.hjkarpet.ekomas.domain.usecase.GetAllPostsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

// State untuk UI Halaman Eksplorasi
sealed class EksplorState {
    object Loading : EksplorState()
    // Success state sekarang berisi list yang sudah difilter
    data class Success(val filteredPosts: List<Post>) : EksplorState()
    data class Error(val message: String) : EksplorState()
}

class EksplorViewModel : ViewModel() {

    private val repository = PostRepositoryImpl()
    private val getAllPostsUseCase = GetAllPostsUseCase(repository)

    private val _uiState = MutableStateFlow<EksplorState>(EksplorState.Loading)
    val uiState: StateFlow<EksplorState> = _uiState

    // Menyimpan daftar postingan asli tanpa filter
    private var allPosts: List<Post> = emptyList()

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            getAllPostsUseCase()
                .onStart { _uiState.value = EksplorState.Loading }
                .catch { e -> _uiState.value = EksplorState.Error(e.message ?: "Terjadi kesalahan") }
                .collect { result ->
                    result.onSuccess { posts ->
                        allPosts = posts // Simpan daftar asli
                        // Tampilkan semua postingan sebagai default
                        _uiState.value = EksplorState.Success(posts)
                    }.onFailure { e ->
                        _uiState.value = EksplorState.Error(e.message ?: "Terjadi kesalahan")
                    }
                }
        }
    }

    /**
     * Fungsi untuk menerapkan filter berdasarkan tipe postingan.
     * @param type Tipe yang dipilih. Jika null, tampilkan semua.
     */
    fun applyFilter(type: PostType?) {
        val filteredList = if (type == null) {
            allPosts // Tampilkan semua jika filter null
        } else {
            allPosts.filter { it.type == type }
        }
        _uiState.value = EksplorState.Success(filteredList)
    }
}