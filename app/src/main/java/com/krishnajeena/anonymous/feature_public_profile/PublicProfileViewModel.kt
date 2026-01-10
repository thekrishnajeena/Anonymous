package com.krishnajeena.anonymous.feature_public_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.krishnajeena.anonymous.data.FirestoreUserRepository
import com.krishnajeena.anonymous.data.follow.FollowRepository
import com.krishnajeena.anonymous.data.post.FirestorePostRepository
import com.krishnajeena.anonymous.domain.post.Post
import com.krishnajeena.anonymous.domain.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository,
    private val postRepository: FirestorePostRepository,
    private val followRepository: FollowRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uid: String = savedStateHandle["uid"] ?: ""

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing = _isFollowing.asStateFlow()

    private val _followers = MutableStateFlow(0L)
    val followers = _followers.asStateFlow()

    private val _following = MutableStateFlow(0L)
    val following = _following.asStateFlow()

    private val currentUid: String = requireNotNull(
        FirebaseAuth.getInstance().currentUser?.uid
    )

    init {
        loadUser()
        loadPosts()
        loadFollowState()
    }

    fun loadFollowState() {
        viewModelScope.launch {
            _isFollowing.value =
                followRepository.isFollowing(currentUid, uid)
        }
    }

    fun isBothSame(): Boolean {
        return currentUid == uid
    }

    private fun loadUser() {
        viewModelScope.launch {
            val loadedUser = userRepository.getUser(uid)
            _user.value = loadedUser
            _followers.value = loadedUser.followersCount
            _following.value = loadedUser.followingCount
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            postRepository.observeUserPosts(uid)
                .catch { _posts.value = emptyList() }
                .collect { posts ->
                    _posts.value = posts
                }
        }
    }

    fun toggleFollow() {
        if(currentUid == uid) return

        viewModelScope.launch {
            if (_isFollowing.value) {
                followRepository.unfollow(currentUid, uid)
                _isFollowing.value = false
                _followers.value--

            } else {
                followRepository.follow(currentUid, uid)
                _isFollowing.value = true
                _followers.value++
            }
        }
    }
}
