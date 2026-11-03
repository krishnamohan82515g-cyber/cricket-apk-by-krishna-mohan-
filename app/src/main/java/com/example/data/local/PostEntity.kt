package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CommunityPost
import com.example.data.model.PostComment

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorRole: String,
    val content: String,
    val tag: String,
    val mediaUri: String?,
    val likesCount: Int,
    val isLiked: Boolean,
    val timestamp: Long,
    val serializedComments: String = ""
) {
    fun toDomain(): CommunityPost {
        val commentsList = if (serializedComments.isBlank()) {
            emptyList()
        } else {
            serializedComments.split("|||").mapNotNull { entry ->
                val parts = entry.split(":::")
                if (parts.size >= 4) {
                    PostComment(id = parts[0], author = parts[1], text = parts[2], timeAgo = parts[3])
                } else null
            }
        }

        return CommunityPost(
            id = id,
            authorName = authorName,
            authorRole = authorRole,
            content = content,
            tag = tag,
            mediaUri = mediaUri,
            likesCount = likesCount,
            isLiked = isLiked,
            timestamp = timestamp,
            comments = commentsList
        )
    }

    companion object {
        fun fromDomain(post: CommunityPost): PostEntity {
            val serialized = post.comments.joinToString("|||") {
                "${it.id}:::${it.author}:::${it.text}:::${it.timeAgo}"
            }
            return PostEntity(
                id = post.id,
                authorName = post.authorName,
                authorRole = post.authorRole,
                content = post.content,
                tag = post.tag,
                mediaUri = post.mediaUri,
                likesCount = post.likesCount,
                isLiked = post.isLiked,
                timestamp = post.timestamp,
                serializedComments = serialized
            )
        }
    }
}
