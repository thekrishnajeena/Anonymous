package com.krishnajeena.anonymous.data

import com.google.firebase.firestore.FirebaseFirestore
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
}