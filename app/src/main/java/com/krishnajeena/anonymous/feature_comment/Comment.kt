package com.krishnajeena.anonymous.feature_comment

import com.google.firebase.firestore.DocumentSnapshot

data class Comment(
    val commentId: String = "",
    val postId: String = "",
    val authorUid: String = "",
    val authorTag: String = "",
    val content: String = "",
    val createdAt: Long = 0L,
    val likesCount: Int = 0
)


fun DocumentSnapshot.toComment(): Comment? {
    return try {
        Comment(
            commentId = id,
            postId = getString("postId") ?: "",
            authorUid = getString("authorUid") ?: "",
            authorTag = getString("authorTag") ?: "",
            content = getString("content") ?: "",
            createdAt = getLong("createdAt") ?: 0L,
            likesCount = (getLong("likesCount") ?: 0L).toInt()
        )
    } catch (e: Exception) {
        null
    }
}
