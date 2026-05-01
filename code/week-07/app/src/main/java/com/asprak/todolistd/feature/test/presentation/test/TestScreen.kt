package com.asprak.todolistd.feature.test.presentation.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.core.Routes

@Composable
fun TestScreen(
    vm: TestVM = viewModel(factory = TestVM.Factory),
    vm2: Test2VM = viewModel()
) {
    val backStack = LocalBackStack.current
    val hostState = remember { SnackbarHostState() }

    var counter1 by remember { mutableIntStateOf(0) }
    var counter2 by rememberSaveable { mutableIntStateOf(0) }

    val counter5 by vm.counter5.collectAsState()
    val uiState by vm.uiState.collectAsState()

    val counter7 by vm.counter7.collectAsState()
    val counter7a by vm.counter7a.collectAsState()

    val counter8 by vm.counter8.collectAsState()
    val counter8a by vm.counter8a.collectAsState()
    val counter9 by vm.counter9.collectAsState()
    val counter10 by vm.counter10.collectAsState()
    val isDark by vm.isDark.collectAsState()

    LaunchedEffect(Unit) {
        vm.messageEvent.collect {
            hostState.showSnackbar(it)
        }
    }

    Content(
        counter1 = counter1,
        onClickIncrementCounter1 = { counter1++ },
        counter2 = counter2,
        onClickIncrementCounter2 = { counter2++ },

        counter3 = vm.counter3,
        onClickIncrementCounter3 = { vm.counter3++ },
        counter4 = vm.counter4,
        onClickIncrementCounter4 = { vm.incrementCounter4() },

        counter5 = counter5,
        onClickIncrementCounter5 = vm::incrementCounter5,
        counter6 = uiState.counter6,
        onClickIncrementCounter6 = vm::incrementCounter6,

        counter7 = counter7,
        counter7a = counter7a,
        onClickIncrementCounter7 = vm::incrementCounter7,

        counter8 = counter8,
        counter8a = counter8a,
        onClickIncrementCounter8 = vm::incrementCounter8,

        onClickTriggerMessageEvent = vm::triggerMessageEvent,

        hostState = hostState,
        counter9 = counter9,
        onClickIncrement = {
            backStack.add(Routes.IncrementTestRoute)
        },
        counter10 = counter10,
        isDark = isDark,
        onClickToggleTheme = vm::toggleTheme
    )
}

@Composable
private fun Content(
    counter1: Int = 0,
    onClickIncrementCounter1: () -> Unit = {},
    counter2: Int = 0,
    onClickIncrementCounter2: () -> Unit = {},
    counter3: Int = 0,
    onClickIncrementCounter3: () -> Unit = {},
    counter4: Int = 0,
    onClickIncrementCounter4: () -> Unit = {},
    counter5: Int = 0,
    onClickIncrementCounter5: () -> Unit = {},
    counter6: Int = 0,
    onClickIncrementCounter6: () -> Unit = {},
    counter7: Int = 0,
    counter7a: Int = 0,
    onClickIncrementCounter7: () -> Unit = {},
    counter8: Int = 0,
    counter8a: Int = 0,
    onClickIncrementCounter8: () -> Unit = {},
    onClickTriggerMessageEvent: () -> Unit = {},
    hostState: SnackbarHostState = remember { SnackbarHostState() },
    counter9: Int = 0,
    onClickIncrement: () -> Unit = {},
    counter10: Int = 0,
    isDark: Boolean = false,
    onClickToggleTheme: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Test")
                },
                actions = {
                    IconButton(onClick = onClickToggleTheme) {
                        Icon(
                            imageVector = if (isDark) Icons.Rounded.LightMode
                            else Icons.Rounded.DarkMode,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = hostState)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onClickIncrement) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = null
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("mutableStateOf")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Counter: $counter1",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = onClickIncrementCounter1) {
                            Text("Increment")
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("saveable mutableStateOf")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Counter: $counter2",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = onClickIncrementCounter2) {
                            Text("Increment")
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("mutableStateOf in ViewModel")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Counter: $counter3",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = onClickIncrementCounter3) {
                            Text("Increment")
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("mutableStateOf in ViewModel private set")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Counter: $counter4",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = onClickIncrementCounter4) {
                            Text("Increment")
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("MutableStateFlow in ViewModel")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Counter: $counter5",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = onClickIncrementCounter5) {
                            Text("Increment")
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("MutableStateFlow in ViewModel with UI State")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Counter: $counter6",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = onClickIncrementCounter6) {
                            Text("Increment")
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("MutableStateFlow in ViewModel with map")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Counter: $counter7 * 2 = $counter7a",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = onClickIncrementCounter7) {
                            Text("Increment")
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("MutableStateFlow in ViewModel with debounce")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Counter: $counter8 ~ $counter8a",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = onClickIncrementCounter8) {
                            Text("Increment")
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("MutableSharedFlow in ViewModel")
                    Button(onClick = onClickTriggerMessageEvent) {
                        Text("Trigger message event")
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("MutableSharedFlow with TickHandler")
                    Text(
                        "Counter: $counter9",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Counter from repository")
                    Text(
                        "Counter: $counter10",
                        style = MaterialTheme.typography.titleMedium,
                    )
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