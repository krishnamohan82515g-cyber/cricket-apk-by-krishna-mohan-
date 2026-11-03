package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CommunityPost
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGoldLight
import com.example.ui.theme.CricketGoldSecondary
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.CricketGreenPrimary
import com.example.ui.viewmodel.CricketViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SocialFeedScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val posts by viewModel.posts.collectAsState()
    val showCreateDialog by viewModel.showCreatePostDialog.collectAsState()
    val selectedPostForComments by viewModel.selectedPostForComments.collectAsState()

    // Create post form state
    var authorNameInput by remember { mutableStateOf("") }
    var authorRoleInput by remember { mutableStateOf("Village Fan") }
    var contentInput by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("Match Day") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Comment input state
    var commentAuthorInput by remember { mutableStateOf("Cricket Fan") }
    var newCommentText by remember { mutableStateOf("") }

    // Community Poll state
    var selectedPollOption by remember { mutableIntStateOf(0) }
    var hasVoted by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
            Toast.makeText(context, "Photo attached to post!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = modifier.testTag("social_feed_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreatePostDialog.value = true },
                containerColor = CricketGreenDark,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_create_post")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Post")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Post", fontWeight = FontWeight.Bold)
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
            // Interactive Fan Poll Card (MVP of the Day)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("poll_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.HowToVote, contentDescription = null, tint = CricketGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "COMMUNITY POLL: Player of the Match?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val pollCandidates = listOf(
                            Pair("Ramesh Patel (Captain's knock 56*)", if (hasVoted) 0.58f else 0.45f),
                            Pair("Suresh Kumar (Deadly yorker spells)", if (hasVoted) 0.28f else 0.35f),
                            Pair("Deepak Yadav (Sharp stumping & 40+ runs)", if (hasVoted) 0.14f else 0.20f)
                        )

                        pollCandidates.forEachIndexed { index, (name, percentage) ->
                            val isChosen = selectedPollOption == index
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedPollOption = index
                                        hasVoted = true
                                    },
                                color = if (isChosen && hasVoted) CricketGreenPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(name, fontSize = 13.sp, fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal)
                                        if (hasVoted) {
                                            Text("${(percentage * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CricketGreenPrimary)
                                        }
                                    }
                                    if (hasVoted) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { percentage },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = CricketGreenPrimary,
                                            trackColor = Color.LightGray.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            }
                        }

                        if (!hasVoted) {
                            Text(
                                text = "Tap any candidate to cast your village fan vote!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            Text(
                                text = "✓ Thanks for voting! 248 village votes recorded.",
                                fontSize = 11.sp,
                                color = CricketGreenDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Posts List
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    onLike = { viewModel.togglePostLike(post) },
                    onOpenComments = { viewModel.selectPostForComments(post) }
                )
            }
        }
    }

    // Create Post Dialog
    if (showCreateDialog) {
        val roleOptions = listOf("Village Fan", "Team Captain", "Club Secretary", "Opening Batsman", "Spectator")
        val tagOptions = listOf("Match Day", "Victory Cheer", "Practice Session", "Announcement", "Fan Appreciation")

        AlertDialog(
            onDismissRequest = { viewModel.showCreatePostDialog.value = false },
            title = { Text("Share on Village Feed 📢", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = authorNameInput,
                                onValueChange = { authorNameInput = it },
                                label = { Text("Your Name") },
                                placeholder = { Text("e.g. Ramesh Patel / Raju") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_post_author"),
                                singleLine = true
                            )

                            Text("Your Role in Club/Village:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                roleOptions.forEach { r ->
                                    FilterChip(
                                        selected = authorRoleInput == r,
                                        onClick = { authorRoleInput = r },
                                        label = { Text(r, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Text("Topic / Tag:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                tagOptions.forEach { t ->
                                    FilterChip(
                                        selected = tagInput == t,
                                        onClick = { tagInput = t },
                                        label = { Text(t, fontSize = 11.sp) }
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = contentInput,
                                onValueChange = { contentInput = it },
                                label = { Text("What's happening in village cricket?") },
                                placeholder = { Text("Celebrate the win, cheer the boys, or give team updates...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_post_content"),
                                minLines = 3
                            )

                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (selectedPhotoUri != null) "✓ Photo Attached" else "Attach Photo from Gallery")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val author = authorNameInput.ifBlank { "Proud Rampur Supporter" }
                        val text = contentInput.ifBlank { "Great cricket match today! Let's go Tigers!" }
                        viewModel.createPost(
                            authorName = author,
                            authorRole = authorRoleInput,
                            content = text,
                            tag = tagInput,
                            mediaUri = selectedPhotoUri?.toString()
                        )
                        authorNameInput = ""
                        contentInput = ""
                        selectedPhotoUri = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark),
                    modifier = Modifier.testTag("btn_confirm_post")
                ) {
                    Text("Post to Feed")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showCreatePostDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Comments Sheet / Dialog
    selectedPostForComments?.let { post ->
        AlertDialog(
            onDismissRequest = { viewModel.selectPostForComments(null) },
            title = {
                Column {
                    Text("Comments & Cheering 💬", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = "On ${post.authorName}'s post: \"${post.content.take(40)}...\"",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        if (post.comments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No comments yet. Be the first to cheer!",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(post.comments) { c ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(c.author, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CricketGreenPrimary)
                                        Text(c.timeAgo, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(c.text, fontSize = 13.sp)
                                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Write a cheer or comment...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_comment"),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (newCommentText.isNotBlank()) {
                                        viewModel.addComment(post, commentAuthorInput, newCommentText)
                                        newCommentText = ""
                                    }
                                },
                                modifier = Modifier.testTag("btn_send_comment")
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = CricketGreenPrimary)
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.selectPostForComments(null) }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun PostCard(
    post: CommunityPost,
    onLike: () -> Unit,
    onOpenComments: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Avatar, Name, Role, Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CricketGreenDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.take(1).uppercase(),
                        color = CricketGoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = post.authorRole,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CricketGoldLight.copy(alpha = 0.35f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = post.tag,
                        color = CricketGoldSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Text
            Text(
                text = post.content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                style = MaterialTheme.typography.bodyMedium
            )

            // Attached Photo (if any)
            if (!post.mediaUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = post.mediaUri,
                    contentDescription = "Post attachment",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))

            // Engagement Row: Claps/Likes & Comments
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLike() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("btn_like_post_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) CricketBallRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likesCount} Claps",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenComments() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("btn_comments_post_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.comments.size} Comments",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
