package dev.gerits.speed.data.location

data class Location(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val bearing: Float
)