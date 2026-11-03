package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CricketMatch
import com.example.data.model.MatchStatus

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
    val tournamentName: String,
    val opponentTeam: String,
    val date: String,
    val time: String,
    val venue: String,
    val oversLimit: Int,
    val status: String,
    val tossResult: String,
    val teamBattingFirst: Boolean,
    val teamScore: Int,
    val teamWickets: Int,
    val teamOvers: Double,
    val opponentScore: Int,
    val opponentWickets: Int,
    val opponentOvers: Double,
    val resultText: String,
    val playerOfTheMatch: String
) {
    fun toDomain(): CricketMatch {
        val parsedStatus = try {
            MatchStatus.valueOf(status)
        } catch (e: Exception) {
            MatchStatus.UPCOMING
        }
        return CricketMatch(
            id = id,
            tournamentName = tournamentName,
            opponentTeam = opponentTeam,
            date = date,
            time = time,
            venue = venue,
            oversLimit = oversLimit,
            status = parsedStatus,
            tossResult = tossResult,
            teamBattingFirst = teamBattingFirst,
            teamScore = teamScore,
            teamWickets = teamWickets,
            teamOvers = teamOvers,
            opponentScore = opponentScore,
            opponentWickets = opponentWickets,
            opponentOvers = opponentOvers,
            resultText = resultText,
            playerOfTheMatch = playerOfTheMatch
        )
    }

    companion object {
        fun fromDomain(match: CricketMatch): MatchEntity {
            return MatchEntity(
                id = match.id,
                tournamentName = match.tournamentName,
                opponentTeam = match.opponentTeam,
                date = match.date,
                time = match.time,
                venue = match.venue,
                oversLimit = match.oversLimit,
                status = match.status.name,
                tossResult = match.tossResult,
                teamBattingFirst = match.teamBattingFirst,
                teamScore = match.teamScore,
                teamWickets = match.teamWickets,
                teamOvers = match.teamOvers,
                opponentScore = match.opponentScore,
                opponentWickets = match.opponentWickets,
                opponentOvers = match.opponentOvers,
                resultText = match.resultText,
                playerOfTheMatch = match.playerOfTheMatch
            )
        }
    }
}
