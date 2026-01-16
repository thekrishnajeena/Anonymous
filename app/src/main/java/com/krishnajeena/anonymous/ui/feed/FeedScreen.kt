package com.krishnajeena.anonymous.ui.feed

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.*
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.krishnajeena.anonymous.R
import com.krishnajeena.anonymous.domain.post.Post
import com.krishnajeena.anonymous.feature_feed.FeedViewModel
import com.krishnajeena.anonymous.feature_feed.FeedViewModel.FeedUiState
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
    viewModel: FeedViewModel,
    onSearchClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitialFeed()
    }

    Scaffold(
        topBar = { FeedTopBar(onSearchClick) },
        containerColor = Color.Transparent
    ) { innerPadding ->

        when (uiState) {

            is FeedViewModel.FeedUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Loading…") }
            }

            is FeedViewModel.FeedUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Something went wrong") }
            }

            is FeedViewModel.FeedUiState.Success -> {
                val posts = (uiState as FeedUiState.Success).posts

                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {

                    items(posts, key = { it.id }) { post ->
                        PostItem(
                            post = post,
                            onToggleLike = viewModel::toggleLike,
                            onToggleSave = viewModel::toggleSave
                        )
                    }

                    item {
                        LaunchedEffect(posts.size) {
                            viewModel.loadMore()
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun PostItem(
    post: Post,
    onToggleLike: (Post) -> Unit,
    onToggleSave: (Post) -> Unit,
    withSaveButton: Boolean = true
) {
    GlassPostContainer {
        Column(Modifier.fillMaxWidth()) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(post.authorTag, style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${DateUtils.getRelativeTimeSpanString(post.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(0.6f),
                        fontSize = TextUnit(12f, TextUnitType.Sp)
                    )
                }

                if(withSaveButton) {
                    IconButton(onClick = { onToggleSave(post) }) {
                        Icon(
                            painterResource(
                                if (post.isSaved) R.drawable.bookmark_filled
                                else R.drawable.bookmark
                            ),
                            contentDescription = null
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(post.content)

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(1.dp)) {

                IconButton(onClick = { onToggleLike(post) },
                    modifier = Modifier.size(18.dp)) {
                    Icon(
                        painterResource(
                            if (post.isLiked) R.drawable.liked else R.drawable.not_liked
                        ),
                        contentDescription = null
                    )
                }

                Text("${post.likesCount}")
            }
        }
    }
}



@Composable
fun GlassPostContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.55f),
            shape = RoundedCornerShape(20.dp)
        )){
        Box(
            modifier = modifier
                .fillMaxWidth()
                .matchParentSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Magenta.copy(alpha=0.1f))
                .blur(45.dp)
                .padding(10.dp)
        )


        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            content()
        }
}

}

