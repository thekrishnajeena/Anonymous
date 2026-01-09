package com.krishnajeena.anonymous.data.post

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.krishnajeena.anonymous.domain.post.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestorePostRepository(
    private val firestore: FirebaseFirestore
) {

    fun observePosts(): Flow<List<Post>> = callbackFlow {

        val listener = firestore.collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    Post(
                        id = doc.id,
                        authorUid = doc.getString("authorUid") ?: return@mapNotNull null,
                        authorTag = doc.getString("authorTag") ?: "unknown",
                        content = doc.getString("content") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0L
                    )
                } ?: emptyList()

                trySend(posts)
            }

        awaitClose { listener.remove() }
    }

    suspend fun fetchFeedPage(
        lastPost: DocumentSnapshot?,
        pageSize: Long = 20
    ): Pair<List<Post>, DocumentSnapshot?> {

        var query = firestore.collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(pageSize)

        if (lastPost != null) {
            query = query.startAfter(lastPost)
        }

        val snapshot = query.get().await()
        val posts = snapshot.documents.mapNotNull { it.toPost() }
        val newLast = snapshot.documents.lastOrNull()

        return posts to newLast
    }

    fun observeUserPosts(uid: String): Flow<List<Post>> =
        firestore.collection("posts")
            .whereEqualTo("authorUid", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshotsToFlow()

    suspend fun savePost(uid: String, post: Post) {
        firestore.collection("users")
            .document(uid)
            .collection("saved")
            .document(post.id)
            .set(
                mapOf(
                    "postId" to post.id,
                    "savedAt" to System.currentTimeMillis()
                )
            )
            .await()
    }

    // 🔹 Unsave post
    suspend fun unsavePost(uid: String, postId: String) {
        firestore.collection("users")
            .document(uid)
            .collection("saved")
            .document(postId)
            .delete()
            .await()
    }

    // 🔹 Observe saved posts (Profile tab)
    fun observeSavedPosts(uid: String): Flow<List<Post>> = callbackFlow {
        val savedRef = firestore.collection("users")
            .document(uid)
            .collection("saved")
            .orderBy("savedAt", Query.Direction.DESCENDING)

        val listener = savedRef.addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }

            val postIds = snap!!.documents.mapNotNull { it.getString("postId") }

            if (postIds.isEmpty()) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            firestore.collection("posts")
                .whereIn(FieldPath.documentId(), postIds)
                .get()
                .addOnSuccessListener { postsSnap ->
                    val posts = postsSnap.documents.mapNotNull { it.toPost() }
                    trySend(posts)
                }
        }

        awaitClose { listener.remove() }
    }

}

fun Query.snapshotsToFlow(): Flow<List<Post>> = callbackFlow {
    val listener = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close(error)
            return@addSnapshotListener
        }

        val posts = snapshot?.documents?.mapNotNull { doc ->
            doc.toPost()
        } ?: emptyList()

        trySend(posts)
    }

    awaitClose { listener.remove() }
}

fun DocumentSnapshot.toPost(): Post? {
    return Post(
        id = id,
        authorUid = getString("authorUid") ?: return null,
        authorTag = getString("authorTag") ?: "",
        content = getString("content") ?: "",
        createdAt = getLong("createdAt") ?: 0L
    )
}

