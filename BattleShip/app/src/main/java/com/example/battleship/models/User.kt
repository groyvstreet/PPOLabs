package com.example.battleship.models

data class User(
    val uid: String,
    val email: String,
    val nickname: String,
    val imageUrl: String,
    val isGravatar: Boolean,
    val games: List<Game>
)
