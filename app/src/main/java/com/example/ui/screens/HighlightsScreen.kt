package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MatchHighlight
import com.example.data.model.MediaType
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGoldLight
import com.example.ui.theme.CricketGoldSecondary
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.CricketGreenPrimary
import com.example.ui.viewmodel.CricketViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HighlightsScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val highlights by viewModel.highlights.collectAsState()
    val showUploadDialog by viewModel.showUploadHighlightDialog.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Videos 🎥", "Photos 📸", "Sixes & Fours 🏏", "Wickets ⚡")

    var playingHighlightId by remember { mutableStateOf<String?>(null) }

    // Upload Form State
    var uploadTitle by remember { mutableStateOf("") }
    var uploadDesc by remember { mutableStateOf("") }
    var uploadMatchTag by remember { mutableStateOf("GPL 2026 Final") }
    var uploadPlayerTag by remember { mutableStateOf("Ramesh Patel") }
    var uploadMediaType by remember { mutableStateOf(MediaType.VIDEO) }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }

    // Zero-permission Photo/Video Picker
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedMediaUri = uri
            Toast.makeText(context, "Media file attached!", Toast.LENGTH_SHORT).show()
        }
    }

    val filteredHighlights = highlights.filter { h ->
        when (selectedFilter) {
            "Videos 🎥" -> h.mediaType == MediaType.VIDEO
            "Photos 📸" -> h.mediaType == MediaType.PHOTO
            "Sixes & Fours 🏏" -> h.title.contains("six", ignoreCase = true) || h.title.contains("boundary", ignoreCase = true)
            "Wickets ⚡" -> h.title.contains("wicket", ignoreCase = true) || h.title.contains("yorker", ignoreCase = true) || h.title.contains("catch", ignoreCase = true)
            else -> true
        }
    }

    Scaffold(
        modifier = modifier.testTag("highlights_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showUploadHighlightDialog.value = true },
                containerColor = CricketGoldSecondary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_upload_highlight")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Videocam, contentDescription = "Upload Highlight")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload Clip", fontWeight = FontWeight.Bold)
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
            // Category Filters
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            modifier = Modifier.testTag("filter_${filter.take(4).lowercase()}")
                        )
                    }
                }
            }

            // Highlights Video Cards
            items(filteredHighlights, key = { it.id }) { item ->
                val isPlaying = playingHighlightId == item.id
                HighlightPlayerCard(
                    highlight = item,
                    isPlaying = isPlaying,
                    onTogglePlay = {
                        playingHighlightId = if (isPlaying) null else item.id
                    },
                    onToggleLike = {
                        viewModel.toggleHighlightLike(item)
                    },
                    onShare = {
                        Toast.makeText(
                            context,
                            "Sharing: ${item.title} (Village Tigers Highlights)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }

    // Upload Highlight Dialog
    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showUploadHighlightDialog.value = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = CricketGoldSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Match Highlight 🎥", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Pick file button
                            OutlinedButton(
                                onClick = {
                                    mediaPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_select_media_picker")
                            ) {
                                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (selectedMediaUri != null) "✓ Media Attached" else "Pick Video / Photo from Phone"
                                )
                            }

                            if (selectedMediaUri != null) {
                                Text(
                                    text = "Selected file: ${selectedMediaUri?.lastPathSegment?.take(25) ?: "Media selected"}",
                                    fontSize = 11.sp,
                                    color = CricketGreenPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = uploadMediaType == MediaType.VIDEO,
                                    onClick = { uploadMediaType = MediaType.VIDEO },
                                    label = { Text("Video Clip") }
                                )
                                FilterChip(
                                    selected = uploadMediaType == MediaType.PHOTO,
                                    onClick = { uploadMediaType = MediaType.PHOTO },
                                    label = { Text("Match Photo") }
                                )
                            }

                            OutlinedTextField(
                                value = uploadTitle,
                                onValueChange = { uploadTitle = it },
                                label = { Text("Highlight Title") },
                                placeholder = { Text("e.g. Ramesh Patel's helicopter six into pond") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_highlight_title"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = uploadDesc,
                                onValueChange = { uploadDesc = it },
                                label = { Text("Description & Village Moment") },
                                placeholder = { Text("Crowd went wild on the banyan tree boundary!") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )

                            OutlinedTextField(
                                value = uploadMatchTag,
                                onValueChange = { uploadMatchTag = it },
                                label = { Text("Match / Tournament Tag") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = uploadPlayerTag,
                                onValueChange = { uploadPlayerTag = it },
                                label = { Text("Player Tagged") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = uploadTitle.ifBlank { "Unbelievable Village Cricket Moment!" }
                        val desc = uploadDesc.ifBlank { "Huge cheer from the village crowd!" }
                        viewModel.uploadHighlight(
                            title = title,
                            description = desc,
                            mediaUri = selectedMediaUri?.toString(),
                            mediaType = uploadMediaType,
                            matchTag = uploadMatchTag,
                            playerTag = uploadPlayerTag
                        )
                        uploadTitle = ""
                        uploadDesc = ""
                        selectedMediaUri = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark),
                    modifier = Modifier.testTag("btn_confirm_upload")
                ) {
                    Text("Publish to Feed")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showUploadHighlightDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HighlightPlayerCard(
    highlight: MatchHighlight,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onToggleLike: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0f) }

    // Simulated playback progress
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                delay(100)
                progress = (progress + 0.02f) % 1f
            }
        } else {
            progress = 0f
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("highlight_card_${highlight.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Video / Thumbnail Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(CricketGreenDark, Color(0xFF071B0F))
                        )
                    )
                    .clickable { onTogglePlay() },
                contentAlignment = Alignment.Center
            ) {
                // If user selected custom image/video uri, show via Coil AsyncImage
                if (!highlight.mediaUri.isNullOrBlank()) {
                    AsyncImage(
                        model = highlight.mediaUri,
                        contentDescription = highlight.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Decorative Cricket Stadium Field Graphic overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                            )
                        )
                )

                // Central Play / Pause Button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) CricketGoldSecondary else Color.White.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = if (isPlaying) Color.White else CricketGreenDark,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Duration badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isPlaying) "PLAYING" else highlight.duration,
                        color = if (isPlaying) CricketGoldLight else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Match tag badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CricketGreenPrimary.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🏆 ${highlight.matchTag}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Playback progress bar
            if (isPlaying) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = CricketGoldSecondary,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
            }

            // Content details
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = highlight.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = highlight.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tagged: ${highlight.playerTag} • ${highlight.viewsCount} views",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Cheer / Like Button
                        IconButton(onClick = onToggleLike) {
                            Icon(
                                imageVector = if (highlight.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (highlight.isLiked) CricketBallRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${highlight.likesCount}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Share Button
                        IconButton(onClick = onShare) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
