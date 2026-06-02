package com.asprak.todolistd.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.core.Routes
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun AuthScreen(vm: AuthVM = hiltViewModel()) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.event.collect { event ->
            when (event) {
                AuthUiState.Event.AuthSucceeded -> backStack.apply {
                    clear()
                    add(Routes.ListTodoRoute)
                }

                is AuthUiState.Event.ShowMessage -> snackbarHostState.showSnackbar(
                    event.message
                )
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        isLogin = uiState.isLogin,
        isLoading = uiState.isLoading,
        name = uiState.name,
        email = uiState.email,
        password = uiState.password,
        emailError = uiState.emailError,
        passwordError = uiState.passwordError,
        nameError = uiState.nameError,
        confirmPasswordError = uiState.confirmPasswordError,
        confirmPassword = uiState.confirmPassword,
        onChangeName = vm::onChangeName,
        onChangeEmail = vm::onChangeEmail,
        onChangePassword = vm::onChangePassword,
        onChangeConfirmPassword = vm::onChangeConfirmPassword,
        onClickSwitch = vm::onChangeMode,
        onClickSubmit = {
            when (uiState.isLogin) {
                true -> vm.login()
                else -> vm.register()
            }
        },
    )
}

@Composable
private fun Content(
    snackbarHostState: SnackbarHostState,
    isLogin: Boolean = true,
    isLoading: Boolean = false,
    name: String = "",
    email: String = "",
    password: String = "",
    confirmPassword: String = "",
    emailError: String? = null,
    passwordError: String? = null,
    nameError: String? = null,
    confirmPasswordError: String? = null,
    onChangeName: (String) -> Unit = {},
    onChangeEmail: (String) -> Unit = {},
    onChangePassword: (String) -> Unit = {},
    onChangeConfirmPassword: (String) -> Unit = {},
    onClickSwitch: () -> Unit = {},
    onClickSubmit: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Autentikasi")
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (!isLogin)
                    TextField(
                        value = name,
                        onValueChange = onChangeName,
                        placeholder = {
                            Text("Nama lengkap")
                        },
                        isError = nameError != null,
                        supportingText = {
                            if (nameError != null) Text(nameError)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                TextField(
                    value = email,
                    onValueChange = onChangeEmail,
                    placeholder = { Text("Alamat email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,

                    isError = emailError != null,

                    supportingText = {
                        if (emailError != null) {
                            Text(emailError)
                        }
                    }
                )
                TextField(
                    value = password,
                    onValueChange = onChangePassword,
                    placeholder = {
                        Text("Kata sandi")
                    },
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) {
                            Text(passwordError)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                if (!isLogin)
                    TextField(
                        value = confirmPassword,
                        onValueChange = onChangeConfirmPassword,
                        placeholder = {
                            Text("Konfirmasi kata sandi")
                        },
                        isError = confirmPasswordError != null,
                        supportingText = {
                            if (confirmPasswordError != null) Text(confirmPasswordError)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                when (isLoading) {
                    true -> ContainedLoadingIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    else -> Button(
                        onClick = onClickSubmit,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            when (isLogin) {
                                true -> "Masuk"
                                false -> "Daftar"
                            }
                        )
                    }
                }
                TextButton(
                    onClick = onClickSwitch,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !isLoading
                ) {
                    Text(
                        when (isLogin) {
                            true -> "Belum punya akun? Daftarkan akun baru"
                            false -> "Sudah punya akun? Masuk sekarang"
                        }
                    )
                }
            }
        }
    }
}

@Preview(group = "Login")
@Composable
private fun LoginPreview() {
    TodoListTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@Preview(group = "Login")
@Composable
private fun LoginLoadingPreview() {
    TodoListTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            isLoading = true
        )
    }
}

@Preview(group = "Register")
@Composable
private fun RegisterPreview() {
    TodoListTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            isLogin = false
        )
    }
}

@Preview(group = "Register")
@Composable
private fun RegisterLoadingPreview() {
    TodoListTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            isLoading = true,
            isLogin = false
        )
    }
}