package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CineBottomNav
import com.example.ui.screens.*
import com.example.ui.theme.CineBackground
import com.example.ui.theme.CineStreamTheme
import com.example.ui.viewmodel.CineStreamViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CineStreamViewModel = viewModel()
            val currentUser by viewModel.currentUser.collectAsState()

            CineStreamTheme(darkTheme = currentUser.darkTheme) {
                CineStreamApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CineStreamApp(
    viewModel: CineStreamViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedMovie by viewModel.selectedMovie.collectAsState()
    val playingMovie by viewModel.playingMovie.collectAsState()
    val playingEpisode by viewModel.playingEpisode.collectAsState()
    val playerStartPosition by viewModel.playerStartPositionMs.collectAsState()
    val showAdminDashboard by viewModel.showAdminDashboard.collectAsState()
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val myListIds by viewModel.myListMovieIds.collectAsState()

    // Active full-screen video playback overlay
    if (playingMovie != null) {
        PlayerScreen(
            movie = playingMovie!!,
            episode = playingEpisode,
            startPositionMs = playerStartPosition,
            viewModel = viewModel
        )
        return
    }

    // Admin Management Console
    if (showAdminDashboard) {
        AdminDashboardScreen(viewModel = viewModel)
        return
    }

    // Movie Details Overlay Screen
    if (selectedMovie != null) {
        MovieDetailsScreen(
            movie = selectedMovie!!,
            viewModel = viewModel
        )
        return
    }

    // System BackHandler: if not on Home tab, return to Home tab
    BackHandler(enabled = currentTab != NavigationTab.HOME) {
        viewModel.selectTab(NavigationTab.HOME)
    }

    // Main App Shell with Bottom Navigation
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(CineBackground),
        containerColor = CineBackground,
        bottomBar = {
            CineBottomNav(
                selectedTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) },
                isBengali = viewModel.isBengali,
                downloadCount = downloads.count { it.status == com.example.data.model.DownloadStatus.DOWNLOADING },
                watchlistCount = myListIds.size
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    NavigationTab.HOME -> HomeScreen(viewModel = viewModel)
                    NavigationTab.EXPLORE -> ExploreScreen(viewModel = viewModel)
                    NavigationTab.DOWNLOADS -> DownloadsScreen(viewModel = viewModel)
                    NavigationTab.MY_LIST -> MyListScreen(viewModel = viewModel)
                    NavigationTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Authentication & Profile Switch Modal
    if (showAuthDialog) {
        AuthDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeAuthDialog() }
        )
    }
}
