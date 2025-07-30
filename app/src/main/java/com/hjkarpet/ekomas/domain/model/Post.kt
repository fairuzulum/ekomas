// domain/model/Post.kt
package com.hjkarpet.ekomas.domain.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Post(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String = "", // URL dari Cloudinary
    val type: PostType = PostType.ARTIKEL,

    // Informasi penulis (Masjid)
    val authorUid: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String = "",

    @ServerTimestamp
    val createdAt: Date? = null,

    // Data interaksi
    val likesCount: Long = 0,
    val savesCount: Long = 0,
)