package com.krishnajeena.anonymous.domain.post

data class Post(
    val id: String,
    val authorUid: String,
    val authorTag: String,
    val content: String,
    val createdAt: Long,
    val isSaved: Boolean = false,
    val likesCount: Long = 0,
    val isLiked: Boolean = false
)
