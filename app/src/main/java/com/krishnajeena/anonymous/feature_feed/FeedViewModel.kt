package com.krishnajeena.anonymous.feature_feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.krishnajeena.anonymous.data.auth.FirebaseAuthRepository
import com.krishnajeena.anonymous.data.post.FirestorePostRepository
import com.krishnajeena.anonymous.data.post.LikeRepository
import com.krishnajeena.anonymous.domain.post.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postRepository: FirestorePostRepository,
    private val authRepository: FirebaseAuthRepository,
    private val likeRepository: LikeRepository
) : ViewModel() {

    init {
        Log.e("VM-PROOF", "FeedViewModel instance = ${this.hashCode()}")
    }

    private val uid: String
        get() = authRepository.currentUser()?.uid ?: ""

    /* ---------------- UI STATE ---------------- */
    sealed class FeedUiState {
        object Loading : FeedUiState()
        data class Success(val posts: List<Post>) : FeedUiState()
        data class Error(val message: String) : FeedUiState()
    }

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    /* ---------------- PAGINATION ---------------- */
    private var lastPost: DocumentSnapshot? = null
    private var isLoading = false

    /* ---------------- INITIAL LOAD ---------------- */

    fun loadInitialFeed() {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                val (newPosts, last) = postRepository.fetchFeedPage(null)
                lastPost = last

                val hydrated = newPosts.map { post ->
                    val liked = likeRepository.isLiked(post.id, uid)
                    post.copy(isLiked = liked)
                }

                _uiState.value = FeedUiState.Success(hydrated)
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error(e.message ?: "Something went wrong")
            }

            isLoading = false
        }
    }

    /* ---------------- LOAD MORE ---------------- */

    fun loadMore() {
        if (isLoading || lastPost == null) return
        isLoading = true

        viewModelScope.launch {
            try {
                val (newPosts, nextLast) = postRepository.fetchFeedPage(lastPost)
                lastPost = nextLast

                val hydrated = newPosts.map { post ->
                    val liked = likeRepository.isLiked(post.id, uid)
                    post.copy(isLiked = liked)
                }

                val current = (_uiState.value as? FeedUiState.Success)?.posts ?: emptyList()
                _uiState.value = FeedUiState.Success(current + hydrated)

            } catch (_: Exception) { }

            isLoading = false
        }
    }

    /* ---------------- LIKE ---------------- */

    fun toggleLike(post: Post) {
        if (uid.isEmpty()) return

        viewModelScope.launch {
            val state = _uiState.value
            if (state !is FeedUiState.Success) return@launch

            val currentlyLiked = post.isLiked

            // Optimistic UI update
            val updated = state.posts.map {
                if (it.id == post.id) {
                    it.copy(
                        isLiked = !currentlyLiked,
                        likesCount = if (currentlyLiked) it.likesCount - 1 else it.likesCount + 1
                    )
                } else it
            }
            _uiState.value = FeedUiState.Success(updated)

            // Firestore sync
            try {
                if (currentlyLiked)
                    likeRepository.unlike(post.id, uid)
                else
                    likeRepository.like(post.id, uid)
            } catch (e: Exception) {
                // optional rollback
            }
        }
    }

    /* ---------------- SAVE ---------------- */

    fun toggleSave(post: Post) {
        if (uid.isEmpty()) return

        viewModelScope.launch {
            val state = _uiState.value
            if (state !is FeedUiState.Success) return@launch

            // Update Firestore
            if (post.isSaved) {
                postRepository.unsavePost(uid, post.id)
            } else {
                postRepository.savePost(uid, post)
            }

            // Update UI
            val updated = state.posts.map {
                if (it.id == post.id) {
                    it.copy(isSaved = !post.isSaved)
                } else it
            }

            _uiState.value = FeedUiState.Success(updated)
        }
    }
}
