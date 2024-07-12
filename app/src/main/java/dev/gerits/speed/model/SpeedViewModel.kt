package dev.gerits.speed.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SpeedViewModel : ViewModel() {
    private val _speed = MutableStateFlow(0.0f)
    private val _connected = MutableStateFlow(false)
    val speed: StateFlow<Float> get() = _speed
    val connected: StateFlow<Boolean> get() = _connected

    fun updateSpeed(newValue: Float) {
        _speed.value = newValue
    }

    fun updateConnected(newValue: Boolean) {
        _connected.value = newValue
    }
}
