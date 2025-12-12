package com.krishnajeena.anonymous

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.krishnajeena.anonymous.ui.theme.AnonymousTheme
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.AndroidEntryPoint


@HiltAndroidApp
class App : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_Anonymous_Splash)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnonymousTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Text(text = "Hello World", modifier = Modifier.padding(innerPadding))

                }
            }
        }
    }
}
