package dev.gerits.speed.data.statistics

import kotlinx.serialization.Serializable

@Serializable
data class Statistics(
    val maxSpeed: Float = 0f,
    val totalDistance: Float = 0f
)
