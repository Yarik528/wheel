package com.example.wheeloffortune

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WheelCanvas(
    items: List<String>,
    rotation: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Canvas(modifier = modifier.size(300.dp)) {
        if (items.isEmpty()) return@Canvas

        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width / 2f - 10f
        val segmentAngle = 360f / items.size

        val colors = listOf(
            Color(0xFFFF6B6B), // Красный
            Color(0xFF4ECDC4), // Бирюзовый
            Color(0xFFFFE66D), // Жёлтый
            Color(0xFF95E1D3), // Мятный
            Color(0xFFF38181), // Коралловый
            Color(0xFFAA96DA), // Лавандовый
            Color(0xFFFFA07A), // Лососевый
            Color(0xFF98D8C8)  // Мятный светлый
        )

        rotate(rotation, center) {
            items.forEachIndexed { index, item ->
                val startAngle = index * segmentAngle

                // Сектор
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = segmentAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // Линия-разделитель
                drawLine(
                    color = Color.White,
                    start = center,
                    end = Offset(
                        center.x + radius * cos(Math.toRadians(startAngle.toDouble())).toFloat(),
                        center.y + radius * sin(Math.toRadians(startAngle.toDouble())).toFloat()
                    ),
                    strokeWidth = 2f
                )

                // Текст
                drawContext.canvas.nativeCanvas.apply {
                    val textAngle = startAngle + segmentAngle / 2
                    val textRadius = radius * 0.65f

                    save()
                    rotate(textAngle, center.x, center.y)

                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = with(density) { 14.sp.toPx() }
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
                        setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                    }

                    val text = if (item.length > 15) item.take(12) + "..." else item
                    drawText(text, center.x + textRadius, center.y, paint)

                    restore()
                }
            }

            // Центральный круг
            drawCircle(
                color = Color.White,
                radius = 20f,
                center = center
            )
            drawCircle(
                color = Color(0xFF2C3E50),
                radius = 15f,
                center = center
            )
        }
    }
}
