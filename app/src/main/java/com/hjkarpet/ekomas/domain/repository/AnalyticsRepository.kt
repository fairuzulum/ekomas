package com.hjkarpet.ekomas.domain.repository

import com.hjkarpet.ekomas.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    // Mengambil semua postingan (published & draft) milik satu masjid
    fun getMyPosts(uid: String): Flow<Result<List<Post>>>

    // Mengambil jumlah postingan yang di-like oleh masjid
    fun getMyLikedPostsCount(uid: String): Flow<Result<Int>>

    // Mengambil jumlah postingan yang di-simpan oleh masjid
    fun getMySavedPostsCount(uid: String): Flow<Result<Int>>
}