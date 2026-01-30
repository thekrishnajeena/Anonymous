package com.krishnajeena.anonymous.feature_comment

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.krishnajeena.anonymous.domain.user.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
class CommentRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // Real-time comment listener
    fun observeComments(postId: String): Flow<List<Comment>> =
        callbackFlow {
            val listener = firestore.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snap, err ->
                    if (err != null) {
                        close(err)
                        return@addSnapshotListener
                    }

                    val comments = snap?.documents
                        ?.mapNotNull { it.toComment() }
                        ?: emptyList()

                    trySend(comments)
                }

            awaitClose { listener.remove() }
        }


    // Add new comment
    suspend fun addComment(
        postId: String,
        userId: String,
        userTag: String,// author info
        content: String
    ) {
        val commentData = mapOf(
            "authorUid" to userId,
            "authorTag" to userTag,
            "content" to content,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .add(commentData)
            .await()
    }
}
