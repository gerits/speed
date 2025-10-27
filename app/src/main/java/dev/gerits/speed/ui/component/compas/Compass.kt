package dev.gerits.speed.ui.component.compas

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.gerits.speed.ui.theme.SpeedTheme
import kotlin.math.round


@Composable
@ExperimentalMaterial3ExpressiveApi
fun Compass(modifier: Modifier = Modifier, orientation: Float) {
    var unwrappedAngle by remember { mutableStateOf(0f) }

    LaunchedEffect(orientation) {
        val currentCircleAngle = (unwrappedAngle % 360 + 360) % 360
        val delta = shortestAngleDelta(currentCircleAngle, -orientation)
        unwrappedAngle += delta
    }

    val animatedRotation by animateFloatAsState(
        targetValue = unwrappedAngle,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Spacer(
            modifier =
                Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .clip(MaterialShapes.Square.toShape())
                    .background(MaterialTheme.colorScheme.secondaryContainer)
        )
        Spacer(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .aspectRatio(1f)
                    .rotate(animatedRotation)
                    .clip(MaterialShapes.Arrow.toShape())
                    .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = determineDirection(orientation),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.displaySmall
        )
    }
}

private fun determineDirection(direction: Float): String {
    val roundedDirection = round(direction / 45f)
    return when (roundedDirection) {
        0f -> "N"
        1f -> "NE"
        2f -> "E"
        3f -> "SE"
        4f -> "S"
        5f -> "SW"
        6f -> "W"
        7f -> "NW"
        else -> "N"
    }
}

private fun shortestAngleDelta(currentAngle: Float, newAngle: Float): Float {
    var delta = (newAngle - currentAngle) % 360
    if (delta > 180) {
        delta -= 360
    } else if (delta < -180) {
        delta += 360
    }
    return delta
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun CompassPreview() {
    SpeedTheme {
        Compass(
            orientation = 150f, modifier = Modifier.fillMaxSize()
        )
    }

}