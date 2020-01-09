package com.example.data.firebase

data class CO2Node(var timestamp: Long, var latitude: Double, var longitude: Double, var gramsPerSec: Float) {
    constructor() : this(0L, 0.0,0.0,0f)
}