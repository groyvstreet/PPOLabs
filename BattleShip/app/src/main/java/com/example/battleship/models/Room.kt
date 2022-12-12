package com.example.battleship.models

data class Room(
    val id: String,
    val firstPlayerId: String,
    val secondPlayerId: String,
    val firstPlayerMap: List<MutableList<Int>>,
    val secondPlayerMap: List<MutableList<Int>>,
    val firstPlayerShips: MutableList<Ship>,
    val secondPlayerShips: MutableList<Ship>,
    val firstPlayerScore: Long = 0,
    val secondPlayerScore: Long = 0,
    val isFirstPlayerMoving: Boolean = true,
    val isSecondPlayerMoving: Boolean = false,
    val isFirstPlayerReady: Boolean = false,
    val isSecondPlayerReady: Boolean = false,
    val startTime: String = "null",
    val winnerId: String = "null"
)
