package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Trophy
import com.example.data.model.UserRole
import com.example.ui.theme.CricketGoldLight
import com.example.ui.theme.CricketGoldSecondary
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.CricketGreenPrimary
import com.example.ui.components.UserNameDisplay
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CricketViewModel
import java.util.Locale

@Composable
fun TeamProfileScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.teamProfile.collectAsState()
    val players by viewModel.players.collectAsState()
    val highlights by viewModel.highlights.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("team_profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Logged-in User Profile & Role Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_account_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentUser != null) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (currentUser?.role) {
                                            UserRole.ADMIN -> CricketGoldSecondary
                                            UserRole.PLAYER -> CricketGreenDark
                                            UserRole.FAN -> com.example.ui.theme.CricketBallRed
                                            null -> CricketGreenDark
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser?.name?.take(1)?.uppercase() ?: "U",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    UserNameDisplay(
                                        fullName = currentUser?.name ?: "",
                                        isRowLayout = true,
                                        primaryFontSize = 14.sp,
                                        secondaryFontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CricketGoldLight.copy(alpha = 0.4f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = currentUser?.role?.badge ?: "",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "@${currentUser?.usernameOrEmail} • ${currentUser?.role?.label}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { viewModel.showProfileDialog.value = true },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_manage_my_profile")
                            ) {
                                Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.showAuthDialog.value = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_switch_account")
                            ) {
                                Text("Switch", fontSize = 11.sp)
                            }
                        }
                    } else {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Not Logged In", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Sign in or register to manage your profile & role", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { viewModel.showAuthDialog.value = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Log In / Sign Up", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Club Crest Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("club_hero_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Emblem
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(CricketGoldLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsCricket,
                                contentDescription = "Team Crest",
                                tint = CricketGreenDark,
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 22.sp
                            )
                        )

                        Text(
                            text = "\"${profile.motto}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = CricketGoldLight,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = profile.homeGround,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "Est. ${profile.establishedYear} • Affiliated to District Cricket Board",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Fan Club Membership Card (Interactive)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fan_membership_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (profile.isUserFanMember) CricketGoldLight.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = if (profile.isUserFanMember) Icons.Default.CheckCircle else Icons.Default.CardMembership,
                            contentDescription = null,
                            tint = if (profile.isUserFanMember) CricketGoldSecondary else CricketGreenPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (profile.isUserFanMember) "12th Man Village Member 🎖️" else "Join Village 12th Man Army",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${profile.fanCount} Village Supporters Enrolled",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.toggleFanMembership()
                            val msg = if (!profile.isUserFanMember) "Welcome to Rampur Tigers Fan Club!" else "Fan membership updated"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (profile.isUserFanMember) CricketGoldSecondary else CricketGreenDark
                        ),
                        modifier = Modifier.testTag("btn_toggle_membership")
                    ) {
                        Text(if (profile.isUserFanMember) "Joined ✓" else "Join Fan Club", fontSize = 12.sp)
                    }
                }
            }
        }

        // Team Win-Loss Record
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ALL-TIME CLUB RECORD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        RecordItem("Matches", "${profile.totalMatches}")
                        RecordItem("Wins", "${profile.wins}", color = CricketGreenPrimary)
                        RecordItem("Losses", "${profile.losses}", color = MaterialTheme.colorScheme.error)
                        RecordItem("Win Rate", "${String.format(Locale.US, "%.1f", profile.winPercentage)}%", color = CricketGoldSecondary)
                        RecordItem("Trophies", "${profile.trophiesWon}", color = CricketGoldSecondary)
                    }
                }
            }
        }

        // Trophy Cabinet
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = CricketGoldSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TROPHY CABINET (${profile.trophies.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(profile.trophies) { trophy ->
                        TrophyCard(trophy)
                    }
                }
            }
        }

        // Team Highlights & Match Clips on Profile
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, tint = CricketGreenPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TEAM HIGHLIGHTS & CLIPS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketGreenPrimary,
                        modifier = Modifier.clickable {
                            viewModel.navigateTo(AppScreen.HIGHLIGHTS)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                highlights.take(3).forEach { h ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.navigateTo(AppScreen.HIGHLIGHTS)
                            },
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CricketGreenDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.SportsCricket, contentDescription = null, tint = CricketGoldLight, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(h.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text("${h.matchTag} • ${h.viewsCount} views • ${h.likesCount} cheers", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Club Officials
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("officials_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CLUB LEADERSHIP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LeadershipRow("Club President & Patron", "Krishna Mohan Shukla ( Rishabh shukla )", "Chief club patron & founder")
                    LeadershipRow("Captain", profile.captain, "Leading from the front")
                    LeadershipRow("Vice-Captain", profile.viceCaptain, "Bowling spearhead")
                    LeadershipRow("Coach & Mentor", profile.coach, "Senior village trainer")
                }
            }
        }
    }
}

@Composable
fun RecordItem(label: String, value: String, color: Color = Color.Unspecified) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LeadershipRow(role: String, name: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$role: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        UserNameDisplay(fullName = name, isRowLayout = true, primaryFontSize = 13.sp, secondaryFontSize = 10.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun TrophyCard(trophy: Trophy) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .testTag("trophy_card_${trophy.year}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CricketGoldLight.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = CricketGoldSecondary)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CricketGoldSecondary)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(trophy.year, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(trophy.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
            Text(trophy.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)

            Spacer(modifier = Modifier.height(4.dp))
            Text("Margin: ${trophy.opponentBeaten}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CricketGreenDark)
        }
    }
}
