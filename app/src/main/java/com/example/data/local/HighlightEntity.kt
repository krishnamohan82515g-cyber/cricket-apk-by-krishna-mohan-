package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.MatchHighlight
import com.example.data.model.MediaType

@Entity(tableName = "highlights")
data class HighlightEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val mediaUri: String?,
    val mediaType: String,
    val duration: String,
    val viewsCount: Int,
    val likesCount: Int,
    val isLiked: Boolean,
    val matchTag: String,
    val playerTag: String,
    val timestamp: Long
) {
    fun toDomain(): MatchHighlight {
        val type = try {
            MediaType.valueOf(mediaType)
        } catch (e: Exception) {
            MediaType.VIDEO
        }
        return MatchHighlight(
            id = id,
            title = title,
            description = description,
            mediaUri = mediaUri,
            mediaType = type,
            duration = duration,
            viewsCount = viewsCount,
            likesCount = likesCount,
            isLiked = isLiked,
            matchTag = matchTag,
            playerTag = playerTag,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDomain(highlight: MatchHighlight): HighlightEntity {
            return HighlightEntity(
                id = highlight.id,
                title = highlight.title,
                description = highlight.description,
                mediaUri = highlight.mediaUri,
                mediaType = highlight.mediaType.name,
                duration = highlight.duration,
                viewsCount = highlight.viewsCount,
                likesCount = highlight.likesCount,
                isLiked = highlight.isLiked,
                matchTag = highlight.matchTag,
                playerTag = highlight.playerTag,
                timestamp = highlight.timestamp
            )
        }
    }
}
