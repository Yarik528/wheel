package com.example.wheeloffortune

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WheelViewModel : ViewModel() {

    private val _items = MutableStateFlow(listOf("Пицца", "Суши", "Бургеры", "Паста", "Салат"))
    val items: StateFlow<List<String>> = _items

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning

    private val _currentRotation = MutableStateFlow(0f)
    val currentRotation: StateFlow<Float> = _currentRotation

    private val _winner = MutableStateFlow<String?>(null)
    val winner: StateFlow<String?> = _winner

    fun addItem(item: String) {
        if (item.isNotBlank() && _items.value.size < 20) {
            _items.value = _items.value + item
        }
    }

    fun removeItem(index: Int) {
        if (index in _items.value.indices) {
            _items.value = _items.value.toMutableList().apply { removeAt(index) }
        }
    }

    fun spin(context: Context) {
        if (_isSpinning.value || _items.value.size < 2) return

        _isSpinning.value = true
        _winner.value = null

        // Случайный угол + несколько полных оборотов
        val randomAngle = (0..360).random().toFloat()
        val fullRotations = (5..10).random()
        val targetRotation = _currentRotation.value + (fullRotations * 360f) + randomAngle

        _currentRotation.value = targetRotation

        // Определяем победителя через 4 секунды (время анимации)
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            kotlinx.coroutines.delay(4000)
            val finalAngle = (targetRotation % 360f)
            val segmentAngle = 360f / _items.value.size
            val winnerIndex = (_items.value.size - (finalAngle / segmentAngle).toInt()) % _items.value.size
            _winner.value = _items.value[winnerIndex]
            _isSpinning.value = false

            // Вибрация при победе
            vibrate(context)
        }
    }

    private fun vibrate(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                manager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (_: Exception) {}
    }

    fun reset() {
        _winner.value = null
    }
}
