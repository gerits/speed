package dev.gerits.speed.core.ui.component.compas

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dev.gerits.speed.core.ui.theme.SpeedTheme


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Compass(modifier: Modifier = Modifier, orientation: Float) {
    var unwrappedAngle by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(orientation) {
        val currentCircleAngle = (unwrappedAngle % 360 + 360) % 360
        val delta = shortestAngleDelta(currentCircleAngle, -orientation)
        unwrappedAngle += delta
    }

    val animatedRotation by animateFloatAsState(
        targetValue = unwrappedAngle,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
    )

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val matchHeightConstraintsFirst = this.maxHeight < this.maxWidth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(animatedRotation)
                .clip(MaterialShapes.Circle.toShape())
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .aspectRatio(1f, matchHeightConstraintsFirst)
        ) {
            WindDirection("N", Alignment.TopCenter)
            WindDirection("E", Alignment.CenterEnd)
            WindDirection("S", Alignment.BottomCenter)
            WindDirection("W", Alignment.CenterStart)
        }
        Spacer(
            modifier = Modifier
                .fillMaxSize(0.2f)
                .aspectRatio(0.8f, matchHeightConstraintsFirst)
                .clip(MaterialShapes.Arrow.toShape())
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun BoxScope.WindDirection(text: String, alignment: Alignment) {
    Text(
        modifier = Modifier
            .padding(8.dp)
            .align(alignment),
        text = text,
        textAlign = TextAlign.Center,
        fontSize = 2.em,
        color = MaterialTheme.colorScheme.primary
    )
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
            orientation = 150f, modifier = Modifier
                .width(300.dp)
                .height(100.dp)
        )
    }

}