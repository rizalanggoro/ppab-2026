package com.asprak.todolistd.feature.test.presentation.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asprak.todolistd.core.MyApplication
import com.asprak.todolistd.feature.setting.data.ThemeRepository
import com.asprak.todolistd.feature.test.data.CounterRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TestVM(
    private val counterRepository: CounterRepository,
    private val themeRepository: ThemeRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
                TestVM(
                    counterRepository = app.counterRepository,
                    themeRepository = app.themeRepository
                )
            }
        }
    }

    var counter3 by mutableIntStateOf(0)

    var counter4 by mutableIntStateOf(0)
        private set

    fun incrementCounter4() {
        counter4++
    }

    private var _counter5 = MutableStateFlow(0)
    val counter5 = _counter5.asStateFlow()

    fun incrementCounter5() {
        _counter5.value++
    }

    private var _uiState = MutableStateFlow(TestUiState())
    val uiState = _uiState.asStateFlow()

    fun incrementCounter6() {
        _uiState.update {
            it.copy(counter6 = it.counter6 + 1)
        }
    }

    private var _counter7 = MutableStateFlow(0)
    val counter7 = _counter7.asStateFlow()
    val counter7a = counter7.map { it * 2 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun incrementCounter7() {
        _counter7.value++
    }

    private var _counter8 = MutableStateFlow(0)
    val counter8 = _counter8.asStateFlow()
    val counter8a = counter8.debounce(1000)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun incrementCounter8() {
        _counter8.value++
    }

    private var _messageEvent = MutableSharedFlow<String>(replay = 0)
    val messageEvent = _messageEvent.asSharedFlow()

    fun triggerMessageEvent() = viewModelScope.launch {
        delay(2000)
        _messageEvent.emit("Hello from TestVM!")
    }

    val counter9 = counterRepository.counterFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val counter10 = counterRepository.counter
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val isDark = themeRepository.isDark
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun toggleTheme() = themeRepository.toggleTheme()
}