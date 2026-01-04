package com.krishnajeena.anonymous.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.krishnajeena.anonymous.domain.user.User
import kotlinx.coroutines.tasks.await

class FirestoreUserRepository(
    private val firestore: FirebaseFirestore
) {

    suspend fun createUserIfNotExists(
        uid: String,
        email: String?,
        photoUrl: String?,
        displayName: String
    ){

        val ref = firestore.collection("users").document(uid)

        val snapshot = ref.get().await()
        if(snapshot.exists()) return

        val username = UsernameGenerator.generate(displayName, uid)

        val user = mapOf(
            "uid" to uid,
            "displayName" to displayName,
            "tag" to username,
            "email" to email,
            "photoUrl" to photoUrl,
            "provider" to "google",
            "createdAt" to System.currentTimeMillis(),
            "lastLoginAt" to System.currentTimeMillis()
        )

        ref.set(user).await()

    }

    suspend fun getUser(uid: String): User{
        val snapshot = firestore
            .collection("users")
            .document(uid)
            .get()
            .await()

        return User(
            uid = uid,
            displayName = snapshot.getString("displayName") ?: "",
            tag = snapshot.getString("tag") ?: "",
            photoUrl = snapshot.getString("photoUrl")
        )
    }

    suspend fun searchUsersByTag(query: String): List<User> {
        if(query.isBlank()) return emptyList()

        val snapshot = firestore.collection("users")
            .orderBy("tag")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .limit(20)
            .get()
            .await()

        return snapshot.documents.mapNotNull{ doc ->
            User(
                uid = doc.id,
                tag = doc.getString("tag") ?: return@mapNotNull null,
                displayName = doc.getString("displayName") ?: "",
                photoUrl = doc.getString("photoUrl")
            )
        }
    }

    suspend fun getCurrentUser(): User? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        return getUser(uid)
    }

}