package com.example.data.repository

import com.example.data.local.CricketDatabase
import com.example.data.local.HighlightEntity
import com.example.data.local.MatchEntity
import com.example.data.local.PlayerEntity
import com.example.data.local.PostEntity
import com.example.data.local.UserEntity
import com.example.data.model.BallEvent
import com.example.data.model.CommunityPost
import com.example.data.model.CricketMatch
import com.example.data.model.LiveBatsman
import com.example.data.model.LiveBowler
import com.example.data.model.MatchHighlight
import com.example.data.model.MatchStatus
import com.example.data.model.Player
import com.example.data.model.PostComment
import com.example.data.model.TeamProfile
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

data class LiveScoringState(
    val matchId: String = "m1",
    val matchTitle: String = "GPL 2026 (Final) vs Shyamnagar Strikers",
    val venue: String = "Panchayat Stadium, Rampur",
    val totalOversLimit: Int = 20,
    val innings: Int = 1, // 1: batting first, 2: chasing
    val battingTeamName: String = "Rampur Tigers",
    val bowlingTeamName: String = "Shyamnagar Strikers",
    val score: Int = 142,
    val wickets: Int = 3,
    val completedOvers: Int = 15,
    val ballsInCurrentOver: Int = 4,
    val target: Int? = null,
    val striker: LiveBatsman = LiveBatsman("Ramesh Patel", runs = 56, balls = 34, fours = 5, sixes = 3),
    val nonStriker: LiveBatsman = LiveBatsman("Amit Verma", runs = 28, balls = 18, fours = 2, sixes = 1),
    val currentBowler: LiveBowler = LiveBowler("Kallu Yadav", overs = 3, ballsInOver = 4, maidens = 0, runsConceded = 32, wickets = 1),
    val recentBallsInOver: List<String> = listOf("1", "4", "0", "6"),
    val ballHistory: List<BallEvent> = emptyList(),
    val isMatchFinished: Boolean = false,
    val resultMessage: String = ""
) {
    val totalBallsBowled: Int
        get() = (completedOvers * 6) + ballsInCurrentOver

    val currentOverDisplay: String
        get() = "$completedOvers.$ballsInCurrentOver"

    val currentRunRate: Double
        get() = if (totalBallsBowled > 0) (score.toDouble() / totalBallsBowled) * 6.0 else 0.0

    val requiredRunRate: Double?
        get() {
            if (target == null) return null
            val runsNeeded = target - score
            val ballsRemaining = (totalOversLimit * 6) - totalBallsBowled
            return if (ballsRemaining > 0 && runsNeeded > 0) {
                (runsNeeded.toDouble() / ballsRemaining) * 6.0
            } else 0.0
        }
}

class CricketRepository(private val database: CricketDatabase) {

    val players: Flow<List<Player>> = database.playerDao().getAllPlayers().map { list ->
        list.map { it.toDomain() }
    }

    val matches: Flow<List<CricketMatch>> = database.matchDao().getAllMatches().map { list ->
        list.map { it.toDomain() }
    }

    val posts: Flow<List<CommunityPost>> = database.postDao().getAllPosts().map { list ->
        list.map { it.toDomain() }
    }

    val highlights: Flow<List<MatchHighlight>> = database.highlightDao().getAllHighlights().map { list ->
        list.map { it.toDomain() }
    }

    // Live Scoring Active Session
    private val _liveScoringState = MutableStateFlow(LiveScoringState())
    val liveScoringState: StateFlow<LiveScoringState> = _liveScoringState.asStateFlow()

    // Team Profile State
    private val _teamProfile = MutableStateFlow(TeamProfile())
    val teamProfile: StateFlow<TeamProfile> = _teamProfile.asStateFlow()

