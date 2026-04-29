package com.asprak.todolistd.feature.test.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class TickHandler(
    private val externalScope: CoroutineScope,
    private val tickIntervalMs: Long = 1000L
) {
    private val _tick = MutableSharedFlow<Unit>()
    val tick = _tick.asSharedFlow()

    init {
        externalScope.launch {
            while (true) {
                _tick.emit(Unit)
                delay(tickIntervalMs)
            }
        }
    }
}