package com.asprak.todolistd.feature.test.presentation.increment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asprak.todolistd.core.MyApplication
import com.asprak.todolistd.feature.test.data.CounterRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IncrementTestVM(
    private val counterRepository: CounterRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                IncrementTestVM(
                    counterRepository = app.counterRepository,
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(IncrementTestUiState())
    val uiState = _uiState.asStateFlow()

    private val _successEvent = MutableSharedFlow<Unit>()
    val successEvent = _successEvent.asSharedFlow()

    fun increment() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        counterRepository.increment()
        _uiState.update { it.copy(isLoading = false) }
        _successEvent.emit(Unit)
    }
}