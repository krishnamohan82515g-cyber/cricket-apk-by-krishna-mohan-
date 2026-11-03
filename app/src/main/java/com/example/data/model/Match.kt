package com.example.data.model

enum class MatchStatus(val label: String) {
    UPCOMING("Upcoming"),
    LIVE("Live"),
    COMPLETED("Completed")
}

data class BallEvent(
    val overIndex: Int,
    val ballIndex: Int, // 1 to 6
    val runs: Int,
    val isWide: Boolean = false,
    val isNoBall: Boolean = false,
    val isBye: Boolean = false,
    val isLegBye: Boolean = false,
    val isWicket: Boolean = false,
    val dismissalType: String? = null,
    val strikerName: String,
    val bowlerName: String,
    val commentary: String
)

data class LiveBatsman(
    val name: String,
    val runs: Int = 0,
    val balls: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val isOut: Boolean = false,
    val dismissalInfo: String = "not out"
) {
    val strikeRate: Double
        get() = if (balls > 0) (runs.toDouble() / balls) * 100.0 else 0.0
}

data class LiveBowler(
    val name: String,
    val overs: Int = 0,
    val ballsInOver: Int = 0,
    val maidens: Int = 0,
    val runsConceded: Int = 0,
    val wickets: Int = 0
) {
    val totalOversDisplay: String
        get() = "$overs.$ballsInOver"

    val economy: Double
        get() {
            val totalOvers = overs + (ballsInOver / 6.0)
            return if (totalOvers > 0) runsConceded / totalOvers else 0.0
        }
}

data class CricketMatch(
    val id: String,
    val tournamentName: String,
    val opponentTeam: String,
    val date: String,
    val time: String,
    val venue: String,
    val oversLimit: Int = 20,
    val status: MatchStatus = MatchStatus.UPCOMING,
    val tossResult: String = "",
    val teamBattingFirst: Boolean = true,
    // Scores
    val teamScore: Int = 0,
    val teamWickets: Int = 0,
    val teamOvers: Double = 0.0,
    val opponentScore: Int = 0,
    val opponentWickets: Int = 0,
    val opponentOvers: Double = 0.0,
    val resultText: String = "",
    val playerOfTheMatch: String = ""
)
