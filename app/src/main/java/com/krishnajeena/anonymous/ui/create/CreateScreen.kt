package com.krishnajeena.anonymous.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.krishnajeena.anonymous.feature_create.CreatePostViewModel
import androidx.compose.runtime.getValue
import com.krishnajeena.anonymous.feature_create.CreatePostState

@Composable
fun CreateScreen(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onPostSuccess: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel()

) {

    val focusRequester = remember { FocusRequester()}
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val state by viewModel.state.collectAsState()


    LaunchedEffect(Unit){
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    LaunchedEffect(state) {
        when(state){
            is CreatePostState.Success -> {
                onPostSuccess()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                onCancel()
            }) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.submitPost() },
                enabled = viewModel.text.isNotBlank() && state !is CreatePostState.Posting
            ) {
                Text("Post")
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.text,
            onValueChange = viewModel::onTextChange,
            placeholder = { Text("What's on your mind?") },
            modifier = Modifier.fillMaxSize(),
            maxLines = Int.MAX_VALUE
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${viewModel.text.length}/500",
            modifier = Modifier.align(Alignment.End),
            style = MaterialTheme.typography.labelSmall
        )
    }
}