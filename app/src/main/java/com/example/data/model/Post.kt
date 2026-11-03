package com.example.data.model

data class PostComment(
    val id: String,
    val author: String,
    val text: String,
    val timeAgo: String
)

data class CommunityPost(
    val id: String,
    val authorName: String,
    val authorRole: String = "Village Fan",
    val content: String,
    val tag: String = "Match Update",
    val mediaUri: String? = null,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val comments: List<PostComment> = emptyList()
)
