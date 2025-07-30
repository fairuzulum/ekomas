// domain/model/Masjid.kt
package com.hjkarpet.ekomas.domain.model

import com.google.firebase.firestore.DocumentId

data class Masjid(
    @DocumentId
    val uid: String = "", // Akan diisi otomatis dengan UID dari Firebase Auth
    val namaMasjid: String = "",
    val telepon: String = "",
    val email: String = "",
    val provinsi: String = "",
    val kabupatenKota: String = "",
    val alamatLengkap: String = "",
    val namaDkm: String = "",
    val fotoProfilUrl: String = "" // URL dari Cloudinary, untuk saat ini kosong
)