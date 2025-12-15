package com.krishnajeena.anonymous.ui.auth

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.krishnajeena.anonymous.feature_auth.AuthUiState
import com.krishnajeena.anonymous.feature_auth.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import com.krishnajeena.anonymous.R


@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Welcome to Aliasly")

        Spacer(Modifier.height(24.dp))

        Button(
            enabled = uiState !is AuthUiState.Loading,
            onClick = {
               activity?.let {
                    viewModel.signInWithGoogle(
                        activity = activity,
                        onSuccess = onAuthSuccess
                    )
                }
            },
        ) {
            Row(modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center){
            Icon(
                painter = painterResource(R.drawable.google),
                contentDescription = "Google Icon",
                modifier = Modifier.size(14.dp),
                tint = Color.Unspecified
            )
                Spacer(modifier = Modifier.width(10.dp))
            Text("Continue with Google")
        }
        }

        if (uiState is AuthUiState.Loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (uiState is AuthUiState.Error) {
            Text(
                (uiState as AuthUiState.Error).msg,
                color = Color.Red
            )
        }
    }
}
