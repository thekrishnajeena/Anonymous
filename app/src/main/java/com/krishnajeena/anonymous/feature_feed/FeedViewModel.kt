package com.krishnajeena.anonymous.feature_feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.anonymous.data.auth.FirebaseAuthRepository
import com.krishnajeena.anonymous.data.post.FirestorePostRepository
import com.krishnajeena.anonymous.domain.post.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    suspend fun toggleSave(post: Post) {
        if (post.isSaved) {
            postRepository.unsavePost(uid, post.id)
        } else {
            postRepository.savePost(uid, post)
        }
    }

}