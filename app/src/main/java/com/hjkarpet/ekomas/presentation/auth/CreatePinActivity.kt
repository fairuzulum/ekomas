// presentation/auth/CreatePinActivity.kt
package com.hjkarpet.ekomas.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hjkarpet.ekomas.MainActivity
import com.hjkarpet.ekomas.core.utils.SecurityPreferences
import com.hjkarpet.ekomas.databinding.ActivityCreatePinBinding

class CreatePinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePinBinding
    private lateinit var securityPrefs: SecurityPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        securityPrefs = SecurityPreferences(this)

        binding.btnSavePin.setOnClickListener {
            validateAndSavePin()
        }
    }

    private fun validateAndSavePin() {
        val newPin = binding.pinViewNew.text.toString()
        val confirmPin = binding.pinViewConfirm.text.toString()

        if (newPin.length != 6 || confirmPin.length != 6) {
            Toast.makeText(this, "PIN harus 6 digit.", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPin != confirmPin) {
            Toast.makeText(this, "PIN tidak cocok. Coba lagi.", Toast.LENGTH_SHORT).show()
            binding.pinViewConfirm.text = null
            return
        }

        // Simpan PIN yang sudah di-hash
        securityPrefs.savePin(newPin)
        Toast.makeText(this, "PIN berhasil disimpan!", Toast.LENGTH_SHORT).show()

        // Navigasi ke halaman utama
        navigateToMain()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}