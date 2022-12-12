package com.example.battleship.models

data class Game(
    val id: String,
    val winnerId: String,
    val loserId: String,
    val startTime: String,
    val durationTime: String,
    val winnerScore: Long,
    val loserScore: Long,
    val winnerMap: List<MutableList<Int>>,
    val loserMap: List<MutableList<Int>>,
    val winnerShips: MutableList<Ship>,
    val loserShips: MutableList<Ship>
)
