package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.User
import com.example.data.model.UserRole
import java.security.MessageDigest
import java.security.SecureRandom

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val usernameOrEmail: String,
    val passwordHash: String,
    val role: String,
    val jerseyNumber: Int?,
    val playerSpecialty: String?,
    val phoneNumber: String,
    val villageName: String,
    val bio: String,
    val avatarColorHex: String,
    val joinedAt: Long,
    val salt: String = "",
    val failedAttempts: Int = 0,
    val lockedUntil: Long = 0L,
    val securityPin: String? = null
) {
    fun toDomain(): User {
        val parsedRole = try {
            UserRole.valueOf(role)
        } catch (e: Exception) {
            UserRole.FAN
        }
        return User(
            id = id,
            name = name,
            usernameOrEmail = usernameOrEmail,
            passwordHash = passwordHash,
            role = parsedRole,
            jerseyNumber = jerseyNumber,
            playerSpecialty = playerSpecialty,
            phoneNumber = phoneNumber,
            villageName = villageName,
            bio = bio,
            avatarColorHex = avatarColorHex,
            joinedAt = joinedAt,
            salt = salt,
            failedAttempts = failedAttempts,
            lockedUntil = lockedUntil,
            securityPin = securityPin
        )
    }

    companion object {
        private val secureRandom = SecureRandom()

        fun generateSalt(): String {
            val bytes = ByteArray(16)
            secureRandom.nextBytes(bytes)
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun fromDomain(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                name = user.name,
                usernameOrEmail = user.usernameOrEmail,
                passwordHash = user.passwordHash,
                role = user.role.name,
                jerseyNumber = user.jerseyNumber,
                playerSpecialty = user.playerSpecialty,
                phoneNumber = user.phoneNumber,
                villageName = user.villageName,
                bio = user.bio,
                avatarColorHex = user.avatarColorHex,
                joinedAt = user.joinedAt,
                salt = user.salt,
                failedAttempts = user.failedAttempts,
                lockedUntil = user.lockedUntil,
                securityPin = user.securityPin
            )
        }

        fun hashPassword(password: String, salt: String = ""): String {
            val md = MessageDigest.getInstance("SHA-256")
            val combined = (password + salt).toByteArray(Charsets.UTF_8)
            var hashBytes = md.digest(combined)
            // 1000 key stretching iterations for excellent level cryptographic resistance against brute force
            for (i in 1..999) {
                hashBytes = md.digest(hashBytes)
            }
            return hashBytes.joinToString("") { "%02x".format(it) }
        }

        fun verifyPassword(rawPassword: String, storedHash: String, salt: String): Boolean {
            val computedHash = hashPassword(rawPassword, salt)
            // Constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(
                computedHash.toByteArray(Charsets.UTF_8),
                storedHash.toByteArray(Charsets.UTF_8)
            )
        }
    }
}
