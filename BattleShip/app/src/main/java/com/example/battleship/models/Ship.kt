package com.example.battleship.models

data class Ship(
    val coordinates: List<List<Int>>,
    var isDestroyed: Boolean = false
)
