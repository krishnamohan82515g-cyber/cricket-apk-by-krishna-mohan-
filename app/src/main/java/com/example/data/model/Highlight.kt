package com.example.data.model

enum class MediaType {
    VIDEO,
    PHOTO
}

data class MatchHighlight(
    val id: String,
    val title: String,
    val description: String,
    val mediaUri: String? = null,
    val mediaType: MediaType = MediaType.VIDEO,
    val duration: String = "0:30",
    val viewsCount: Int = 120,
    val likesCount: Int = 18,
    val isLiked: Boolean = false,
    val matchTag: String = "GPL 2026",
    val playerTag: String = "Ramesh Patel",
    val timestamp: Long = System.currentTimeMillis()
)
