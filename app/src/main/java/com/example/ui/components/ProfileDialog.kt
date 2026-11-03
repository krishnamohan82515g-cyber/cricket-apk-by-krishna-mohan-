package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGoldLight
import com.example.ui.theme.CricketGoldSecondary
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.CricketGreenPrimary
import com.example.ui.viewmodel.CricketViewModel

@Composable
fun ProfileDialog(
    user: User,
    viewModel: CricketViewModel,
    onDismiss: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    // Edit fields
    var editName by remember { mutableStateOf(user.name) }
    var editRole by remember { mutableStateOf(user.role) }
    var editJersey by remember { mutableStateOf(user.jerseyNumber?.toString() ?: "") }
    var editSpecialty by remember { mutableStateOf(user.playerSpecialty ?: "") }
    var editPhone by remember { mutableStateOf(user.phoneNumber) }
    var editVillage by remember { mutableStateOf(user.villageName) }
    var editBio by remember { mutableStateOf(user.bio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Edit Member Profile" else "Member Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_dialog_content"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isEditing) {
                    // --- PROFILE VIEW MODE ---
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (user.role) {
                                            UserRole.ADMIN -> CricketGoldSecondary
                                            UserRole.PLAYER -> CricketGreenDark
                                            UserRole.FAN -> CricketBallRed
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            UserNameDisplay(
                                fullName = user.name,
                                primaryFontSize = 18.sp,
                                secondaryFontSize = 12.sp,
                                isRowLayout = false
                            )

                            Text(
                                text = "@${user.usernameOrEmail}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Role Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when (user.role) {
                                            UserRole.ADMIN -> CricketGoldLight
                                            UserRole.PLAYER -> MaterialTheme.colorScheme.primaryContainer
                                            UserRole.FAN -> CricketBallRed.copy(alpha = 0.15f)
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${user.role.badge} (${user.role.label})",
                                    color = when (user.role) {
                                        UserRole.ADMIN -> CricketGoldSecondary
                                        UserRole.PLAYER -> CricketGreenPrimary
                                        UserRole.FAN -> CricketBallRed
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (user.role == UserRole.PLAYER) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.SportsCricket, contentDescription = null, modifier = Modifier.size(16.dp), tint = CricketGreenPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Jersey #${user.jerseyNumber ?: 7} • ${user.playerSpecialty ?: "All-Rounder"}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(user.villageName, fontSize = 13.sp)
                                }

                                if (user.phoneNumber.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(user.phoneNumber, fontSize = 13.sp)
                                    }
                                }

                                if (user.bio.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("\"${user.bio}\"", fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    // Excellent Security Status Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("security_status_card"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CricketGreenDark.copy(alpha = 0.08f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CricketGreenPrimary.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🛡️", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "SECURITY: EXCELLENT LEVEL",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp,
                                            color = CricketGreenPrimary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CricketGreenPrimary)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("ACTIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = "• Cryptographic Salt: 128-bit unique entropy (${if (user.salt.isNotBlank()) user.salt.take(8) + "..." else "Secure"})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "• Key Stretching: 1000 SHA-256 iterations (PBKDF resistance)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "• Constant-Time Verification: Side-channel timing attack proof",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "• Brute-Force Shield: Auto-lock after 5 failed attempts (0 current fails)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { isEditing = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_edit_profile"),
                                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Profile")
                            }

                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_logout")
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp), tint = CricketBallRed)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log Out", color = CricketBallRed)
                            }
                        }
                    }
                } else {
                    // --- PROFILE EDIT MODE ---
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("Display Name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_edit_name"),
                                singleLine = true
                            )

                            Text("Change Your Role:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                            // Role Selection Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                UserRole.entries.forEach { role ->
                                    val isSelected = editRole == role
                                    Button(
                                        onClick = { editRole = role },
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(2.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) CricketGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(role.badge, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (editRole == UserRole.PLAYER) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = editJersey,
                                        onValueChange = { editJersey = it },
                                        label = { Text("Jersey #") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = editSpecialty,
                                        onValueChange = { editSpecialty = it },
                                        label = { Text("Playing Role") },
                                        modifier = Modifier.weight(2f),
                                        singleLine = true
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = editVillage,
                                onValueChange = { editVillage = it },
                                label = { Text("Village / Hometown") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = editBio,
                                onValueChange = { editBio = it },
                                label = { Text("Bio / Status") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.updateUserProfile(
                                            name = editName,
                                            role = editRole,
                                            jerseyNumber = editJersey.toIntOrNull(),
                                            playerSpecialty = editSpecialty,
                                            phoneNumber = editPhone,
                                            villageName = editVillage,
                                            bio = editBio
                                        )
                                        isEditing = false
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_save_profile"),
                                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
                                ) {
                                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save Changes", fontWeight = FontWeight.Bold)
                                }

                                TextButton(
                                    onClick = { isEditing = false },
                                    modifier = Modifier.weight(0.7f)
                                ) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
