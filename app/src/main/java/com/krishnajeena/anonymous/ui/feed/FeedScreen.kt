package com.krishnajeena.anonymous.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewModelScope
import com.krishnajeena.anonymous.R
import com.krishnajeena.anonymous.domain.post.Post
import com.krishnajeena.anonymous.feature_feed.FeedViewModel
import kotlinx.coroutines.launch

@Composable
fun FeedTopBar(
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)   // ← exact height
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Anonymous",
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = onSearchClick) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onSearchClick: () -> Unit
) {
    val posts by viewModel.posts.collectAsState()

    Scaffold(topBar = {
        FeedTopBar { onSearchClick() }
    }){
        inner ->

    LazyColumn(
        modifier = Modifier.padding(inner),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(
            items = posts,
            key = { it.id }
        ) { post ->
            PostItem(post,
                onToggleSave = {p ->
                    viewModel.viewModelScope.launch {
                        viewModel.toggleSave(p)
                    }
                })
        }
    }
}

}
@Composable
fun PostItem(
    post: Post,
    onToggleSave: (Post) -> Unit,
    withSaveButton: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.authorTag,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if(withSaveButton) {
            IconButton(onClick = { onToggleSave(post) }) {
                Icon(
                    painter = if (post.isSaved)
                        painterResource(R.drawable.bookmark_filled)
                    else
                        painterResource(R.drawable.bookmark),
                    contentDescription = "Save"
                )
            }}
        }

        Spacer(Modifier.height(6.dp))

        Text(post.content, style = MaterialTheme.typography.bodyLarge)
    }
}

