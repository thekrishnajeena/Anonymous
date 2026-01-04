package com.krishnajeena.anonymous

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.krishnajeena.anonymous.app.AppViewModel
import com.krishnajeena.anonymous.domain.AuthState
import com.krishnajeena.anonymous.feature_auth.AuthViewModel
import com.krishnajeena.anonymous.feature_public_profile.PublicProfileScreen
import com.krishnajeena.anonymous.feature_search.SearchScreen
import com.krishnajeena.anonymous.ui.auth.AuthScreen
import com.krishnajeena.anonymous.ui.create.CreateScreen
import com.krishnajeena.anonymous.ui.feed.FeedScreen
import com.krishnajeena.anonymous.ui.profile.ProfileScreen
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
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Anonymous)
        enableEdgeToEdge()

        setContent {

            val authState by appViewModel.authState.collectAsState()

            when (authState) {

                AuthState.Loading -> {
                    SplashScreen()
                }

                AuthState.Unauthenticated -> {
                    AuthScreen(
                        viewModel = authViewModel,
                        onAuthSuccess = { uid ->
                            appViewModel.onLoginSuccess(uid)
                        })
                }

                is AuthState.Authenticated -> {
                    MainScaffold(
                        onLogout = { appViewModel.logout() }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScaffold(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        },
        floatingActionButton = {
            CreatePostFab{
                navController.navigate("create")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->

        MainNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onLogout = onLogout
        )
    }
}

@Composable
fun CreatePostFab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.offset(y=38.dp),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        elevation = FloatingActionButtonDefaults.elevation(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Create Post"
        )
    }
}


@Composable
fun BottomNavBar(navController: NavHostController) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar(modifier = Modifier) {
        MainScreens.entries.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) }
            )
        }
    }
}


enum class MainScreens(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    Feed("feed", Icons.Default.Home, "Feed"),
    Profile("profile", Icons.Default.Person, "Profile"),
}

enum class InternalScreen(
    val route: String,
    val icon: ImageVector,
    val label: String
){
    Search("search", Icons.Default.Search, "Search"),
    PublicProfile("public_profile/{uid}", Icons.Default.Person, "Public Profile")

}

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = MainScreens.Feed.route,
        modifier = modifier
    ) {

        composable(MainScreens.Feed.route) {
            FeedScreen(onSearchClick = {
                navController.navigate(InternalScreen.Search.route)
            })
        }

        composable(InternalScreen.Search.route) {
            SearchScreen(
                onBack = {
                    navController.popBackStack()
                },
                onUserClick = {
                    uid ->
                    navController.navigate("profile/$uid")
                }
            )
        }

        composable(route = "profile/{uid}",
            arguments = listOf(navArgument("uid"){ type = NavType.StringType})) {
            PublicProfileScreen(
                onBack = {navController.popBackStack()}
            )
        }

        composable("create",
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it},
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                )
            }) {
            CreateScreen(
                onCancel = { navController.popBackStack()}
                    , onPostSuccess = { navController.popBackStack()})
        }

        composable(MainScreens.Profile.route) {
            ProfileScreen(onLogout = onLogout)
        }
    }
}
