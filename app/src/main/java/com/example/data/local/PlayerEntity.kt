package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Player
import com.example.data.model.PlayerRole

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nickname: String,
    val jerseyNumber: Int,
    val role: String,
    val battingStyle: String,
    val bowlingStyle: String,
    val isCaptain: Boolean,
    val isViceCaptain: Boolean,
    val matches: Int,
    val innings: Int,
    val runs: Int,
    val highestScore: Int,
    val ballsFaced: Int,
    val fifties: Int,
    val hundreds: Int,
    val wickets: Int,
    val oversBowled: Double,
    val runsConceded: Int,
    val catches: Int
) {
    fun toDomain(): Player {
        val parsedRole = try {
            PlayerRole.valueOf(role)
        } catch (e: Exception) {
            PlayerRole.ALL_ROUNDER
        }
        return Player(
            id = id,
            name = name,
            nickname = nickname,
            jerseyNumber = jerseyNumber,
            role = parsedRole,
            battingStyle = battingStyle,
            bowlingStyle = bowlingStyle,
            isCaptain = isCaptain,
            isViceCaptain = isViceCaptain,
            matches = matches,
            innings = innings,
            runs = runs,
            highestScore = highestScore,
            ballsFaced = ballsFaced,
            fifties = fifties,
            hundreds = hundreds,
            wickets = wickets,
            oversBowled = oversBowled,
            runsConceded = runsConceded,
            catches = catches
        )
    }

    companion object {
        fun fromDomain(player: Player): PlayerEntity {
            return PlayerEntity(
                id = player.id,
                name = player.name,
                nickname = player.nickname,
                jerseyNumber = player.jerseyNumber,
                role = player.role.name,
                battingStyle = player.battingStyle,
                bowlingStyle = player.bowlingStyle,
                isCaptain = player.isCaptain,
                isViceCaptain = player.isViceCaptain,
                matches = player.matches,
                innings = player.innings,
                runs = player.runs,
                highestScore = player.highestScore,
                ballsFaced = player.ballsFaced,
                fifties = player.fifties,
                hundreds = player.hundreds,
                wickets = player.wickets,
                oversBowled = player.oversBowled,
                runsConceded = player.runsConceded,
                catches = player.catches
            )
        }
    }
}
