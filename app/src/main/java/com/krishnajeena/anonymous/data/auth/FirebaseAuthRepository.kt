package com.krishnajeena.anonymous.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.krishnajeena.anonymous.domain.AuthState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth
) {

    fun authState(): Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if(user != null){
                trySend(AuthState.Authenticated(user.uid))
            } else {
                trySend(AuthState.Unauthenticated)
            }
        }

        auth.addAuthStateListener(listener)

        awaitClose{
            auth.removeAuthStateListener(listener)
        }
    }

    suspend fun signInWithGoogle(idToken: String): String {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return result.user?.uid ?: throw IllegalStateException("User null")
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun logout() {
        auth.signOut()
    }
}
