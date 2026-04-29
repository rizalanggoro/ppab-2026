package com.asprak.todolistd.feature.test.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class CounterRepository(
    private val tickHandler: TickHandler
) {
    fun counterFlow(): Flow<Int> = flow {
        var counter = 0

        tickHandler.tick.collect {
            counter++
            emit(counter)
        }
    }

    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()

    suspend fun increment() {
        delay(2000)
        _counter.value++
    }
}