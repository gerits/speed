package dev.gerits.speed.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gerits.speed.ui.theme.SpeedTheme


private const val START_ARC_ANGLE = 135f
private const val SWEEP_ARC_ANGLE = 270f

@Composable
fun Gauge(modifier: Modifier = Modifier, speed: Float = 0f, maximum: Int = 150) {
    val secondaryContainer = MaterialTheme.colorScheme.secondaryContainer;
    val primary = MaterialTheme.colorScheme.primary;
    val strokeWidth = 20.dp

    Spacer(modifier = modifier.drawWithCache {
        onDrawBehind {
            inset(horizontal = strokeWidth.toPx() / 2, vertical = strokeWidth.toPx() / 2) {
                val width = size.width
                val height = size.height
                val size = width.coerceAtMost(height);

                val arcSize = Size(size, size)
                val arcStroke = Stroke(strokeWidth.toPx(), 0f, StrokeCap.Round)

                drawArc(
                    secondaryContainer,
                    START_ARC_ANGLE,
                    SWEEP_ARC_ANGLE,
                    false,
                    topLeft = Offset(width - size, (height - size) / 2),
                    size = arcSize,
                    style = arcStroke
                )

                drawArc(
                    primary,
                    START_ARC_ANGLE,
                    speed.coerceAtMost(maximum.toFloat()) / maximum * SWEEP_ARC_ANGLE,
                    false,
                    topLeft = Offset(width - size, (height - size) / 2),
                    size = arcSize,
                    style = arcStroke
                )
            }
        }
    })
}

@Preview(showBackground = true)
@Composable
fun SpeedometerPreview() {
    SpeedTheme {
        Gauge(
            speed = 40f,
            maximum = 150,
            modifier = Modifier.fillMaxSize()
        )
    }
}