package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WatchHistoryItem
import com.example.ui.components.MovieCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineStreamViewModel
import com.example.ui.viewmodel.NavigationTab

@Composable
fun MyListScreen(
    viewModel: CineStreamViewModel,
    modifier: Modifier = Modifier
) {
    val movies by viewModel.movies.collectAsState()
    val myListIds by viewModel.myListMovieIds.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        viewModel.t("Saved Movies", "সংরক্ষিত সিনেমা"),
        viewModel.t("Watch History", "দেখা ইতিহাস")
    )

    var showClearHistoryDialog by remember { mutableStateOf(false) }

    val savedMovies = remember(movies, myListIds) {
        movies.filter { myListIds.contains(it.id) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CineBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("my_list_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = viewModel.t("My Library", "আমার লাইব্রেরি"),
                color = CineTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            if (selectedTabIndex == 1 && watchHistory.isNotEmpty()) {
                TextButton(
                    onClick = { showClearHistoryDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = CineRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = viewModel.t("Clear", "মুছুন"),
                        color = CineRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = CineSurface,
            contentColor = CineCyan,
            divider = { HorizontalDivider(color = CineBorder) }
        ) {
            tabTitles.forEachIndexed { index, title ->
                val selected = selectedTabIndex == index
                Tab(
                    selected = selected,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.testTag("tab_${if (index == 0) "saved" else "history"}"),
                    text = {
                        Text(
                            text = title,
                            color = if (selected) CineCyan else CineTextSecondary,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Tab Contents
        if (selectedTabIndex == 0) {
            // Saved Movies Grid
            if (savedMovies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = null,
                            tint = CineTextMuted,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = viewModel.t("Your list is empty", "আপনার তালিকা খালি"),
                            color = CineTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.t("Bookmark your favorite titles to watch later", "পরে দেখার জন্য পছন্দের সিনেমা বুকমার্ক করুন"),
                            color = CineTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.selectTab(NavigationTab.HOME) },
                            colors = ButtonDefaults.buttonColors(containerColor = CineCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Discover Titles", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 110.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(savedMovies, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            cardWidth = 110.dp,
                            cardHeight = 165.dp,
                            onClick = { viewModel.openMovieDetails(movie) }
                        )
                    }
                }
            }
        } else {
            // Watch History List
            if (watchHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = CineTextMuted,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = viewModel.t("No watch history yet", "কোনো দেখার ইতিহাস নেই"),
                            color = CineTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.t("Movies you start watching will appear here", "আপনার শুরু করা সিনেমা এখানে প্রদর্শিত হবে"),
                            color = CineTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(watchHistory, key = { it.movieId }) { item ->
                        val movie = movies.find { it.id == item.movieId }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CineSurfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (movie != null) viewModel.playMovie(movie, startMs = item.positionMs)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Thumbnail with progress
                                Box(
                                    modifier = Modifier
                                        .size(width = 80.dp, height = 55.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CineSurfaceHigh)
                                ) {
                                    if (item.localBackdropResId != null) {
                                        Image(
                                            painter = painterResource(id = item.localBackdropResId),
                                            contentDescription = item.movieTitle,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else if (item.localPosterResId != null) {
                                        Image(
                                            painter = painterResource(id = item.localPosterResId),
                                            contentDescription = item.movieTitle,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                            .align(Alignment.Center),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = CineCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    LinearProgressIndicator(
                                        progress = { item.progressPercentage },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .align(Alignment.BottomCenter),
                                        color = CineCyan,
                                        trackColor = Color.White.copy(alpha = 0.3f),
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.movieTitle,
                                        color = CineTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${(item.progressPercentage * 100).toInt()}% completed",
                                        color = CineCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.removeFromHistory(item.movieId) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = CineTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Clear history confirmation
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear Watch History?", color = CineTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "This will remove all playback progress and resume points. This action cannot be undone.",
                    color = CineTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearWatchHistory()
                        showClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CineRed, contentColor = Color.White)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel", color = CineTextSecondary)
                }
            },
            containerColor = CineSurfaceHigh
        )
    }
}
