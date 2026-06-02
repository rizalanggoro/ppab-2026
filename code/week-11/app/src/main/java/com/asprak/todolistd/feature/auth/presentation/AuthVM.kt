package com.asprak.todolistd.feature.auth.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asprak.todolistd.feature.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthVM @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _event = MutableSharedFlow<AuthUiState.Event>()
    val event = _event.asSharedFlow()

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
        validateForm()
        if (!isFormValid()) return@launch

        runCatching {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            authRepository.login(
                email = _uiState.value.email.lowercase(),
                password = _uiState.value.password,
            )
        }.onSuccess {
            _event.emit(AuthUiState.Event.AuthSucceeded)
        }.onFailure {
            _event.emit(
                AuthUiState.Event.ShowMessage(
                    message = it.message ?: "Terjadi kesalahan tak terduga!"
                )
            )
        }.also {
            _uiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun register() = viewModelScope.launch {
        runCatching {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            authRepository.register(
                name = _uiState.value.name,
                email = _uiState.value.email,
                password = _uiState.value.password,
            )
        }.onSuccess {
            _event.emit(AuthUiState.Event.AuthSucceeded)
        }.onFailure {
            _event.emit(
                AuthUiState.Event.ShowMessage(
                    message = it.message ?: "Terjadi kesalahan tak terduga!"
                )
            )
        }.also {
            _uiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }
}