// presentation/auth/EnterPinActivity.kt
package com.hjkarpet.ekomas.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.hjkarpet.ekomas.MainActivity
import com.hjkarpet.ekomas.core.utils.SecurityPreferences
import com.hjkarpet.ekomas.databinding.ActivityEnterPinBinding

class EnterPinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterPinBinding
    private lateinit var securityPrefs: SecurityPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterPinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        securityPrefs = SecurityPreferences(this)

        binding.pinView.addTextChangedListener {
            if (it?.length == 6) {
                checkPin(it.toString())
            }
        }

        binding.tvLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            securityPrefs.clearPin()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun checkPin(pin: String) {
        if (securityPrefs.isPinCorrect(pin)) {
            navigateToMain()
        } else {
            Toast.makeText(this, "PIN Salah!", Toast.LENGTH_SHORT).show()
            binding.pinView.text = null
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}