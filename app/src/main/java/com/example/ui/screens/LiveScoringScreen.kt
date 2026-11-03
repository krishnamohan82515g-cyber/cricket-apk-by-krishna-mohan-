package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGoldLight
import com.example.ui.theme.CricketGoldSecondary
import com.example.data.model.UserRole
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGoldLight
import com.example.ui.theme.CricketGoldSecondary
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.CricketGreenPrimary
import com.example.ui.viewmodel.CricketViewModel
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiveScoringScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val liveState by viewModel.liveScoringState.collectAsState()
    val players by viewModel.players.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showScorecardModal by remember { mutableStateOf(false) }
    var selectedDismissalType by remember { mutableStateOf("Bowled") }
    var nextBatsmanInput by remember { mutableStateOf("") }
    var newBowlerInput by remember { mutableStateOf("") }
    var finishWinnerInput by remember { mutableStateOf("Rampur Tigers won by 24 runs") }
    var motmInput by remember { mutableStateOf("Ramesh Patel") }

    fun triggerVibrate(durationMs: Long = 50) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                v?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    // Pulsating animation for the LIVE badge
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_scoring_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tournament & Match Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Live indicator badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(CricketBallRed.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CricketBallRed)
                                    .alpha(pulseAlpha)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE TOURNAMENT",
                                color = CricketBallRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(
                            onClick = { showScorecardModal = true },
                            modifier = Modifier.testTag("btn_view_scorecard")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Full Scorecard",
                                tint = CricketGreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = liveState.matchTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "📍 ${liveState.venue} • ${liveState.totalOversLimit} Overs Match",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Main Live Score Board
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_scoreboard_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(CricketGreenDark, CricketGreenPrimary)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = liveState.battingTeamName.uppercase(),
                                color = CricketGoldLight,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Overs: ${liveState.currentOverDisplay} / ${liveState.totalOversLimit}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${liveState.score}/${liveState.wickets}",
                                    color = Color.White,
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "CRR: ${String.format(Locale.US, "%.2f", liveState.currentRunRate)}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                liveState.requiredRunRate?.let { rrr ->
                                    Text(
                                        text = "RRR: ${String.format(Locale.US, "%.2f", rrr)}",
                                        color = CricketGoldLight,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // This Over Timeline Pills
                        Text(
                            text = "THIS OVER:",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (liveState.recentBallsInOver.isEmpty()) {
                                Text(
                                    text = "New over starting...",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            } else {
                                liveState.recentBallsInOver.forEach { ball ->
                                    val isWicket = ball == "W"
                                    val isBoundaryFour = ball == "4"
                                    val isBoundarySix = ball == "6"
                                    val pillBg = when {
                                        isWicket -> CricketBallRed
                                        isBoundarySix -> CricketGoldSecondary
                                        isBoundaryFour -> Color(0xFF2E7D32)
                                        else -> Color.White.copy(alpha = 0.2f)
                                    }
                                    val pillText = when {
                                        isWicket || isBoundaryFour || isBoundarySix -> Color.White
                                        else -> Color.White
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(pillBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ball,
                                            color = pillText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Batsmen & Bowler Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("partnership_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BATSMEN ON CREASE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        FilledTonalButton(
                            onClick = {
                                triggerVibrate(30)
                                viewModel.swapStrike()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_swap_strike")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Swap Strike",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Swap Strike", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Striker Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SportsCricket,
                                contentDescription = "Striker",
                                tint = CricketGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${liveState.striker.name}*",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "${liveState.striker.runs} (${liveState.striker.balls}b) • 4s:${liveState.striker.fours} 6s:${liveState.striker.sixes} • SR:${String.format(Locale.US, "%.1f", liveState.striker.strikeRate)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Non-Striker Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Non-Striker",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = liveState.nonStriker.name,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "${liveState.nonStriker.runs} (${liveState.nonStriker.balls}b) • 4s:${liveState.nonStriker.fours} 6s:${liveState.nonStriker.sixes}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // Current Bowler Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BOWLER: ${liveState.currentBowler.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "O: ${liveState.currentBowler.totalOversDisplay} | M: ${liveState.currentBowler.maidens} | R: ${liveState.currentBowler.runsConceded} | W: ${liveState.currentBowler.wickets} | Econ: ${String.format(Locale.US, "%.1f", liveState.currentBowler.economy)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        OutlinedButton(
                            onClick = { viewModel.showChangeBowlerDialog.value = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("btn_change_bowler")
                        ) {
                            Text("Change Bowler", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Live Scoring Keypad / Role Access Card
        item {
            if (currentUser == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_prompt_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Log In to Score Matches", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Sign in with your role (Admin, Player, Fan) to unlock scoring or personalize your stats.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.showAuthDialog.value = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
                        ) {
                            Text("Log In / Sign Up", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (currentUser?.role == UserRole.FAN) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fan_mode_scoring_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CricketBallRed.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CricketBallRed.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📣", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Village Fan Viewing Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Button(
                                onClick = { viewModel.quickLoginRole(UserRole.ADMIN) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CricketGoldSecondary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_switch_to_admin")
                            ) {
                                Text("Switch to Admin 🛡️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "You are following live balls in Fan mode. Live scoring controls (recording runs, extras, and wickets) are reserved for Club Admins and Match Officials.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scoring_keypad_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RECORD BALL / SCORING CONSOLE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Logged in as ${currentUser?.role?.badge}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CricketGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                    // Main Run Buttons (0, 1, 2, 3, 4, 6)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0, 1, 2, 3).forEach { run ->
                            Button(
                                onClick = {
                                    triggerVibrate(30)
                                    viewModel.recordBall(runs = run)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_run_$run"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (run == 0) "• Dot" else "+$run",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Boundary Buttons: 4 (Green) and 6 (Gold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                triggerVibrate(60)
                                viewModel.recordBall(runs = 4)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("btn_run_4"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CricketGreenPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🏏 4 FOUR!", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        }

                        Button(
                            onClick = {
                                triggerVibrate(90)
                                viewModel.recordBall(runs = 6)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("btn_run_6"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CricketGoldSecondary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("💥 6 SIXER!", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Extras Row: Wide, No Ball, Bye, Leg Bye
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                triggerVibrate(40)
                                viewModel.recordBall(runs = 0, isWide = true)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_wide"),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("Wide (+1)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = {
                                triggerVibrate(40)
                                viewModel.recordBall(runs = 0, isNoBall = true)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_noball"),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("No Ball", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = {
                                triggerVibrate(30)
                                viewModel.recordBall(runs = 1, isBye = true)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_bye"),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("Bye (+1)", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                triggerVibrate(30)
                                viewModel.recordBall(runs = 1, isLegBye = true)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_legbye"),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("L-Bye (+1)", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Critical Actions: WICKET, UNDO, FINISH
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                triggerVibrate(100)
                                viewModel.showDismissalDialog.value = true
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("btn_wicket"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CricketBallRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("⚡ WICKET (OUT!)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                triggerVibrate(30)
                                viewModel.undoLastBall()
                            },
                            modifier = Modifier
                                .weight(0.9f)
                                .height(48.dp)
                                .testTag("btn_undo_ball"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Undo", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.showFinishMatchDialog.value = true },
                            modifier = Modifier
                                .weight(0.9f)
                                .height(48.dp)
                                .testTag("btn_finish_match"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CricketGreenDark,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Finish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

        // Live Commentary Tape
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("commentary_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "LIVE BALL-BY-BALL COMMENTARY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (liveState.ballHistory.isEmpty()) {
                        Text(
                            text = "Live match commentary will appear here ball by ball as you score.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        liveState.ballHistory.take(5).forEach { event ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (event.isWicket) CricketBallRed
                                            else if (event.runs >= 4) CricketGoldSecondary
                                            else MaterialTheme.colorScheme.primaryContainer
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${event.overIndex}.${event.ballIndex}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (event.isWicket || event.runs >= 4) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = event.commentary,
                                    fontSize = 13.sp,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }
            }
        }
    }

    // Dismissal Dialog
    val showDismissal by viewModel.showDismissalDialog.collectAsState()
    if (showDismissal) {
        val dismissalOptions = listOf("Bowled", "Caught", "LBW", "Run Out", "Stumped", "Hit Wicket")
        AlertDialog(
            onDismissRequest = { viewModel.showDismissalDialog.value = false },
            title = { Text("Record Wicket ⚡", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select dismissal method:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        dismissalOptions.forEach { type ->
                            val isSelected = selectedDismissalType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CricketBallRed else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { selectedDismissalType = type }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = type,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = nextBatsmanInput,
                        onValueChange = { nextBatsmanInput = it },
                        label = { Text("Next Batsman Name") },
                        placeholder = { Text("e.g. Munna Tiwari") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_next_batsman"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = nextBatsmanInput.ifBlank { "Deepak Yadav" }
                        viewModel.recordBall(
                            runs = 0,
                            isWicket = true,
                            dismissalType = selectedDismissalType,
                            newBatsmanName = name
                        )
                        viewModel.showDismissalDialog.value = false
                        nextBatsmanInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketBallRed),
                    modifier = Modifier.testTag("btn_confirm_wicket")
                ) {
                    Text("Confirm Wicket")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDismissalDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Change Bowler Dialog
    val showBowlerDialog by viewModel.showChangeBowlerDialog.collectAsState()
    if (showBowlerDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showChangeBowlerDialog.value = false },
            title = { Text("Change Bowler 🏏", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select from squad or enter new bowler name:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    players.filter { it.role.name.contains("BOWL") || it.role.name.contains("ALL") }.take(4).forEach { p ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    viewModel.setNewBowler(p.name)
                                },
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${p.name} (${p.bowlingStyle})",
                                modifier = Modifier.padding(10.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newBowlerInput,
                        onValueChange = { newBowlerInput = it },
                        label = { Text("Or Type Bowler Name") },
                        placeholder = { Text("e.g. Suresh Kumar") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_bowler"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newBowlerInput.isNotBlank()) {
                            viewModel.setNewBowler(newBowlerInput)
                            newBowlerInput = ""
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_new_bowler")
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showChangeBowlerDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Finish Match Dialog
    val showFinishDialog by viewModel.showFinishMatchDialog.collectAsState()
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showFinishMatchDialog.value = false },
            title = { Text("Complete Match & Finalize 🏆", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Final Score: ${liveState.score}/${liveState.wickets} in ${liveState.currentOverDisplay} overs.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = finishWinnerInput,
                        onValueChange = { finishWinnerInput = it },
                        label = { Text("Match Result Summary") },
                        placeholder = { Text("e.g. Rampur Tigers won by 24 runs") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_match_result"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = motmInput,
                        onValueChange = { motmInput = it },
                        label = { Text("Player of the Match (MOTM)") },
                        placeholder = { Text("e.g. Ramesh Patel") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_motm"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.finishMatch(finishWinnerInput, motmInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark),
                    modifier = Modifier.testTag("btn_confirm_finish")
                ) {
                    Text("Save & Complete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showFinishMatchDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Full Scorecard Dialog
    if (showScorecardModal) {
        AlertDialog(
            onDismissRequest = { showScorecardModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = CricketGoldSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Match Scorecard", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Text(
                            text = "${liveState.battingTeamName} • ${liveState.score}/${liveState.wickets} (${liveState.currentOverDisplay} ov)",
                            fontWeight = FontWeight.Bold,
                            color = CricketGreenPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "BATTING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Batsmen scorecard
                    val allBatsmen = listOf(
                        Triple(liveState.striker.name, "${liveState.striker.runs}* (${liveState.striker.balls})", "4s: ${liveState.striker.fours} | 6s: ${liveState.striker.sixes}"),
                        Triple(liveState.nonStriker.name, "${liveState.nonStriker.runs}* (${liveState.nonStriker.balls})", "4s: ${liveState.nonStriker.fours} | 6s: ${liveState.nonStriker.sixes}"),
                        Triple("Munna Tiwari", "32 (18)", "c deep midwicket b Bowler"),
                        Triple("Deepak Yadav", "18 (12)", "b Bowler")
                    )

                    items(allBatsmen) { (name, scoreStr, info) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text(info, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(scoreStr, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "BOWLING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(liveState.currentBowler.name, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text(
                                "${liveState.currentBowler.totalOversDisplay}-${liveState.currentBowler.maidens}-${liveState.currentBowler.runsConceded}-${liveState.currentBowler.wickets}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showScorecardModal = false }) {
                    Text("Close")
                }
            }
        )
    }
}
