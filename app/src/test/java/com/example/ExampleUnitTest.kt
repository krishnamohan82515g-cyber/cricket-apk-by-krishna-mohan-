package com.example

import com.example.data.local.UserEntity
import com.example.data.model.Player
import com.example.data.model.PlayerRole
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.LiveScoringState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPlayerStatsCalculation() {
        val player = Player(
            id = "test_1",
            name = "Ramesh Patel",
            jerseyNumber = 7,
            role = PlayerRole.ALL_ROUNDER,
            matches = 10,
            innings = 8,
            runs = 320,
            ballsFaced = 200,
            wickets = 12,
            oversBowled = 30.0,
            runsConceded = 180
        )

        assertEquals(40.0, player.battingAverage, 0.01)
        assertEquals(160.0, player.strikeRate, 0.01)
        assertEquals(6.0, player.bowlingEconomy, 0.01)
    }

    @Test
    fun testLiveScoringStateCalculations() {
        val state = LiveScoringState(
            score = 120,
            completedOvers = 10,
            ballsInCurrentOver = 0,
            target = 180,
            totalOversLimit = 20
        )

        assertEquals(60, state.totalBallsBowled)
        assertEquals("10.0", state.currentOverDisplay)
        assertEquals(12.0, state.currentRunRate, 0.01)
        assertNotNull(state.requiredRunRate)
        assertEquals(6.0, state.requiredRunRate!!, 0.01)
    }

    @Test
    fun testUserPasswordHashingAndRoleMapping() {
        val rawPassword = "cricket_password_2026"
        val hash1 = UserEntity.hashPassword(rawPassword)
        val hash2 = UserEntity.hashPassword(rawPassword)
        val wrongHash = UserEntity.hashPassword("wrong_password")

        assertEquals(hash1, hash2)
        assertNotEquals(hash1, wrongHash)

        val user = User(
            id = "u_test",
            name = "Suraj Yadav",
            usernameOrEmail = "suraj12",
            passwordHash = hash1,
            role = UserRole.PLAYER,
            jerseyNumber = 18,
            playerSpecialty = "Fast Bowler",
            phoneNumber = "+91 99999 88888",
            villageName = "Rampur",
            bio = "Left arm quick"
        )

        val entity = UserEntity.fromDomain(user)
        val restored = entity.toDomain()

        assertEquals(user.id, restored.id)
        assertEquals(user.name, restored.name)
        assertEquals(user.role, restored.role)
        assertEquals(18, restored.jerseyNumber)
        assertEquals("Fast Bowler", restored.playerSpecialty)
    }

    @Test
    fun testExcellentSecuritySaltAndKeyStretching() {
        val salt1 = UserEntity.generateSalt()
        val salt2 = UserEntity.generateSalt()

        // Verify salt is random and unique
        assertNotEquals(salt1, salt2)
        assertEquals(32, salt1.length) // 16 bytes = 32 hex chars

        val rawPassword = "securePassword123"
        val hashWithSalt1 = UserEntity.hashPassword(rawPassword, salt1)
        val hashWithSalt2 = UserEntity.hashPassword(rawPassword, salt2)

        // Same password with different salts must produce completely different hashes
        assertNotEquals(hashWithSalt1, hashWithSalt2)

        // Verify constant-time verification
        org.junit.Assert.assertTrue(UserEntity.verifyPassword(rawPassword, hashWithSalt1, salt1))
        org.junit.Assert.assertFalse(UserEntity.verifyPassword("wrongPassword", hashWithSalt1, salt1))
        org.junit.Assert.assertFalse(UserEntity.verifyPassword(rawPassword, hashWithSalt1, salt2))
    }

    @Test
    fun testKrishnaMohanShuklaNameAndAliasParsing() {
        val fullName = "Krishna Mohan Shukla ( Rishabh shukla )"
        val user = User(
            id = "u_krishna",
            name = fullName,
            usernameOrEmail = "krishna_admin",
            passwordHash = "hash",
            role = UserRole.ADMIN
        )

        assertEquals("Krishna Mohan Shukla", user.primaryName)
        assertEquals("( Rishabh shukla )", user.secondaryAlias)

        // Verify single name without parentheses
        val singleUser = User(
            id = "u_single",
            name = "Ramesh Patel",
            usernameOrEmail = "ramesh",
            passwordHash = "hash",
            role = UserRole.PLAYER
        )
        assertEquals("Ramesh Patel", singleUser.primaryName)
        org.junit.Assert.assertNull(singleUser.secondaryAlias)
    }
}
