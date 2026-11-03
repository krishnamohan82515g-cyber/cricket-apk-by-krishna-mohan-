package com.example.data.model

enum class UserRole(val label: String, val badge: String, val description: String) {
    ADMIN("Admin / Official", "🛡️ Admin", "Can schedule matches, score live games, and manage club operations"),
    PLAYER("Team Player", "🏏 Player", "Can link jersey number, manage player profile, and upload match highlights"),
    FAN("Village Fan", "📣 Fan", "Can cheer with claps, comment on chaupal posts, vote in polls, and join fan club")
}

data class User(
    val id: String,
    val name: String,
    val usernameOrEmail: String,
    val passwordHash: String,
    val role: UserRole,
    val jerseyNumber: Int? = null,
    val playerSpecialty: String? = null,
    val phoneNumber: String = "",
    val villageName: String = "Rampur Village",
    val bio: String = "",
    val avatarColorHex: String = "#1B5E20",
    val joinedAt: Long = System.currentTimeMillis(),
    val salt: String = "",
    val failedAttempts: Int = 0,
    val lockedUntil: Long = 0L,
    val securityPin: String? = null
) {
    val isLockedOut: Boolean
        get() = lockedUntil > System.currentTimeMillis()

    val primaryName: String
        get() {
            val openParen = name.indexOf('(')
            return if (openParen > 0) name.substring(0, openParen).trim() else name
        }

    val secondaryAlias: String?
        get() {
            val openParen = name.indexOf('(')
            val closeParen = name.indexOf(')', openParen)
            return if (openParen in 0 until closeParen) {
                name.substring(openParen, closeParen + 1).trim()
            } else if (openParen >= 0) {
                name.substring(openParen).trim()
            } else null
        }
}
