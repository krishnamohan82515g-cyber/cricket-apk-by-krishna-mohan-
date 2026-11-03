package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGoldLight
import com.example.ui.theme.CricketGoldSecondary
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.CricketGreenPrimary
import com.example.ui.viewmodel.CricketViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthDialog(
    viewModel: CricketViewModel,
    onDismiss: () -> Unit
) {
    val authError by viewModel.authErrorMessage.collectAsState()
    val isLoading by viewModel.isAuthLoading.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Sign Up

    // Login fields
    var loginIdentifier by remember { mutableStateOf("krishna_admin") }
    var loginPassword by remember { mutableStateOf("password123") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Sign Up fields
    var signName by remember { mutableStateOf("") }
    var signUsername by remember { mutableStateOf("") }
    var signPassword by remember { mutableStateOf("") }
    var signRole by remember { mutableStateOf(UserRole.PLAYER) }
    var signJersey by remember { mutableStateOf("11") }
    var signSpecialty by remember { mutableStateOf("All-Rounder") }
    var signPhone by remember { mutableStateOf("+91 ") }
    var signVillage by remember { mutableStateOf("Rampur Village") }
    var signBio by remember { mutableStateOf("Cricket enthusiast from the village.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CricketGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsCricket,
                                contentDescription = null,
                                tint = CricketGoldLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedTab == 0) "Log In to Club" else "Join Village Club",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Log In", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Sign Up", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_dialog_content"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Error message banner
                authError?.let { err ->
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            color = CricketBallRed.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "⚠️ $err",
                                color = CricketBallRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                if (selectedTab == 0) {
                    // --- LOG IN TAB ---
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = loginIdentifier,
                                onValueChange = { loginIdentifier = it },
                                label = { Text("Username or Email") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_login_username"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password"
                                        )
                                    }
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_login_password"),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    viewModel.login(loginIdentifier, loginPassword)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_action_login"),
                                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text("Log In", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider()

                            // Quick Demo Logins for instant emulator testing
                            Text(
                                text = "⚡ ONE-TAP ROLE SWITCH (DEMO ACCOUNTS):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.quickLoginRole(UserRole.ADMIN) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_quick_admin"),
                                    contentPadding = PaddingValues(4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CricketGoldSecondary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("🛡️ Krishna (Admin)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.quickLoginRole(UserRole.PLAYER) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_quick_player"),
                                    contentPadding = PaddingValues(4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("🏏 Player", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.quickLoginRole(UserRole.FAN) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_quick_fan"),
                                    contentPadding = PaddingValues(4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CricketBallRed),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("📣 Fan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🛡️ Excellent Security: 1000-Round Salted SHA-256 Active",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // --- SIGN UP TAB ---
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = signName,
                                onValueChange = { signName = it },
                                label = { Text("Full Name") },
                                placeholder = { Text("e.g. Vikas Patel") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_signup_name"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = signUsername,
                                onValueChange = { signUsername = it },
                                label = { Text("Username or Email") },
                                placeholder = { Text("e.g. vikas_cricket or vikas@gmail.com") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_signup_username"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = signPassword,
                                onValueChange = { signPassword = it },
                                label = { Text("Create Password (min 4 chars)") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_signup_password"),
                                singleLine = true
                            )

                            Text(
                                text = "CHOOSE YOUR ROLE IN THE CLUB:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            // Role Selection Cards
                            UserRole.entries.forEach { role ->
                                val isSelected = signRole == role
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) CricketGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { signRole = role }
                                        .testTag("role_option_${role.name.lowercase()}"),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.surface
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${role.badge} - ${role.label}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = role.description,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = CricketGreenPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Role-specific fields if Player
                            if (signRole == UserRole.PLAYER) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = signJersey,
                                        onValueChange = { signJersey = it },
                                        label = { Text("Jersey #") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = signSpecialty,
                                        onValueChange = { signSpecialty = it },
                                        label = { Text("Specialty") },
                                        placeholder = { Text("Batsman / Bowler") },
                                        modifier = Modifier.weight(2f),
                                        singleLine = true
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = signVillage,
                                onValueChange = { signVillage = it },
                                label = { Text("Village / Town") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = signPhone,
                                onValueChange = { signPhone = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = signBio,
                                onValueChange = { signBio = it },
                                label = { Text("Bio / Playing Note") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )

                            Button(
                                onClick = {
                                    viewModel.signUp(
                                        name = signName,
                                        usernameOrEmail = signUsername,
                                        passwordRaw = signPassword,
                                        role = signRole,
                                        jerseyNumber = signJersey.toIntOrNull(),
                                        playerSpecialty = signSpecialty,
                                        phoneNumber = signPhone,
                                        villageName = signVillage,
                                        bio = signBio
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_action_signup"),
                                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text("Create Account & Log In", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