    // Authentication & Profile State
    private val _currentUser = MutableStateFlow<User?>(
        // Start logged in as Krishna Mohan Shukla (Admin) for immediate seamless usage, with full sign up/login/switch support
        run {
            val defaultSalt = "8f3a9b1c7d2e4f0a"
            User(
                id = "u_krishna",
                name = "Krishna Mohan Shukla ( Rishabh shukla )",
                usernameOrEmail = "krishna_admin",
                passwordHash = UserEntity.hashPassword("password123", defaultSalt),
                role = UserRole.ADMIN,
                playerSpecialty = "Club President & Patron",
                phoneNumber = "+91 98765 43210",
                villageName = "Rampur Village",
                bio = "Village Cricket Club President and Chief Patron. Dedicated to grassroots rural sports development.",
                avatarColorHex = "#D97706",
                salt = defaultSalt,
                failedAttempts = 0,
                lockedUntil = 0L,
                securityPin = "1234"
            )
        }
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    suspend fun login(identifier: String, passwordRaw: String): Result<User> {
        val trimmed = identifier.trim()
        val userEntity = database.userDao().getUserByUsernameOrEmail(trimmed)
            ?: return Result.failure(Exception("No account found for '$trimmed'"))

        val now = System.currentTimeMillis()

        // 1. Check brute-force lockout protection
        if (userEntity.lockedUntil > now) {
            val secondsRemaining = ((userEntity.lockedUntil - now) / 1000L).coerceAtLeast(1L)
            return Result.failure(Exception("Account temporarily locked due to excessive failed attempts. Try again in ${secondsRemaining}s."))
        }

        // 2. Verify password with 1000-round SHA-256 and constant-time comparison
        val isValid = UserEntity.verifyPassword(passwordRaw, userEntity.passwordHash, userEntity.salt)
        if (!isValid) {
            val newFailedAttempts = userEntity.failedAttempts + 1
            val lockTime = if (newFailedAttempts >= 5) now + 60_000L else 0L
            val updatedUserEntity = userEntity.copy(
                failedAttempts = newFailedAttempts,
                lockedUntil = lockTime
            )
            database.userDao().updateUser(updatedUserEntity)

            return if (newFailedAttempts >= 5) {
                Result.failure(Exception("Account locked for 60 seconds due to 5 consecutive failed attempts."))
            } else {
                val attemptsLeft = 5 - newFailedAttempts
                Result.failure(Exception("Incorrect password. $attemptsLeft attempts remaining before account lockout."))
            }
        }

        // 3. Reset failed attempts on successful authentication
        if (userEntity.failedAttempts > 0 || userEntity.lockedUntil > 0L) {
            database.userDao().updateUser(userEntity.copy(failedAttempts = 0, lockedUntil = 0L))
        }

        val user = userEntity.copy(failedAttempts = 0, lockedUntil = 0L).toDomain()
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun signUp(
        name: String,
        usernameOrEmail: String,
        passwordRaw: String,
        role: UserRole,
        jerseyNumber: Int? = null,
        playerSpecialty: String? = null,
        phoneNumber: String = "",
        villageName: String = "Rampur Village",
        bio: String = ""
    ): Result<User> {
        val trimmedIdentifier = usernameOrEmail.trim()
        if (trimmedIdentifier.isBlank()) {
            return Result.failure(Exception("Username or email cannot be empty"))
        }
        if (passwordRaw.length < 4) {
            return Result.failure(Exception("Password must be at least 4 characters"))
        }

        val existing = database.userDao().getUserByUsernameOrEmail(trimmedIdentifier)
        if (existing != null) {
            return Result.failure(Exception("An account with username '$trimmedIdentifier' already exists"))
        }

        val salt = UserEntity.generateSalt()
        val passwordHash = UserEntity.hashPassword(passwordRaw, salt)

        val newUser = User(
            id = "u_${UUID.randomUUID().toString().take(8)}",
            name = name.ifBlank { "Village Member" },
            usernameOrEmail = trimmedIdentifier,
            passwordHash = passwordHash,
            role = role,
            jerseyNumber = jerseyNumber,
            playerSpecialty = playerSpecialty,
            phoneNumber = phoneNumber,
            villageName = villageName.ifBlank { "Rampur Village" },
            bio = bio,
            avatarColorHex = when (role) {
                UserRole.ADMIN -> "#D97706"
                UserRole.PLAYER -> "#1B5E20"
                UserRole.FAN -> "#B91C1C"
            },
            salt = salt,
            failedAttempts = 0,
            lockedUntil = 0L
        )

        database.userDao().insertUser(UserEntity.fromDomain(newUser))
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    suspend fun updateUserProfile(
        name: String,
        role: UserRole,
        jerseyNumber: Int? = null,
        playerSpecialty: String? = null,
        phoneNumber: String,
        villageName: String,
        bio: String
    ): Result<User> {
        val current = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        val updated = current.copy(
            name = name.ifBlank { current.name },
            role = role,
            jerseyNumber = jerseyNumber,
            playerSpecialty = playerSpecialty,
            phoneNumber = phoneNumber,
            villageName = villageName,
            bio = bio
        )
        database.userDao().updateUser(UserEntity.fromDomain(updated))
        _currentUser.value = updated
        return Result.success(updated)
    }

    suspend fun quickLoginRole(role: UserRole) {
        val allUsers = when (role) {
            UserRole.ADMIN -> database.userDao().getUserByUsernameOrEmail("krishna_admin")
            UserRole.PLAYER -> database.userDao().getUserByUsernameOrEmail("ramesh_player")
            UserRole.FAN -> database.userDao().getUserByUsernameOrEmail("raju_fan")
        }
        if (allUsers != null) {
            _currentUser.value = allUsers.toDomain()
        } else {
            // Fallback user if database hasn't populated yet
            val defaultSalt = "demo_salt_secure_2026"
            _currentUser.value = User(
                id = "demo_${role.name.lowercase()}",
                name = when (role) {
                    UserRole.ADMIN -> "Krishna Mohan Shukla ( Rishabh shukla )"
                    UserRole.PLAYER -> "Ramesh Patel (#7)"
                    UserRole.FAN -> "Raju Chaiwala (Fan)"
                },
                usernameOrEmail = when (role) {
                    UserRole.ADMIN -> "krishna_admin"
                    UserRole.PLAYER -> "ramesh_player"
                    UserRole.FAN -> "raju_fan"
                },
                passwordHash = UserEntity.hashPassword("password123", defaultSalt),
                role = role,
                salt = defaultSalt
            )
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun toggleFanMembership() {
        val current = _teamProfile.value
        val isMember = !current.isUserFanMember
        val fanDelta = if (isMember) 1 else -1
        _teamProfile.value = current.copy(
            isUserFanMember = isMember,
            fanCount = current.fanCount + fanDelta
        )
    }

    suspend fun savePlayer(player: Player) {
        database.playerDao().insertPlayer(PlayerEntity.fromDomain(player))
    }

    suspend fun deletePlayer(id: String) {
        database.playerDao().deletePlayer(id)
    }

    suspend fun saveMatch(match: CricketMatch) {
        database.matchDao().insertMatch(MatchEntity.fromDomain(match))
    }

    suspend fun deleteMatch(id: String) {
        database.matchDao().deleteMatch(id)
    }

    suspend fun savePost(post: CommunityPost) {
        database.postDao().insertPost(PostEntity.fromDomain(post))
    }

    suspend fun togglePostLike(post: CommunityPost) {
        val updated = post.copy(
            isLiked = !post.isLiked,
            likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
        )
        database.postDao().updatePost(PostEntity.fromDomain(updated))
    }

    suspend fun addCommentToPost(postId: String, author: String, text: String, currentPost: CommunityPost) {
        val newComment = PostComment(
            id = UUID.randomUUID().toString(),
            author = author,
            text = text,
            timeAgo = "Just now"
        )
        val updatedComments = currentPost.comments + newComment
        val updatedPost = currentPost.copy(comments = updatedComments)
        database.postDao().updatePost(PostEntity.fromDomain(updatedPost))
    }

    suspend fun saveHighlight(highlight: MatchHighlight) {
        database.highlightDao().insertHighlight(HighlightEntity.fromDomain(highlight))
    }

    suspend fun toggleHighlightLike(highlight: MatchHighlight) {
        val updated = highlight.copy(
            isLiked = !highlight.isLiked,
            likesCount = if (highlight.isLiked) highlight.likesCount - 1 else highlight.likesCount + 1
        )
        database.highlightDao().updateHighlight(HighlightEntity.fromDomain(updated))
    }

    fun initLiveMatch(match: CricketMatch) {
        _liveScoringState.value = LiveScoringState(
            matchId = match.id,
            matchTitle = "${match.tournamentName} vs ${match.opponentTeam}",
            venue = match.venue,
            totalOversLimit = match.oversLimit,
            battingTeamName = if (match.teamBattingFirst) "Rampur Tigers" else match.opponentTeam,
            bowlingTeamName = if (match.teamBattingFirst) match.opponentTeam else "Rampur Tigers",
            score = match.teamScore,
            wickets = match.teamWickets,
            completedOvers = match.teamOvers.toInt(),
            ballsInCurrentOver = ((match.teamOvers - match.teamOvers.toInt()) * 10).toInt(),
            striker = LiveBatsman("Ramesh Patel", runs = 56, balls = 34, fours = 5, sixes = 3),
            nonStriker = LiveBatsman("Amit Verma", runs = 28, balls = 18, fours = 2, sixes = 1),
            currentBowler = LiveBowler("Opp. Bowler", overs = 3, ballsInOver = 4, runsConceded = 32, wickets = 1),
            recentBallsInOver = listOf("1", "4", "0", "6")
        )
    }

    fun recordBall(
        runs: Int,
        isWide: Boolean = false,
        isNoBall: Boolean = false,
        isBye: Boolean = false,
        isLegBye: Boolean = false,
        isWicket: Boolean = false,
        dismissalType: String? = null,
        newBatsmanName: String? = null
    ) {
        val state = _liveScoringState.value
        if (state.isMatchFinished) return

        var newScore = state.score + runs
        var newWickets = state.wickets
        val isLegalBall = !isWide && !isNoBall

        var striker = state.striker
        var nonStriker = state.nonStriker
        var bowler = state.currentBowler

        var commentaryText = ""

        if (isWide) {
            newScore += 1
            bowler = bowler.copy(runsConceded = bowler.runsConceded + 1 + runs)
            commentaryText = "Wide ball! Bowler strays outside guidelines. +1 extra."
        } else if (isNoBall) {
            newScore += 1
            bowler = bowler.copy(runsConceded = bowler.runsConceded + 1 + runs)
            striker = striker.copy(
                runs = striker.runs + runs,
                balls = striker.balls + 1,
                fours = if (runs == 4) striker.fours + 1 else striker.fours,
                sixes = if (runs == 6) striker.sixes + 1 else striker.sixes
            )
            commentaryText = "No ball called! Free hit coming up. Batsman scores $runs runs."
        } else if (isWicket) {
            newWickets += 1
            bowler = bowler.copy(
                wickets = bowler.wickets + 1,
                runsConceded = bowler.runsConceded + runs
            )
            commentaryText = "WICKET! $dismissalType! ${striker.name} departs after scoring ${striker.runs}."
            val nextName = newBatsmanName ?: "Next Village Batsman"
            striker = LiveBatsman(name = nextName)
        } else {
            // Normal scoring ball
            bowler = bowler.copy(runsConceded = bowler.runsConceded + runs)
            if (!isBye && !isLegBye) {
                striker = striker.copy(
                    runs = striker.runs + runs,
                    balls = striker.balls + 1,
                    fours = if (runs == 4) striker.fours + 1 else striker.fours,
                    sixes = if (runs == 6) striker.sixes + 1 else striker.sixes
                )
            } else {
                striker = striker.copy(balls = striker.balls + 1)
            }

            commentaryText = when (runs) {
                0 -> "Dot ball. Clean block into the off side."
                1 -> "Single taken down to long on."
                2 -> "Good running between the wickets, couple taken."
                3 -> "Superb placement, village boys turn 2 into 3!"
                4 -> "FOUR! Sweet crack of the willow races across the grass!"
                6 -> "SIX! Up, up and away over long-off into the village crowd!"
                else -> "$runs runs added to the total."
            }
        }

        // Calculate ball index in over
        var completedOvers = state.completedOvers
        var ballsInCurrentOver = state.ballsInCurrentOver
        var recentBalls = state.recentBallsInOver.toMutableList()

        val ballTag = when {
            isWicket -> "W"
            isWide -> "Wd"
            isNoBall -> "Nb"
            runs == 0 -> "•"
            else -> "$runs"
        }
        recentBalls.add(ballTag)

        if (isLegalBall) {
            ballsInCurrentOver += 1
            bowler = bowler.copy(ballsInOver = bowler.ballsInOver + 1)

            if (ballsInCurrentOver == 6) {
                completedOvers += 1
                ballsInCurrentOver = 0
                bowler = bowler.copy(
                    overs = bowler.overs + 1,
                    ballsInOver = 0
                )
                recentBalls.clear()
                // Automatic strike rotation at over end
                val temp = striker
                striker = nonStriker
                nonStriker = temp
                commentaryText += " End of over $completedOvers."
            } else if (runs % 2 == 1) {
                // Strike rotation on odd runs
                val temp = striker
                striker = nonStriker
                nonStriker = temp
            }
        } else if (runs % 2 == 1) {
            // Wide or No Ball with odd runs
            val temp = striker
            striker = nonStriker
            nonStriker = temp
        }

        val ballEvent = BallEvent(
            overIndex = completedOvers,
            ballIndex = ballsInCurrentOver,
            runs = runs,
            isWide = isWide,
            isNoBall = isNoBall,
            isBye = isBye,
            isLegBye = isLegBye,
            isWicket = isWicket,
            dismissalType = dismissalType,
            strikerName = striker.name,
            bowlerName = bowler.name,
            commentary = commentaryText
        )

        // Check if innings/match reached limit
        var matchFinished = false
        var resultText = ""
        if (completedOvers >= state.totalOversLimit || newWickets >= 10) {
            matchFinished = true
            resultText = "Innings completed! Total: $newScore/$newWickets in $completedOvers.$ballsInCurrentOver ov."
        }

        _liveScoringState.value = state.copy(
            score = newScore,
            wickets = newWickets,
            completedOvers = completedOvers,
            ballsInCurrentOver = ballsInCurrentOver,
            striker = striker,
            nonStriker = nonStriker,
            currentBowler = bowler,
            recentBallsInOver = recentBalls,
            ballHistory = listOf(ballEvent) + state.ballHistory,
            isMatchFinished = matchFinished,
            resultMessage = resultText
        )
    }

    fun swapStrike() {
        val state = _liveScoringState.value
        _liveScoringState.value = state.copy(
            striker = state.nonStriker,
            nonStriker = state.striker
        )
    }

    fun setNewBowler(bowlerName: String) {
        val state = _liveScoringState.value
        _liveScoringState.value = state.copy(
            currentBowler = LiveBowler(name = bowlerName)
        )
    }

    fun undoLastBall() {
        val state = _liveScoringState.value
        if (state.ballHistory.isEmpty()) return

        val last = state.ballHistory.first()
        val remainingHistory = state.ballHistory.drop(1)

        val restoredScore = (state.score - last.runs - (if (last.isWide || last.isNoBall) 1 else 0)).coerceAtLeast(0)
        val restoredWickets = (if (last.isWicket) state.wickets - 1 else state.wickets).coerceAtLeast(0)

        var completedOvers = state.completedOvers
        var ballsInCurrentOver = state.ballsInCurrentOver

        if (!last.isWide && !last.isNoBall) {
            if (ballsInCurrentOver > 0) {
                ballsInCurrentOver -= 1
            } else if (completedOvers > 0) {
                completedOvers -= 1
                ballsInCurrentOver = 5
            }
        }

        val updatedRecent = if (state.recentBallsInOver.isNotEmpty()) {
            state.recentBallsInOver.dropLast(1)
        } else emptyList()

        _liveScoringState.value = state.copy(
            score = restoredScore,
            wickets = restoredWickets,
            completedOvers = completedOvers,
            ballsInCurrentOver = ballsInCurrentOver,
            recentBallsInOver = updatedRecent,
            ballHistory = remainingHistory,
            isMatchFinished = false,
            resultMessage = ""
        )
    }

    suspend fun completeMatch(finalResultText: String, motm: String) {
        val state = _liveScoringState.value
        val updatedMatch = database.matchDao().getMatchById(state.matchId)?.let { current ->
            current.copy(
                status = MatchStatus.COMPLETED.name,
                teamScore = state.score,
                teamWickets = state.wickets,
                teamOvers = "${state.completedOvers}.${state.ballsInCurrentOver}".toDoubleOrNull() ?: 20.0,
                resultText = finalResultText,
                playerOfTheMatch = motm
            )
        }
        if (updatedMatch != null) {
            database.matchDao().updateMatch(updatedMatch)
        }
        _liveScoringState.value = state.copy(
            isMatchFinished = true,
            resultMessage = finalResultText
        )
    }
}
