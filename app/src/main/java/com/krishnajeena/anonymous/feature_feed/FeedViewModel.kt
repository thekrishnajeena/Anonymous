package com.krishnajeena.anonymous.feature_feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.krishnajeena.anonymous.data.auth.FirebaseAuthRepository
import com.krishnajeena.anonymous.data.post.FirestorePostRepository
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
    private val authRepository: FirebaseAuthRepository
) : ViewModel(){

    private val uid: String = authRepository.currentUser()?.uid ?: ""

    val posts = combine(
        postRepository.observePosts(),
        postRepository.observeSavedPosts(uid)
    ) { allPosts, savedPosts ->

        val savedIds = savedPosts.map { it.id }.toSet()

        allPosts.map { post ->
            post.copy(
                isSaved = post.id in savedIds
            )
        }

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val postsNew = _posts.asStateFlow()

    private var lastPost: DocumentSnapshot? = null
    private var isLoading = false

    fun loadInitialFeed() {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            val (newPosts, last) = postRepository.fetchFeedPage(null)
            _posts.value = newPosts
            lastPost = last
            isLoading = false
        }
    }

    fun loadMore() {
        if (isLoading || lastPost == null) return
        isLoading = true

        viewModelScope.launch {
            val (newPosts, last) = postRepository.fetchFeedPage(lastPost)
            _posts.value += newPosts
            lastPost = last
            isLoading = false
        }
    }

    suspend fun toggleSave(post: Post) {
        if (post.isSaved) {
            postRepository.unsavePost(uid, post.id)
        } else {
            postRepository.savePost(uid, post)
        }
    }

}