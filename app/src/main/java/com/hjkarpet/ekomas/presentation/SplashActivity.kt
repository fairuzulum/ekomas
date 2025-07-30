// presentation/SplashActivity.kt
package com.hjkarpet.ekomas.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.hjkarpet.ekomas.R
import com.hjkarpet.ekomas.core.utils.SecurityPreferences
import com.hjkarpet.ekomas.presentation.auth.EnterPinActivity
import com.hjkarpet.ekomas.presentation.auth.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Buat layout sederhana untuk splash

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 1500) // Jeda 1.5 detik
    }

    private fun checkUserStatus() {
        val user = FirebaseAuth.getInstance().currentUser
        val securityPrefs = SecurityPreferences(this)

        if (user == null) {
            // Jika tidak ada user login, ke halaman Login
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            // Jika ada user login, cek apakah PIN sudah diatur
            if (securityPrefs.isPinSet()) {
                startActivity(Intent(this, EnterPinActivity::class.java))
            } else {
                // Ini terjadi jika pengguna menutup aplikasi saat proses buat PIN
                // Arahkan kembali untuk membuat PIN
                startActivity(Intent(this, com.hjkarpet.ekomas.presentation.auth.CreatePinActivity::class.java))
            }
        }
        finish()
    }
}