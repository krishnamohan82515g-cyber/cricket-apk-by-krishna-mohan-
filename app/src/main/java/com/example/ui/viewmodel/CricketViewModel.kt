package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CricketDatabase
import com.example.data.model.CommunityPost
import com.example.data.model.CricketMatch
import com.example.data.model.MatchHighlight
import com.example.data.model.MatchStatus
import com.example.data.model.MediaType
import com.example.data.model.Player
import com.example.data.model.PlayerRole
import com.example.data.model.TeamProfile
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.CricketRepository
import com.example.data.repository.LiveScoringState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen(val title: String) {
    LIVE_SCORING("Live Match"),
    MATCHES("Fixtures"),
    PLAYERS("Squad & Stats"),
    HIGHLIGHTS("Highlights"),
    SOCIAL_FEED("Village Feed"),
    TEAM_PROFILE("Club Profile")
}

class CricketViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CricketRepository

    init {
        val db = CricketDatabase.getDatabase(application, viewModelScope)
        repository = CricketRepository(db)
    }

    val currentScreen: MutableStateFlow<AppScreen> = MutableStateFlow(AppScreen.LIVE_SCORING)

    val players: StateFlow<List<Player>> = repository.players.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val matches: StateFlow<List<CricketMatch>> = repository.matches.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val posts: StateFlow<List<CommunityPost>> = repository.posts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val highlights: StateFlow<List<MatchHighlight>> = repository.highlights.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val liveScoringState: StateFlow<LiveScoringState> = repository.liveScoringState
    val teamProfile: StateFlow<TeamProfile> = repository.teamProfile
    val currentUser: StateFlow<User?> = repository.currentUser

    // Authentication UI State
    val showAuthDialog = MutableStateFlow(false)
    val showProfileDialog = MutableStateFlow(false)
    val authErrorMessage = MutableStateFlow<String?>(null)
    val isAuthLoading = MutableStateFlow(false)

    // UI selection states
    private val _selectedPlayer = MutableStateFlow<Player?>(null)
    val selectedPlayer: StateFlow<Player?> = _selectedPlayer.asStateFlow()

    private val _selectedPostForComments = MutableStateFlow<CommunityPost?>(null)
    val selectedPostForComments: StateFlow<CommunityPost?> = _selectedPostForComments.asStateFlow()

    private val _selectedMatchForDetail = MutableStateFlow<CricketMatch?>(null)
    val selectedMatchForDetail: StateFlow<CricketMatch?> = _selectedMatchForDetail.asStateFlow()

    // Dialog Visibilities
    val showAddMatchDialog = MutableStateFlow(false)
    val showAddPlayerDialog = MutableStateFlow(false)
    val showUploadHighlightDialog = MutableStateFlow(false)
    val showCreatePostDialog = MutableStateFlow(false)
    val showDismissalDialog = MutableStateFlow(false)
    val showChangeBowlerDialog = MutableStateFlow(false)
    val showFinishMatchDialog = MutableStateFlow(false)

    fun login(identifier: String, passwordRaw: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            isAuthLoading.value = true
            authErrorMessage.value = null
            val result = repository.login(identifier, passwordRaw)
            isAuthLoading.value = false
            result.onSuccess {
                showAuthDialog.value = false
                onSuccess()
            }.onFailure {
                authErrorMessage.value = it.message ?: "Login failed"
            }
        }
    }

    fun signUp(
        name: String,
        usernameOrEmail: String,
        passwordRaw: String,
        role: UserRole,
        jerseyNumber: Int? = null,
        playerSpecialty: String? = null,
        phoneNumber: String = "",
        villageName: String = "Rampur Village",
        bio: String = "",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isAuthLoading.value = true
            authErrorMessage.value = null
            val result = repository.signUp(
                name = name,
                usernameOrEmail = usernameOrEmail,
                passwordRaw = passwordRaw,
                role = role,
                jerseyNumber = jerseyNumber,
                playerSpecialty = playerSpecialty,
                phoneNumber = phoneNumber,
                villageName = villageName,
                bio = bio
            )
            isAuthLoading.value = false
            result.onSuccess {
                showAuthDialog.value = false
                onSuccess()
            }.onFailure {
                authErrorMessage.value = it.message ?: "Sign up failed"
            }
        }
    }

    fun quickLoginRole(role: UserRole) {
        viewModelScope.launch {
            repository.quickLoginRole(role)
            showAuthDialog.value = false
        }
    }

    fun updateUserProfile(
        name: String,
        role: UserRole,
        jerseyNumber: Int? = null,
        playerSpecialty: String? = null,
        phoneNumber: String,
        villageName: String,
        bio: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.updateUserProfile(
                name = name,
                role = role,
                jerseyNumber = jerseyNumber,
                playerSpecialty = playerSpecialty,
                phoneNumber = phoneNumber,
                villageName = villageName,
                bio = bio
            )
            result.onSuccess {
                showProfileDialog.value = false
                onSuccess()
            }
        }
    }

    fun logout() {
        repository.logout()
        showProfileDialog.value = false
        showAuthDialog.value = true
    }

    fun navigateTo(screen: AppScreen) {
        currentScreen.value = screen
    }

    fun selectPlayer(player: Player?) {
        _selectedPlayer.value = player
    }

    fun selectPostForComments(post: CommunityPost?) {
        _selectedPostForComments.value = post
    }

    fun selectMatchForDetail(match: CricketMatch?) {
        _selectedMatchForDetail.value = match
    }

    // Live Scoring Actions
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
        repository.recordBall(
            runs = runs,
            isWide = isWide,
            isNoBall = isNoBall,
            isBye = isBye,
            isLegBye = isLegBye,
            isWicket = isWicket,
            dismissalType = dismissalType,
            newBatsmanName = newBatsmanName
        )
    }

    fun swapStrike() {
        repository.swapStrike()
    }

    fun setNewBowler(name: String) {
        repository.setNewBowler(name)
        showChangeBowlerDialog.value = false
    }

    fun undoLastBall() {
        repository.undoLastBall()
    }

    fun finishMatch(finalResultText: String, motm: String) {
        viewModelScope.launch {
            repository.completeMatch(finalResultText, motm)
            showFinishMatchDialog.value = false
        }
    }

    fun startLiveMatchScoring(match: CricketMatch) {
        repository.initLiveMatch(match)
        currentScreen.value = AppScreen.LIVE_SCORING
    }

    // Player Operations
    fun addPlayer(
        name: String,
        nickname: String,
        jerseyNumber: Int,
        role: PlayerRole,
        battingStyle: String,
        bowlingStyle: String,
        isCaptain: Boolean,
        isViceCaptain: Boolean
    ) {
        viewModelScope.launch {
            val player = Player(
                id = "p_${System.currentTimeMillis()}",
                name = name,
                nickname = nickname,
                jerseyNumber = jerseyNumber,
                role = role,
                battingStyle = battingStyle,
                bowlingStyle = bowlingStyle,
                isCaptain = isCaptain,
                isViceCaptain = isViceCaptain,
                matches = 0,
                runs = 0,
                wickets = 0
            )
            repository.savePlayer(player)
            showAddPlayerDialog.value = false
        }
    }

    // Match Operations
    fun scheduleMatch(
        tournamentName: String,
        opponentTeam: String,
        date: String,
        time: String,
        venue: String,
        overs: Int
    ) {
        viewModelScope.launch {
            val match = CricketMatch(
                id = "m_${System.currentTimeMillis()}",
                tournamentName = tournamentName,
                opponentTeam = opponentTeam,
                date = date,
                time = time,
                venue = venue,
                oversLimit = overs,
                status = MatchStatus.UPCOMING,
                tossResult = "Scheduled",
                resultText = "Fixture announced"
            )
            repository.saveMatch(match)
            showAddMatchDialog.value = false
        }
    }

    // Social Feed Operations
    fun createPost(
        authorName: String,
        authorRole: String,
        content: String,
        tag: String,
        mediaUri: String?
    ) {
        viewModelScope.launch {
            val post = CommunityPost(
                id = "post_${System.currentTimeMillis()}",
                authorName = authorName,
                authorRole = authorRole,
                content = content,
                tag = tag,
                mediaUri = mediaUri,
                likesCount = 1,
                isLiked = true,
                timestamp = System.currentTimeMillis()
            )
            repository.savePost(post)
            showCreatePostDialog.value = false
        }
    }

    fun togglePostLike(post: CommunityPost) {
        viewModelScope.launch {
            repository.togglePostLike(post)
            if (_selectedPostForComments.value?.id == post.id) {
                _selectedPostForComments.value = post.copy(
                    isLiked = !post.isLiked,
                    likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
                )
            }
        }
    }

    fun addComment(post: CommunityPost, author: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addCommentToPost(post.id, author, text, post)
            // update selection dialog if open
            val currentList = post.comments
            val newComment = com.example.data.model.PostComment(
                id = UUID.randomUUID().toString(),
                author = author,
                text = text,
                timeAgo = "Just now"
            )
            _selectedPostForComments.value = post.copy(comments = currentList + newComment)
        }
    }

    // Highlight Operations
    fun uploadHighlight(
        title: String,
        description: String,
        mediaUri: String?,
        mediaType: MediaType,
        matchTag: String,
        playerTag: String
    ) {
        viewModelScope.launch {
            val highlight = MatchHighlight(
                id = "h_${System.currentTimeMillis()}",
                title = title,
                description = description,
                mediaUri = mediaUri,
                mediaType = mediaType,
                duration = if (mediaType == MediaType.VIDEO) "0:45" else "Photo",
                viewsCount = 1,
                likesCount = 1,
                isLiked = true,
                matchTag = matchTag,
                playerTag = playerTag,
                timestamp = System.currentTimeMillis()
            )
            repository.saveHighlight(highlight)
            showUploadHighlightDialog.value = false
        }
    }

    fun toggleHighlightLike(highlight: MatchHighlight) {
        viewModelScope.launch {
            repository.toggleHighlightLike(highlight)
        }
    }

    fun toggleFanMembership() {
        repository.toggleFanMembership()
    }
}
