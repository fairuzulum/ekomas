package com.hjkarpet.ekomas.presentation.analitik

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hjkarpet.ekomas.data.repository.AnalyticsRepositoryImpl
import com.hjkarpet.ekomas.domain.model.AnalyticsData
import com.hjkarpet.ekomas.domain.model.PostStatus
import com.hjkarpet.ekomas.domain.model.PostType
import com.hjkarpet.ekomas.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class AnalitikState {
    object Loading : AnalitikState()
    data class Success(val data: AnalyticsData) : AnalitikState()
    data class Error(val message: String) : AnalitikState()
}

class AnalitikViewModel : ViewModel() {

    private val repository: AnalyticsRepository = AnalyticsRepositoryImpl()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<AnalitikState>(AnalitikState.Loading)
    val uiState: StateFlow<AnalitikState> = _uiState

    init {
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _uiState.value = AnalitikState.Error("User tidak login.")
            return
        }

        viewModelScope.launch {
            // Menggabungkan beberapa Flow menjadi satu
            val myPostsFlow = repository.getMyPosts(currentUser.uid)
            val likedCountFlow = repository.getMyLikedPostsCount(currentUser.uid)
            val savedCountFlow = repository.getMySavedPostsCount(currentUser.uid)

            combine(myPostsFlow, likedCountFlow, savedCountFlow) { postsResult, likedResult, savedResult ->
                // Jika salah satu gagal, seluruh operasi dianggap gagal
                postsResult.getOrThrow()
                likedResult.getOrThrow()
                savedResult.getOrThrow()

                // Proses perhitungan jika semua berhasil
                val myPosts = postsResult.getOrNull() ?: emptyList()
                AnalyticsData(
                    publishedArticles = myPosts.count { it.status == PostStatus.PUBLISHED && it.type == PostType.ARTIKEL },
                    publishedEvents = myPosts.count { it.status == PostStatus.PUBLISHED && it.type == PostType.KEGIATAN },
                    drafts = myPosts.count { it.status == PostStatus.DRAFT },
                    likedPosts = likedResult.getOrNull() ?: 0,
                    savedPosts = savedResult.getOrNull() ?: 0
                )
            }.collect { analyticsData ->
                _uiState.value = AnalitikState.Success(analyticsData)
            }
        }
    }
}