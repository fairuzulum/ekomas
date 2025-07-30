// core/utils/SecurityPreferences.kt
package com.hjkarpet.ekomas.core.utils

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

class SecurityPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ekomas_security_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
    }

    /**
     * Menyimpan hash dari PIN
     */
    fun savePin(pin: String) {
        val editor = prefs.edit()
        editor.putString(KEY_PIN_HASH, hashString(pin))
        editor.apply()
    }

    /**
     * Memeriksa apakah PIN yang dimasukkan cocok dengan yang disimpan
     */
    fun isPinCorrect(pin: String): Boolean {
        val savedHash = prefs.getString(KEY_PIN_HASH, null)
        return savedHash == hashString(pin)
    }

    /**
     * Memeriksa apakah PIN sudah pernah diatur
     */
    fun isPinSet(): Boolean {
        return prefs.contains(KEY_PIN_HASH)
    }

    /**
     * Menghapus PIN (berguna saat logout)
     */
    fun clearPin() {
        prefs.edit().remove(KEY_PIN_HASH).apply()
    }

    /**
     * Fungsi sederhana untuk hashing SHA-256
     */
    private fun hashString(input: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}