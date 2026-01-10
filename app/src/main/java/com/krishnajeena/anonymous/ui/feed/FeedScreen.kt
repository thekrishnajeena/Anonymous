package com.krishnajeena.anonymous.ui.feed

import android.text.format.DateUtils
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

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.sparklebg)
    )

    Scaffold(
        topBar = { FeedTopBar(onSearchClick) },
        containerColor = Color.Transparent
    ) { inner ->

        Box(Modifier.fillMaxSize()) {

            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.fillMaxSize()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(posts, key = { it.id }) { post ->
                    PostItem(
                        post = post,
                        onToggleSave = { viewModel.viewModelScope.launch {
                            viewModel.toggleSave(it)
                        } }
                    )
                }

                item {
                    LaunchedEffect(Unit) {
                        viewModel.loadMore()
                    }
                }
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
    GlassPostContainer {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.authorTag,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "${DateUtils.getRelativeTimeSpanString(post.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(0.6f)
                    )
                }

                if (withSaveButton) {
                    IconButton(
                        onClick = { onToggleSave(post) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                if (post.isSaved)
                                    R.drawable.bookmark_filled
                                else
                                    R.drawable.bookmark
                            ),
                            contentDescription = "Save"
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge
            )
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

