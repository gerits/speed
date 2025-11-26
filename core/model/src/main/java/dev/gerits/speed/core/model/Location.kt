package dev.gerits.speed.core.model

data class Location(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val bearing: Float
)