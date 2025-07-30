// presentation/auth/RegisterActivity.kt
package com.hjkarpet.ekomas.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hjkarpet.ekomas.databinding.ActivityRegisterBinding
import com.hjkarpet.ekomas.domain.model.Masjid

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
        binding.tvGoToLogin.setOnClickListener {
            finish() // Kembali ke LoginActivity
        }
        binding.cardUpload.setOnClickListener {
            // Logika untuk upload foto akan ditambahkan di langkah selanjutnya
            Toast.makeText(this, "Fitur upload foto segera hadir!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser() {
        val dataMasjid = Masjid(
            namaMasjid = binding.etNamaMasjid.text.toString().trim(),
            telepon = binding.etTelepon.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            provinsi = binding.etProvinsi.text.toString().trim(),
            kabupatenKota = binding.etKabupatenKota.text.toString().trim(),
            alamatLengkap = binding.etAlamat.text.toString().trim(),
            namaDkm = binding.etDkm.text.toString().trim()
        )
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        viewModel.registerMasjid(dataMasjid, password, confirmPassword)
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is RegisterState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Registrasi berhasil! Silakan buat PIN.", Toast.LENGTH_LONG).show()
                    // Arahkan ke pembuatan PIN
                    val intent = Intent(this, CreatePinActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is RegisterState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                }
            }
        }
    }
}