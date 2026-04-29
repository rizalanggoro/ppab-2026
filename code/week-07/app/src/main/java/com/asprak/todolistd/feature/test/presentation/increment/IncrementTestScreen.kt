package com.asprak.todolistd.feature.test.presentation.increment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asprak.todolistd.core.LocalBackStack

@Composable
fun IncrementTestScreen(vm: IncrementTestVM = viewModel(factory = IncrementTestVM.Factory)) {
    val backStack = LocalBackStack.current

    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.successEvent.collect {
            backStack.removeLastOrNull()
        }
    }

    Content(
        onClickIncrement = vm::increment,
        isLoading = uiState.isLoading
    )
}

@Composable
private fun Content(
    isLoading: Boolean = false,
    onClickIncrement: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Increment Test")
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (isLoading) {
                true -> LoadingIndicator()
                else -> Button(onClick = onClickIncrement) {
                    Text("Increment")
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Content()
}

@Preview
@Composable
private fun LoadingPreview() {
    Content(isLoading = true)
}