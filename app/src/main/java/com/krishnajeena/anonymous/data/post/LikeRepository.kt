package com.krishnajeena.anonymous.data.post

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LikeRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun likesCollection(postId: String) =
        firestore.collection("posts")
            .document(postId)
            .collection("likes")

    suspend fun isLiked(postId: String, uid: String): Boolean {
        return likesCollection(postId)
            .document(uid)
            .get()
            .await()
            .exists()
    }

    suspend fun like(postId: String, uid: String) {
        val postRef = firestore.collection("posts").document(postId)
        val likeRef = likesCollection(postId).document(uid)

        firestore.runBatch { batch ->
            batch.set(likeRef, mapOf("createdAt" to System.currentTimeMillis()))
            batch.update(postRef, "likesCount", FieldValue.increment(1))
        }.await()
    }

    suspend fun unlike(postId: String, uid: String) {
        val postRef = firestore.collection("posts").document(postId)
        val likeRef = likesCollection(postId).document(uid)

        firestore.runBatch { batch ->
            batch.delete(likeRef)
            batch.update(postRef, "likesCount", FieldValue.increment(-1))
        }.await()
    }
}

