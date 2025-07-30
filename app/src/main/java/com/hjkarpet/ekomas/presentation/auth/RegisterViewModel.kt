// presentation/auth/RegisterViewModel.kt
package com.hjkarpet.ekomas.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjkarpet.ekomas.data.repository.AuthRepositoryImpl
import com.hjkarpet.ekomas.domain.model.Masjid
import com.hjkarpet.ekomas.domain.usecase.RegisterMasjidUseCase
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepositoryImpl()
    private val registerUseCase = RegisterMasjidUseCase(repository)

    private val _registerState = MutableLiveData<RegisterState>(RegisterState.Idle)
    val registerState: LiveData<RegisterState> = _registerState

    fun registerMasjid(dataMasjid: Masjid, password: String, confirmPassword: String) {
        _registerState.value = RegisterState.Loading

        if (dataMasjid.namaMasjid.isBlank() || dataMasjid.email.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState.Error("Nama Masjid, Email, dan Password tidak boleh kosong.")
            return
        }
        if (password.length < 6) {
            _registerState.value = RegisterState.Error("Password minimal 6 karakter.")
            return
        }
        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Password dan konfirmasi password tidak cocok.")
            return
        }

        viewModelScope.launch {
            val result = registerUseCase(dataMasjid.email, password, dataMasjid)
            result.onSuccess {
                _registerState.value = RegisterState.Success
            }.onFailure {
                _registerState.value = RegisterState.Error(it.message ?: "Terjadi kesalahan tidak diketahui")
            }
        }
    }
}