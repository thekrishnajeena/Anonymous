package com.krishnajeena.anonymous.featureProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.anonymous.data.FirestoreUserRepository
import com.krishnajeena.anonymous.data.auth.FirebaseAuthRepository
import com.krishnajeena.anonymous.data.post.FirestorePostRepository
import com.krishnajeena.anonymous.domain.post.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.krishnajeena.anonymous.domain.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val user: User) : ProfileUiState
    data class Error(val msg: String) : ProfileUiState
}

data class ProfileFeedUi(
    val userPosts: List<Post> = emptyList(),
    val savedPosts: List<Post> = emptyList()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val postRepository: FirestorePostRepository,
    private val userRepository: FirestoreUserRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _feedUi = MutableStateFlow(ProfileFeedUi())
    val feedUi = _feedUi.asStateFlow()

    init {
        loadProfile()
        loadMyPosts()
        loadSaved()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val uid = authRepository.currentUser()?.uid
                    ?: error("User not logged in")

                val user = userRepository.getUser(uid)
                _uiState.value = ProfileUiState.Success(user)

            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(
                    e.message ?: "Failed to load profile"
                )
            }
        }
    }

    private fun loadMyPosts() {
        val uid = authRepository.currentUser()?.uid ?: return

        viewModelScope.launch {
            combine(
                postRepository.observeUserPosts(uid),
                postRepository.observeSavedPosts(uid)
            ) { myPosts, savedPosts ->

                val savedIds = savedPosts.map { it.id }.toSet()

                myPosts.map { post ->
                    post.copy(isSaved = post.id in savedIds)
                }

            }.collect { merged ->
                _feedUi.value = _feedUi.value.copy(userPosts = merged)
            }
        }
    }

    private fun loadSaved() {
        val uid = authRepository.currentUser()?.uid ?: return

        viewModelScope.launch {
            combine(
                postRepository.observeSavedPosts(uid),
                postRepository.observeSavedPosts(uid)
            ) { savedPosts, againSavedPosts ->

                savedPosts.map { post ->
                    post.copy(isSaved = true)
                }

            }.collect { merged ->
                _feedUi.value = _feedUi.value.copy(savedPosts = merged)
            }
        }
    }


}