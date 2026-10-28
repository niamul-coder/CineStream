package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.FileDownload
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
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineStreamViewModel
import com.example.ui.viewmodel.NavigationTab

@Composable
fun DownloadsScreen(
    viewModel: CineStreamViewModel,
    modifier: Modifier = Modifier
) {
    val downloads by viewModel.downloads.collectAsState()
    val movies by viewModel.movies.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showDeleteConfirmDialog by remember { mutableStateOf<DownloadItem?>(null) }

    val activeDownloads = remember(downloads) {
        downloads.filter { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PAUSED || it.status == DownloadStatus.PENDING }
    }
    val completedDownloads = remember(downloads) {
        downloads.filter { it.status == DownloadStatus.COMPLETED }
    }

    val totalStorageUsedMb = remember(completedDownloads) {
        completedDownloads.sumOf { it.fileSizeMb }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CineBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("downloads_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = viewModel.t("Downloads & Offline Library", "ডাউনলোড ও অফলাইন লাইব্রেরি"),
                    color = CineTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = viewModel.t("Watch your saved movies anywhere without an internet connection", "ইন্টারনেট ছাড়াই যেকোনো স্থানে আপনার সংরক্ষিত সিনেমা উপভোগ করুন"),
                    color = CineTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Storage & Preference Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CineSurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = CineCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Device Storage",
                                color = CineTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${totalStorageUsedMb} MB used of 64 GB",
                            color = CineTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Storage Progress Bar
                    LinearProgressIndicator(
                        progress = { (totalStorageUsedMb.toFloat() / (64f * 1024f)).coerceIn(0.01f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CineCyan,
                        trackColor = CineSurfaceHigh
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Wi-Fi Only Download Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Download on Wi-Fi Only",
                                color = CineTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Save mobile cellular data",
                                color = CineTextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = currentUser.wifiOnlyDownload,
                            onCheckedChange = { isChecked ->
                                viewModel.updateSettings(
                                    lang = currentUser.preferredLanguage,
                                    dark = currentUser.darkTheme,
                                    wifiOnly = isChecked,
                                    quality = currentUser.defaultQuality
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CineCyan,
                                uncheckedTrackColor = CineSurfaceHigh
                            )
                        )
                    }
                }
            }
        }

        // Active Downloads section
        if (activeDownloads.isNotEmpty()) {
            item {
                Text(
                    text = "Active Downloads (${activeDownloads.size})",
                    color = CineCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(activeDownloads, key = { it.id }) { item ->
                ActiveDownloadCard(
                    item = item,
                    onPause = { viewModel.pauseDownload(item.id) },
                    onResume = { viewModel.resumeDownload(item.id) },
                    onCancel = { viewModel.cancelDownload(item.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Completed Downloads section
        item {
            Text(
                text = "Offline Library (${completedDownloads.size})",
                color = CineTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        if (completedDownloads.isEmpty() && activeDownloads.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CineSurfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FileDownload,
                            contentDescription = null,
                            tint = CineTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = viewModel.t("No movies downloaded yet", "কোনো সিনেমা এখনো ডাউনলোড করা হয়নি"),
                            color = CineTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.t("Download titles from details to watch offline", "অফলাইনে দেখার জন্য সিনেমা বিস্তারিত থেকে ডাউনলোড করুন"),
                            color = CineTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.selectTab(NavigationTab.HOME) },
                            colors = ButtonDefaults.buttonColors(containerColor = CineCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Explore Movies", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(completedDownloads, key = { it.id }) { item ->
                CompletedDownloadCard(
                    item = item,
                    onPlay = {
                        val movie = movies.find { it.id == item.movieId }
                        if (movie != null) {
                            viewModel.playMovie(movie)
                        }
                    },
                    onDelete = { showDeleteConfirmDialog = item }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog != null) {
        val target = showDeleteConfirmDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Delete Download?", color = CineTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to delete '${target.movieTitle}' from offline storage? This will free ${target.fileSizeMb} MB.",
                    color = CineTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDownload(target.id)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CineRed, contentColor = Color.White)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Cancel", color = CineTextSecondary)
                }
            },
            containerColor = CineSurfaceHigh
        )
    }
}

@Composable
private fun ActiveDownloadCard(
    item: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CineSurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 75.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CineSurfaceHigh)
            ) {
                if (item.localPosterResId != null) {
                    Image(
                        painter = painterResource(id = item.localPosterResId),
                        contentDescription = item.movieTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details and progress
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.movieTitle,
                    color = CineTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.quality,
                        color = CineCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = " • ", color = CineTextMuted, fontSize = 10.sp)
                    Text(
                        text = "${(item.progress * 100).toInt()}% • ${(item.fileSizeMb * item.progress).toInt()}/${item.fileSizeMb}MB",
                        color = CineTextSecondary,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = CineCyan,
                    trackColor = CineSurfaceHigh
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (item.status == DownloadStatus.PAUSED) "Paused" else "Downloading at 14.5 MB/s",
                    color = if (item.status == DownloadStatus.PAUSED) CineAmber else CineTextMuted,
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Control buttons
            IconButton(
                onClick = { if (item.status == DownloadStatus.PAUSED) onResume() else onPause() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (item.status == DownloadStatus.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = "Pause/Resume",
                    tint = CineCyan
                )
            }

            IconButton(
                onClick = onCancel,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = CineTextMuted
                )
            }
        }
    }
}

@Composable
private fun CompletedDownloadCard(
    item: DownloadItem,
    onPlay: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CineSurfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CineSurfaceHigh)
            ) {
                if (item.localPosterResId != null) {
                    Image(
                        painter = painterResource(id = item.localPosterResId),
                        contentDescription = item.movieTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Play overlay
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play offline",
                        tint = CineCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.movieTitle,
                    color = CineTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = CineCyanGlow
                    ) {
                        Text(
                            text = item.quality,
                            color = CineCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${item.fileSizeMb} MB",
                        color = CineTextSecondary,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CineGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Downloaded • Ready Offline",
                        color = CineGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Delete action
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = CineTextMuted
                )
            }
        }
    }
}
