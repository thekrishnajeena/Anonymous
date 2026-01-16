package com.krishnajeena.anonymous.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.krishnajeena.anonymous.domain.user.User
import com.krishnajeena.anonymous.featureProfile.ProfileUiState
import com.krishnajeena.anonymous.featureProfile.ProfileViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.krishnajeena.anonymous.feature_feed.FeedViewModel
import com.krishnajeena.anonymous.ui.feed.PostItem
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, onLogout: () -> Unit,
                  viewModel: ProfileViewModel = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsState()


        Box(modifier = Modifier.fillMaxSize()){
            when (uiState) {

                ProfileUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ProfileUiState.Success -> {
                    val user = (uiState as ProfileUiState.Success).user
                    ProfileContent(user, viewModel)
                }

                is ProfileUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Failed to load profile")
                    }
                }
            }

            ProfileMenuButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                onLogout = onLogout
            )


    }



}

@Composable
fun ProfileMenuButton(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = {
                    expanded = false
                    // TODO: Navigate to settings later
                }
            )
            DropdownMenuItem(
                text = { Text("Logout") },
                onClick = {
                    expanded = false
                    onLogout()
                }
            )
        }
    }
}

@Composable
fun ProfileContent(
    user: User,
    viewModel: ProfileViewModel
) {

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Posts", "Saved")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Avatar (placeholder for now)
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            if(user.photoUrl == null) {
                Text(
                    text = user.displayName.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            }


        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = user.tag,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = user.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(2.dp))

        Row(modifier = Modifier, horizontalArrangement = Arrangement.SpaceBetween
            , verticalAlignment = Alignment.CenterVertically) {

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(2.dp)){
                Text(text = "Followers")
                Text(text = "${user.followersCount}")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(2.dp)){
                Text(text = "Following")
                Text(text = "${user.followingCount}")
            }

        }


        Spacer(Modifier.height(32.dp))

        TabRow(selectedTabIndex = selectedTab){
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index,
                    onClick = {selectedTab = index},
                    text = { Text(title) })
            }
        }

        Spacer(Modifier.height(12.dp))

        when(selectedTab){
            0 -> MyPostsList(user.uid)
            1 -> SavedPostsList(user.uid)
        }
    }
}

@Composable
fun MyPostsList(uid: String, viewModel: ProfileViewModel = hiltViewModel(),
                feedViewModel: FeedViewModel = hiltViewModel()) {

    val feedUi by viewModel.feedUi.collectAsState()

    if(feedUi.userPosts.isEmpty()){
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            Text("No posts found")
        }
    }
    else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(feedUi.userPosts, key = { it.id }) { post ->
                PostItem(post,
                    onToggleSave = {p ->
                        feedViewModel.viewModelScope.launch {
                            feedViewModel.toggleSave(p)
                        }
                    },
                    onToggleLike = {feedViewModel.toggleLike(it)})
            }
        }
    }
}

@Composable
fun SavedPostsList(uid: String, viewModel: ProfileViewModel = hiltViewModel(),
                   feedViewModel: FeedViewModel = hiltViewModel()) {
    val feedUi by viewModel.feedUi.collectAsState()

    if (feedUi.savedPosts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No saved posts")
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(feedUi.savedPosts, key = { it.id }) { post ->
                PostItem(post,
                    onToggleSave = {p ->
                        feedViewModel.viewModelScope.launch {
                            feedViewModel.toggleSave(p)
                        }
                    },
                    onToggleLike = {feedViewModel.toggleLike(it)})
            }
        }
    }
}


