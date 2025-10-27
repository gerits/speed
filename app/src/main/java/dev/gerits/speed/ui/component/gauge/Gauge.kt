package dev.gerits.speed.ui.component.gauge

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gerits.speed.ui.theme.SpeedTheme

@Composable
@ExperimentalMaterial3ExpressiveApi
fun Gauge(modifier: Modifier = Modifier, speed: Float, maximum: Float = 150f, showText: Boolean = true) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val strokeWidth = 16.dp
    val startAngle = -220f
    val sweepAngle = 260f

    val animatedSpeed by animateFloatAsState(
        targetValue = speed.coerceIn(0f, maximum) / maximum,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    val progressSweep = sweepAngle * animatedSpeed

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .aspectRatio(1f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = CircleShape
                )
                .padding(16.dp)
                .fillMaxSize()
        ) {
            val strokePx = strokeWidth.toPx()

            val arcSize = Size(
                width = this.size.width - strokePx,
                height = this.size.height - strokePx
            )

            val arcOffset = Offset(x = strokePx / 2, y = strokePx / 2)

            val style = Stroke(
                width = strokePx,
                cap = StrokeCap.Round
            )

            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = style
            )

            drawArc(
                color = primaryColor,
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = style
            )
        }

        if (showText) {
            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "%.${0}f".format(speed),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayLargeEmphasized
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "km/h",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun SpeedometerPreview() {
    SpeedTheme {
        Gauge(
            speed = 50f, maximum = 150f, modifier = Modifier.fillMaxSize()
        )
        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

}