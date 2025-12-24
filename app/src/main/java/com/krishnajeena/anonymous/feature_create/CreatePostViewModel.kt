package com.krishnajeena.anonymous.feature_create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.krishnajeena.anonymous.data.FirestoreUserRepository
import com.krishnajeena.anonymous.data.auth.FirebaseAuthRepository
import com.krishnajeena.anonymous.data.post.FirestorePostRepository
import com.krishnajeena.anonymous.featureProfile.ProfileViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val userRepository: FirestoreUserRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var text by mutableStateOf("")
        private set

    fun onTextChange(newText: String) {
        if (newText.length <= 500) {
            text = newText
        }
    }

    fun submitPost(onSuccess: () -> Unit) {
        viewModelScope.launch {

            val firebaseUser = authRepository.currentUser() ?: return@launch

            // Load user's anonymous tag from Firestore
            val userDoc = userRepository.getUser(firebaseUser.uid)

            val post = mapOf(
                "authorUid" to userDoc.uid,
                "authorTag" to userDoc.tag,
                "content" to text,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("posts")
                .add(post)
                .await()

            onSuccess()
        }
    }

}
