package com.julian.multiplayercheckers.dataclasses

data class User(
    val UID: String,
    val username: String,
    val score: Int,
    val history: Map<String, GameHistory>
)
