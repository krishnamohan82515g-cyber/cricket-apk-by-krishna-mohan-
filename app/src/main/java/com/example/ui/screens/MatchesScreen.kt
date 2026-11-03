package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CricketMatch
import com.example.data.model.MatchStatus
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGoldLight
import com.example.ui.theme.CricketGoldSecondary
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.CricketGreenPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CricketViewModel

@Composable
fun MatchesScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val matches by viewModel.matches.collectAsState()
    val showAddDialog by viewModel.showAddMatchDialog.collectAsState()
    val selectedMatchDetail by viewModel.selectedMatchForDetail.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Fixtures", "Live", "Upcoming", "Completed")

    // Filter matches based on selected tab
    val filteredMatches = when (selectedTabIndex) {
        1 -> matches.filter { it.status == MatchStatus.LIVE }
        2 -> matches.filter { it.status == MatchStatus.UPCOMING }
        3 -> matches.filter { it.status == MatchStatus.COMPLETED }
        else -> matches
    }

    // Schedule Dialog Form state
    var tourneyInput by remember { mutableStateOf("") }
    var opponentInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("Sunday, 9:00 AM") }
    var venueInput by remember { mutableStateOf("Rampur Village Sports Ground") }
    var oversInput by remember { mutableStateOf("20") }

    Scaffold(
        modifier = modifier.testTag("matches_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddMatchDialog.value = true },
                containerColor = CricketGoldSecondary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_schedule_match")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Schedule Match")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Schedule", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("matches_tabs")
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("tab_match_$index")
                    )
                }
            }

            if (filteredMatches.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matches in this category",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMatches, key = { it.id }) { match ->
                        MatchCard(
                            match = match,
                            onStartLive = {
                                viewModel.startLiveMatchScoring(match)
                            },
                            onViewDetails = {
                                viewModel.selectMatchForDetail(match)
                            }
                        )
                    }
                }
            }
        }
    }

    // Schedule Match Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAddMatchDialog.value = false },
            title = { Text("Schedule Match / Fixture 🏏", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tourneyInput,
                        onValueChange = { tourneyInput = it },
                        label = { Text("Tournament / Cup Name") },
                        placeholder = { Text("e.g. Gramin Premier League 2026") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_tournament_name"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = opponentInput,
                        onValueChange = { opponentInput = it },
                        label = { Text("Opponent Village Team") },
                        placeholder = { Text("e.g. Belgaon Kings") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_opponent_name"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dateInput,
                        onValueChange = { dateInput = it },
                        label = { Text("Match Date & Time") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = venueInput,
                        onValueChange = { venueInput = it },
                        label = { Text("Venue / Ground") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = oversInput,
                        onValueChange = { oversInput = it },
                        label = { Text("Overs Limit (e.g. 10, 15, 20)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = tourneyInput.ifBlank { "Village Friendship Cup" }
                        val opp = opponentInput.ifBlank { "Navi Gaon XI" }
                        val ov = oversInput.toIntOrNull() ?: 20
                        viewModel.scheduleMatch(
                            tournamentName = title,
                            opponentTeam = opp,
                            date = dateInput,
                            time = "Morning",
                            venue = venueInput,
                            overs = ov
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark),
                    modifier = Modifier.testTag("btn_confirm_schedule")
                ) {
                    Text("Schedule Match")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAddMatchDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Match Detail & Summary Dialog
    selectedMatchDetail?.let { match ->
        AlertDialog(
            onDismissRequest = { viewModel.selectMatchForDetail(null) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.SportsCricket, contentDescription = null, tint = CricketGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(match.tournamentName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📍 ${match.venue} • ${match.oversLimit} Overs", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("📅 ${match.date} • ${match.time}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider()

                    if (match.status == MatchStatus.COMPLETED) {
                        Text("RESULT:", fontWeight = FontWeight.Bold, color = CricketGreenPrimary, fontSize = 12.sp)
                        Text(match.resultText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Rampur Tigers: ${match.teamScore}/${match.teamWickets} (${match.teamOvers} ov)", fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${match.opponentTeam}: ${match.opponentScore}/${match.opponentWickets} (${match.opponentOvers} ov)", fontWeight = FontWeight.SemiBold)
                        }
                        if (match.playerOfTheMatch.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🏅 MOTM: ${match.playerOfTheMatch}", fontWeight = FontWeight.Medium, color = CricketGoldSecondary)
                        }
                    } else if (match.status == MatchStatus.LIVE) {
                        Text("Current Match in progress! Tap 'Go to Live Scoring' to track live balls.", fontSize = 13.sp)
                    } else {
                        Text("Match is scheduled. Ground pitch preparations underway!", fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (match.status == MatchStatus.LIVE || match.status == MatchStatus.UPCOMING) {
                        viewModel.startLiveMatchScoring(match)
                        viewModel.selectMatchForDetail(null)
                    } else {
                        viewModel.selectMatchForDetail(null)
                    }
                }) {
                    Text(if (match.status == MatchStatus.COMPLETED) "Close" else "Open Live Scorer")
                }
            }
        )
    }
}

@Composable
fun MatchCard(
    match: CricketMatch,
    onStartLive: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLive = match.status == MatchStatus.LIVE
    val isCompleted = match.status == MatchStatus.COMPLETED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
            .testTag("match_card_${match.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isLive) androidx.compose.foundation.BorderStroke(1.5.dp, CricketGreenPrimary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Tournament and Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.tournamentName,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isLive -> CricketBallRed
                                isCompleted -> CricketGreenDark
                                else -> CricketGoldSecondary
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = when {
                            isLive -> "🔴 LIVE"
                            isCompleted -> "COMPLETED"
                            else -> "UPCOMING"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Teams Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Rampur Tigers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (isLive || isCompleted) {
                        Text(
                            text = "${match.teamScore}/${match.teamWickets} (${match.teamOvers} ov)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CricketGreenPrimary
                        )
                    }
                }

                Text(
                    text = "VS",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = match.opponentTeam,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (isCompleted) {
                        Text(
                            text = "${match.opponentScore}/${match.opponentWickets} (${match.opponentOvers} ov)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (isLive) {
                        Text(
                            text = "Yet to bat",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer Info & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${match.date} • ${match.venue.take(22)}...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isLive) {
                    Button(
                        onClick = onStartLive,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_go_live_${match.id}")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Score Live", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (isCompleted) {
                    Text(
                        text = match.resultText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketGreenDark
                    )
                } else {
                    OutlinedButton(
                        onClick = onStartLive,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Start Scorer", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
