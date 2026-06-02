package com.asprak.todolistd.feature.auth.presentation

data class AuthUiState(
    val isLogin: Boolean = true,
    val name: String = "Rizal Dwi Anggoro",
    val nameError: String? = null,
    val email: String = "rizalanggoro@email.com",
    val emailError: String? = null,
    val password: String = "password",
    val passwordError: String? = null,
    val confirmPassword: String = "password",
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false
) {
    sealed interface Event {
        data class ShowMessage(val message: String) : Event
        data object AuthSucceeded : Event
    }
}
