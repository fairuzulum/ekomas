// presentation/auth/LoginViewModel.kt
package com.hjkarpet.ekomas.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjkarpet.ekomas.data.repository.AuthRepositoryImpl
import com.hjkarpet.ekomas.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    private val repository = AuthRepositoryImpl()
    private val loginUseCase = LoginUseCase(repository)

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email dan Password tidak boleh kosong.")
            return
        }

        viewModelScope.launch {
            val result = loginUseCase(email, password)
            result.onSuccess {
                _loginState.value = LoginState.Success
            }.onFailure {
                _loginState.value = LoginState.Error(it.message ?: "Terjadi kesalahan tidak diketahui")
            }
        }
    }
}