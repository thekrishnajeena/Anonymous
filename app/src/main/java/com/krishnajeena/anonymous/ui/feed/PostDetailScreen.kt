package com.krishnajeena.anonymous.ui.feed

import com.krishnajeena.anonymous.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.krishnajeena.anonymous.domain.post.Post
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.krishnajeena.anonymous.feature_comment.CommentViewModel

@Composable
fun PostDetailScreen(
    postId: String,
    viewModel: CommentViewModel = hiltViewModel(),
    navController: NavController
) {
    val post by viewModel.post.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val input by viewModel.input.collectAsState()

    // Load post + comments on first enter
    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
        viewModel.loadComments(postId)
    }

    Column(Modifier.fillMaxSize()) {

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {navController.popBackStack()}) {
                Icon(
                    painterResource(R.drawable.back),
                    contentDescription = "Back"
                )
            }
            Text(
                "Post",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Post Content
        post?.let {
            PostItem(
                post = it,
                onToggleLike = viewModel::toggleLike,
                onToggleSave = viewModel::toggleSave,
                withSaveButton = false
            )
        }

        Spacer(Modifier.height(12.dp))

        // Comments list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(comments) { c ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        c.authorTag,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(c.content)
                }
            }
        }

        // Comment Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {

            OutlinedTextField(
                value = input,
                onValueChange = viewModel::onInputChange,
                modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                placeholder = { Text("Add a comment…") }
            )

            IconButton(
                onClick = { viewModel.postComment(postId) },
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.send),
                    contentDescription = "Send"
                )
            }
        }
    }
}
