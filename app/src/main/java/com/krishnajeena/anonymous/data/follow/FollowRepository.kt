package com.krishnajeena.anonymous.data.follow

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FollowRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun follow(currentUid: String, targetUid: String) {
        val batch = firestore.batch()

        val now = System.currentTimeMillis()

        val followingRef = firestore
            .collection("users")
            .document(currentUid)
            .collection("following")
            .document(targetUid)

        val followersRef = firestore
            .collection("users")
            .document(targetUid)
            .collection("followers")
            .document(currentUid)

        val currentUserRef = firestore
            .collection("users")
            .document(currentUid)

        val targetUserRef = firestore
            .collection("users")
            .document(targetUid)

        batch.set(followingRef, mapOf("createdAt" to now))
        batch.set(followersRef, mapOf("createdAt" to now))

        batch.update(currentUserRef, "followingCount", FieldValue.increment(1))
        batch.update(targetUserRef, "followersCount", FieldValue.increment(1))

        batch.commit().await()
    }

    suspend fun unfollow(currentUid: String, targetUid: String) {
        val batch = firestore.batch()

        val followingRef = firestore
            .collection("users")
            .document(currentUid)
            .collection("following")
            .document(targetUid)

        val followersRef = firestore
            .collection("users")
            .document(targetUid)
            .collection("followers")
            .document(currentUid)

        val currentUserRef = firestore
            .collection("users")
            .document(currentUid)

        val targetUserRef = firestore
            .collection("users")
            .document(targetUid)

        batch.delete(followingRef)
        batch.delete(followersRef)

        batch.update(currentUserRef, "followingCount", FieldValue.increment(-1))
        batch.update(targetUserRef, "followersCount", FieldValue.increment(-1))

        batch.commit().await()
    }

    suspend fun isFollowing(
        currentUid: String,
        targetUid: String
    ): Boolean {
        return firestore
            .collection("users")
            .document(currentUid)
            .collection("following")
            .document(targetUid)
            .get()
            .await()
            .exists()
    }

}
