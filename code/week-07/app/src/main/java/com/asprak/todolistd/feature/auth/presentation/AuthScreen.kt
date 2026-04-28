package com.asprak.todolistd.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun AuthScreen(vm: AuthVM = viewModel(factory = AuthVM.Factory)) {
    val backStack = LocalBackStack.current

    val uiState by vm.uiState.collectAsState()

    Content(
        isLogin = uiState.isLogin,
        isLoading = uiState.isLoading,
        name = uiState.name,
        email = uiState.email,
        password = uiState.password,
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
        }
    )
}

@Composable
private fun Content(
    isLogin: Boolean = true,
    isLoading: Boolean = false,
    name: String = "",
    email: String = "",
    password: String = "",
    confirmPassword: String = "",
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
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                TextField(
                    value = email,
                    onValueChange = onChangeEmail,
                    placeholder = {
                        Text("Alamat email")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                TextField(
                    value = password,
                    onValueChange = onChangePassword,
                    placeholder = {
                        Text("Kata sandi")
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
                        modifier = Modifier.align(Alignment.CenterHorizontally)
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
        Content()
    }
}

@Preview(group = "Login")
@Composable
private fun LoginLoadingPreview() {
    TodoListTheme {
        Content(
            isLoading = true
        )
    }
}

@Preview(group = "Register")
@Composable
private fun RegisterPreview() {
    TodoListTheme {
        Content(
            isLogin = false
        )
    }
}

@Preview(group = "Register")
@Composable
private fun RegisterLoadingPreview() {
    TodoListTheme {
        Content(
            isLoading = true,
            isLogin = false
        )
    }
}