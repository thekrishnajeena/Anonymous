package com.krishnajeena.anonymous.feature_comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.anonymous.data.FirestoreUserRepository
import com.krishnajeena.anonymous.data.auth.FirebaseAuthRepository
import com.krishnajeena.anonymous.data.post.FirestorePostRepository
import com.krishnajeena.anonymous.domain.post.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val postRepo: FirestorePostRepository,
    private val commentRepo: CommentRepository,
    private val authRepo: FirebaseAuthRepository,
    private val userRepo: FirestoreUserRepository
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _input = MutableStateFlow("")
    val input = _input.asStateFlow()

    fun onInputChange(newText: String) {
        _input.value = newText
    }

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _post.value = postRepo.getPostById(postId)
        }
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            commentRepo.observeComments(postId).collect {
                _comments.value = it
            }
        }
    }

    fun postComment(postId: String) {
        val uid = authRepo.currentUser()?.uid ?: return

        viewModelScope.launch {
            val user = userRepo.getUser(uid)

            commentRepo.addComment(
                postId = postId,
                userId = user.uid,
                userTag = user.tag,
                content = input.value
            )
            _input.value = ""
        }
    }

    fun toggleLike(post: Post) {
        // You already wrote this logic earlier — reuse it
    }

    fun toggleSave(post: Post) {
        // same as above
    }
}
