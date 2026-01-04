package com.krishnajeena.anonymous.feature_public_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.anonymous.data.FirestoreUserRepository
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uid: String = savedStateHandle["uid"] ?: ""

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    init {
        loadUser()
        loadPosts()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = userRepository.getUser(uid)
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
}
