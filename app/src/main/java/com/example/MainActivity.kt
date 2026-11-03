package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AuthDialog
import com.example.ui.components.CricketBottomNav
import com.example.ui.components.CricketTopBar
import com.example.ui.components.ProfileDialog
import com.example.ui.screens.HighlightsScreen
import com.example.ui.screens.LiveScoringScreen
import com.example.ui.screens.MatchesScreen
import com.example.ui.screens.PlayersScreen
import com.example.ui.screens.SocialFeedScreen
import com.example.ui.screens.TeamProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CricketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                VillageCricketApp()
            }
        }
    }
}

@Composable
fun VillageCricketApp(
    viewModel: CricketViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val showAuth by viewModel.showAuthDialog.collectAsState()
    val showProfile by viewModel.showProfileDialog.collectAsState()

    // Handle system back navigation: return to Live Scoring if on secondary screen
    BackHandler(enabled = currentScreen != AppScreen.LIVE_SCORING) {
        viewModel.navigateTo(AppScreen.LIVE_SCORING)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("village_cricket_main_scaffold"),
        topBar = {
            CricketTopBar(
                title = when (currentScreen) {
                    AppScreen.LIVE_SCORING -> "Live Tournament Tracker"
                    AppScreen.MATCHES -> "Match Fixtures & Results"
                    AppScreen.PLAYERS -> "Squad & Career Stats"
                    AppScreen.HIGHLIGHTS -> "Reels & Match Highlights"
                    AppScreen.SOCIAL_FEED -> "Village Cricket Chaupal"
                    AppScreen.TEAM_PROFILE -> "Rampur Tigers Profile"
                },
                subtitle = when (currentScreen) {
                    AppScreen.LIVE_SCORING -> "Gramin Premier League 2026"
                    AppScreen.MATCHES -> "Official Tournament Schedule"
                    AppScreen.PLAYERS -> "Rampur Tigers 1st XI"
                    AppScreen.HIGHLIGHTS -> "Community Video Hub"
                    AppScreen.SOCIAL_FEED -> "Village Fan Social Feed"
                    AppScreen.TEAM_PROFILE -> "Est. 2014 • Home of Champions"
                },
                currentUser = currentUser,
                onUserClick = {
                    if (currentUser != null) {
                        viewModel.showProfileDialog.value = true
                    } else {
                        viewModel.showAuthDialog.value = true
                    }
                }
            )
        },
        bottomBar = {
            CricketBottomNav(
                currentScreen = currentScreen,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { targetScreen ->
                when (targetScreen) {
                    AppScreen.LIVE_SCORING -> LiveScoringScreen(viewModel = viewModel)
                    AppScreen.MATCHES -> MatchesScreen(viewModel = viewModel)
                    AppScreen.PLAYERS -> PlayersScreen(viewModel = viewModel)
                    AppScreen.HIGHLIGHTS -> HighlightsScreen(viewModel = viewModel)
                    AppScreen.SOCIAL_FEED -> SocialFeedScreen(viewModel = viewModel)
                    AppScreen.TEAM_PROFILE -> TeamProfileScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Authentication Dialog (Log In / Sign Up)
    if (showAuth) {
        AuthDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showAuthDialog.value = false }
        )
    }

    // Profile Management Dialog (View & Edit user profile and role)
    val user = currentUser
    if (showProfile && user != null) {
        ProfileDialog(
            user = user,
            viewModel = viewModel,
            onDismiss = { viewModel.showProfileDialog.value = false }
        )
    }
}
