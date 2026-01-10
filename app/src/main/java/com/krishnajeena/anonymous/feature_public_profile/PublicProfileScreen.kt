package com.krishnajeena.anonymous.feature_public_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.krishnajeena.anonymous.ui.feed.PostItem
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.krishnajeena.anonymous.domain.user.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    onBack: () -> Unit,
    viewModel: PublicProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val posts by viewModel.posts.collectAsState()

    Scaffold(
        topBar = {
            PublicProfileTopBar(user = user,
                onBack = onBack)
            }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // Header
            user?.let {
                PublicProfileHeader(it, viewModel)
            }

            Spacer(Modifier.height(16.dp))

            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No posts yet")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(posts, key = { it.id }) { post ->
                        PostItem(
                            post = post,
                            onToggleSave = {},
                            withSaveButton = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PublicProfileHeader(user: User, viewModel: PublicProfileViewModel) {


    val isFollowing by viewModel.isFollowing.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            model = user.photoUrl,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = user.tag,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier, horizontalArrangement = Arrangement.SpaceBetween
            , verticalAlignment = Alignment.CenterVertically) {

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(2.dp)){
                Text(text = "Followers")
                Text(text = "${viewModel.followers.collectAsState().value}")
            }

            if(!viewModel.isBothSame()){
                Button(
                    onClick = { viewModel.toggleFollow() },
                    modifier = Modifier.padding(horizontal = 5.dp)
                ) {
                    Text(if (isFollowing) "Following" else "Follow")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(2.dp)){
                Text(text = "Following")
                Text(text = "${viewModel.following.collectAsState().value}")
            }

        }

    }
}

@Composable
fun PublicProfileTopBar(
    onBack: () -> Unit,
    user: User?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)   // ← exact height
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                user?.tag ?: "Profile",
                style = MaterialTheme.typography.titleMedium
            )
        }

        IconButton(onClick = onBack) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
    }
}

