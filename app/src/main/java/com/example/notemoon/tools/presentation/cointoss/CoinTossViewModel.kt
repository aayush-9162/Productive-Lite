package com.example.notemoon.tools.presentation.cointoss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject

/** Flips a fair coin, tracking the running heads/tails tally. */
@HiltViewModel
class CoinTossViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CoinTossState())
    val state: StateFlow<CoinTossState> = _state.asStateFlow()

    fun flip() {
        if (_state.value.isFlipping) return
        _state.update { it.copy(isFlipping = true) }
        viewModelScope.launch {
            delay(800) // let the flip animation play
            val side = if (Random.nextBoolean()) CoinSide.HEADS else CoinSide.TAILS
            _state.update {
                it.copy(
                    result = side,
                    isFlipping = false,
                    headsCount = it.headsCount + if (side == CoinSide.HEADS) 1 else 0,
                    tailsCount = it.tailsCount + if (side == CoinSide.TAILS) 1 else 0
                )
            }
        }
    }

    fun reset() {
        _state.update { CoinTossState() }
    }
}
