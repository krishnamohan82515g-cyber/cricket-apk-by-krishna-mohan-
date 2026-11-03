package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Player
import com.example.data.model.PlayerRole
import com.example.ui.theme.CricketGoldLight
import com.example.ui.theme.CricketGoldSecondary
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.CricketGreenPrimary
import com.example.ui.viewmodel.CricketViewModel
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayersScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val players by viewModel.players.collectAsState()
    val showAddDialog by viewModel.showAddPlayerDialog.collectAsState()
    val selectedPlayer by viewModel.selectedPlayer.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf<PlayerRole?>(null) }

    // Dialog state
    var nameInput by remember { mutableStateOf("") }
    var nickInput by remember { mutableStateOf("") }
    var jerseyInput by remember { mutableStateOf("10") }
    var roleInput by remember { mutableStateOf(PlayerRole.BATSMAN) }
    var batStyleInput by remember { mutableStateOf("Right-hand bat") }
    var bowlStyleInput by remember { mutableStateOf("Right-arm medium") }
    var isCaptainInput by remember { mutableStateOf(false) }
    var isViceCaptainInput by remember { mutableStateOf(false) }

    val filteredPlayers = players.filter { player ->
        val matchesQuery = player.name.contains(searchQuery, ignoreCase = true) ||
                player.nickname.contains(searchQuery, ignoreCase = true)
        val matchesRole = selectedRoleFilter == null || player.role == selectedRoleFilter
        matchesQuery && matchesRole
    }

    val topScorer = players.maxByOrNull { it.runs }
    val topWicketTaker = players.maxByOrNull { it.wickets }

    Scaffold(
        modifier = modifier.testTag("players_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddPlayerDialog.value = true },
                containerColor = CricketGreenDark,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_player")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Player")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Player", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by player name or nickname...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_player_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Role Filter Chips
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedRoleFilter == null,
                        onClick = { selectedRoleFilter = null },
                        label = { Text("All Squad (${players.size})") },
                        modifier = Modifier.testTag("chip_role_all")
                    )
                    PlayerRole.entries.forEach { role ->
                        FilterChip(
                            selected = selectedRoleFilter == role,
                            onClick = { selectedRoleFilter = if (selectedRoleFilter == role) null else role },
                            label = { Text(role.label) },
                            modifier = Modifier.testTag("chip_role_${role.name.lowercase()}")
                        )
                    }
                }
            }

            // Top Performers Banner (Orange Cap & Purple Cap)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top Batsman Card
                    topScorer?.let { scorer ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectPlayer(scorer) }
                                .testTag("card_top_scorer"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CricketGoldLight.copy(alpha = 0.25f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.MilitaryTech, contentDescription = null, tint = CricketGoldSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("TOP BATSMAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CricketGoldSecondary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(scorer.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${scorer.runs} Runs (Avg ${String.format(Locale.US, "%.1f", scorer.battingAverage)})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Top Bowler Card
                    topWicketTaker?.let { bowler ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectPlayer(bowler) }
                                .testTag("card_top_bowler"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CricketGreenPrimary.copy(alpha = 0.15f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.SportsCricket, contentDescription = null, tint = CricketGreenPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("TOP BOWLER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CricketGreenPrimary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(bowler.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${bowler.wickets} Wickets (Econ ${String.format(Locale.US, "%.1f", bowler.bowlingEconomy)})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Players List
            items(filteredPlayers, key = { it.id }) { player ->
                PlayerCard(
                    player = player,
                    onClick = { viewModel.selectPlayer(player) }
                )
            }
        }
    }

    // Add Player Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAddPlayerDialog.value = false },
            title = { Text("Add Village Player 🏏", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Player Full Name") },
                                placeholder = { Text("e.g. Ramesh Patel") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_player_name"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = nickInput,
                                onValueChange = { nickInput = it },
                                label = { Text("Village Nickname") },
                                placeholder = { Text("e.g. Gully King, Chhotu") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_player_nick"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = jerseyInput,
                                onValueChange = { jerseyInput = it },
                                label = { Text("Jersey Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Text("Select Role:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                PlayerRole.entries.forEach { r ->
                                    val isSelected = roleInput == r
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) CricketGreenPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { roleInput = r }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = r.label,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = batStyleInput,
                                onValueChange = { batStyleInput = it },
                                label = { Text("Batting Style") },
                                placeholder = { Text("Right-hand bat") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = bowlStyleInput,
                                onValueChange = { bowlStyleInput = it },
                                label = { Text("Bowling Style") },
                                placeholder = { Text("Right-arm medium fast") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = isCaptainInput, onCheckedChange = { isCaptainInput = it })
                                Text("Team Captain", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Checkbox(checked = isViceCaptainInput, onCheckedChange = { isViceCaptainInput = it })
                                Text("Vice-Captain", fontSize = 13.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalName = nameInput.ifBlank { "New Village Player" }
                        val jersey = jerseyInput.toIntOrNull() ?: 99
                        viewModel.addPlayer(
                            name = finalName,
                            nickname = nickInput,
                            jerseyNumber = jersey,
                            role = roleInput,
                            battingStyle = batStyleInput,
                            bowlingStyle = bowlStyleInput,
                            isCaptain = isCaptainInput,
                            isViceCaptain = isViceCaptainInput
                        )
                        nameInput = ""
                        nickInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark),
                    modifier = Modifier.testTag("btn_confirm_add_player")
                ) {
                    Text("Add to Squad")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAddPlayerDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Player Details Dialog
    selectedPlayer?.let { player ->
        AlertDialog(
            onDismissRequest = { viewModel.selectPlayer(null) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CricketGoldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${player.jerseyNumber}",
                            fontWeight = FontWeight.Bold,
                            color = CricketGreenDark,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(player.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (player.nickname.isNotBlank()) {
                            Text("\"${player.nickname}\"", fontSize = 12.sp, color = CricketGoldSecondary)
                        }
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Role: ${player.role.label}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        if (player.isCaptain) Text("👑 Captain", fontWeight = FontWeight.Bold, color = CricketGoldSecondary, fontSize = 13.sp)
                        else if (player.isViceCaptain) Text("Vice-Captain", fontWeight = FontWeight.Medium, color = CricketGreenPrimary, fontSize = 13.sp)
                    }

                    Text("🏏 ${player.battingStyle} • 🎯 ${player.bowlingStyle}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    HorizontalDivider()

                    Text("BATTING STATS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatBadge("Matches", "${player.matches}")
                        StatBadge("Innings", "${player.innings}")
                        StatBadge("Runs", "${player.runs}")
                        StatBadge("High Score", "${player.highestScore}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatBadge("Average", String.format(Locale.US, "%.1f", player.battingAverage))
                        StatBadge("Strike Rate", String.format(Locale.US, "%.1f", player.strikeRate))
                        StatBadge("50s", "${player.fifties}")
                        StatBadge("100s", "${player.hundreds}")
                    }

                    HorizontalDivider()

                    Text("BOWLING & FIELDING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatBadge("Wickets", "${player.wickets}")
                        StatBadge("Overs", "${player.oversBowled}")
                        StatBadge("Economy", String.format(Locale.US, "%.1f", player.bowlingEconomy))
                        StatBadge("Catches", "${player.catches}")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.selectPlayer(null) }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PlayerCard(
    player: Player,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("player_card_${player.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Jersey Badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(CricketGreenDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${player.jerseyNumber}",
                    color = CricketGoldLight,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = player.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (player.isCaptain) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CricketGoldLight)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("C", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CricketGreenDark)
                        }
                    } else if (player.isViceCaptain) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CricketGreenPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("VC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CricketGreenPrimary)
                        }
                    }
                }

                if (player.nickname.isNotBlank()) {
                    Text(
                        text = "\"${player.nickname}\"",
                        fontSize = 12.sp,
                        color = CricketGoldSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "${player.role.label} • ${player.battingStyle}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick Stats summary column
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${player.runs} Runs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CricketGreenPrimary
                )
                if (player.wickets > 0) {
                    Text(
                        text = "${player.wickets} Wkts",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = CricketGoldSecondary
                    )
                }
                Text(
                    text = "${player.matches} Matches",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
