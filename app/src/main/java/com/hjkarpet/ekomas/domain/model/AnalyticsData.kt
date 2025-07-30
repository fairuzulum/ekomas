package com.hjkarpet.ekomas.domain.model

data class AnalyticsData(
    val publishedArticles: Int = 0,
    val publishedEvents: Int = 0,
    val drafts: Int = 0,
    val likedPosts: Int = 0,
    val savedPosts: Int = 0
)