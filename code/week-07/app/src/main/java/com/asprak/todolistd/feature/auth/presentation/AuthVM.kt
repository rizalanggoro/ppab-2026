package com.asprak.todolistd.feature.auth.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asprak.todolistd.core.MyApplication
import com.asprak.todolistd.feature.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthVM(
    private val authRepository: AuthRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val authRepository = (this[APPLICATION_KEY] as MyApplication).authRepository
                AuthVM(
                    authRepository = authRepository
                )
            }
        }
    }

    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent = _messageEvent.asSharedFlow()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private fun validateForm() {
        // validate email and password
        _uiState.update {
            it.copy(
                emailError = if (
                    it.email.isBlank() ||
                    !Patterns.EMAIL_ADDRESS.matcher(it.email).matches()
                ) "Alamat email tidak valid!" else null,
                passwordError = if (it.password.isBlank()) "Kata sandi tidak boleh kosong!" else null,
                nameError = if (!it.isLogin && it.name.isBlank()) "Nama tidak boleh kosong!" else null,
                confirmPasswordError = if (!it.isLogin) {
                    when (it.confirmPassword.isBlank()) {
                        true -> "Konfirmasi kata sandi tidak boleh kosong!"
                        false -> when (it.confirmPassword != it.password) {
                            true -> "Konfirmasi kata sandi tidak cocok!"
                            false -> null
                        }
                    }
                } else null
            )
        }
    }

    private fun isFormValid(): Boolean {
        with(_uiState.value) {
            val forms = mutableListOf(emailError, passwordError)
            if (!isLogin) {
                forms.add(nameError)
                forms.add(confirmPasswordError)
            }
            return forms.all { it == null }
        }
    }

    fun onChangeMode() =
        _uiState.update { it.copy(isLogin = !it.isLogin) }

    fun onChangeName(name: String) =
        _uiState.update { it.copy(name = name) }

    fun onChangeEmail(email: String) =
        _uiState.update { it.copy(email = email) }

    fun onChangePassword(password: String) =
        _uiState.update { it.copy(password = password) }

    fun onChangeConfirmPassword(confirmPassword: String) =
        _uiState.update { it.copy(confirmPassword = confirmPassword) }

    fun login() = viewModelScope.launch {
        try {
            validateForm()
            if (!isFormValid()) return@launch

            _uiState.update { it.copy(isLoading = true) }

            // login
            with(_uiState.value) {
                authRepository.login(
                    email = email,
                    password = password
                )
            }
        } catch (e: Exception) {
            _messageEvent.emit(e.message ?: "Terjadi kesalahan tak terduga!")
        } finally {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun register() = viewModelScope.launch {
        try {
            validateForm()
            if (!isFormValid()) return@launch

            _uiState.update { it.copy(isLoading = true) }

            // login
            with(_uiState.value) {
                authRepository.register(
                    name = name,
                    email = email,
                    password = password
                )
            }
        } catch (e: Exception) {
            _messageEvent.emit(e.message ?: "Terjadi kesalahan tak terduga!")
        } finally {
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}