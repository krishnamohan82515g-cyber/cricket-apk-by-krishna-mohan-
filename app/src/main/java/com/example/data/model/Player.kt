package com.example.data.model

enum class PlayerRole(val label: String) {
    BATSMAN("Batsman"),
    BOWLER("Bowler"),
    ALL_ROUNDER("All-Rounder"),
    WICKET_KEEPER("Wicket-Keeper")
}

data class Player(
    val id: String,
    val name: String,
    val nickname: String = "",
    val jerseyNumber: Int,
    val role: PlayerRole,
    val battingStyle: String = "Right-hand bat",
    val bowlingStyle: String = "Right-arm medium",
    val isCaptain: Boolean = false,
    val isViceCaptain: Boolean = false,
    val matches: Int = 0,
    val innings: Int = 0,
    val runs: Int = 0,
    val highestScore: Int = 0,
    val ballsFaced: Int = 0,
    val fifties: Int = 0,
    val hundreds: Int = 0,
    val wickets: Int = 0,
    val oversBowled: Double = 0.0,
    val runsConceded: Int = 0,
    val catches: Int = 0
) {
    val battingAverage: Double
        get() = if (innings > 0) runs.toDouble() / innings else 0.0

    val strikeRate: Double
        get() = if (ballsFaced > 0) (runs.toDouble() / ballsFaced) * 100.0 else 0.0

    val bowlingEconomy: Double
        get() = if (oversBowled > 0.0) runsConceded / oversBowled else 0.0
}
