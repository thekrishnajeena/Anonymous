package com.krishnajeena.anonymous

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.krishnajeena.anonymous.app.AppViewModel
import com.krishnajeena.anonymous.domain.AuthState
import com.krishnajeena.anonymous.feature_auth.AuthViewModel
import com.krishnajeena.anonymous.ui.auth.AuthScreen
import com.krishnajeena.anonymous.ui.feed.FeedScreen
import com.krishnajeena.anonymous.ui.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_Anonymous_Splash)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val authState by appViewModel.authState.collectAsState()

            when (authState) {
                AuthState.Loading -> SplashScreen()

                AuthState.Unauthenticated ->
                    AuthScreen(
                        viewModel = authViewModel,
                        onAuthSuccess = { uid ->
                            appViewModel.onLoginSuccess(uid)
                        }
                    )

                is AuthState.Authenticated ->
                    FeedScreen { appViewModel.logout() }
            }

        }
    }
}
