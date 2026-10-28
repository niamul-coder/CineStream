package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Episode
import com.example.data.model.Movie
import com.example.ui.components.MovieCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineStreamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movie: Movie,
    viewModel: CineStreamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allMovies by viewModel.movies.collectAsState()
    val myListIds by viewModel.myListMovieIds.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()

    val isFavorite = myListIds.contains(movie.id)
    val historyItem = watchHistory.find { it.movieId == movie.id }
    val existingDownload = downloads.find { it.movieId == movie.id }

    var showDownloadDialog by remember { mutableStateOf(false) }

    // Intercept back button to return to home screen
    BackHandler {
        viewModel.closeMovieDetails()
    }

    Scaffold(
        containerColor = CineBackground,
        modifier = modifier.testTag("movie_details_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Hero Backdrop with Gradient and Top Controls
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    if (movie.localBackdropResId != null) {
                        Image(
                            painter = painterResource(id = movie.localBackdropResId),
                            contentDescription = movie.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (movie.localPosterResId != null) {
                        Image(
                            painter = painterResource(id = movie.localPosterResId),
                            contentDescription = movie.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Cinematic Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        CineBackground.copy(alpha = 0.85f),
                                        CineBackground
                                    ),
                                    startY = 0f
                                )
                            )
                    )

                    // Navigation Actions (Back button & Favorite / Share)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.closeMovieDetails() },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .testTag("details_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { viewModel.toggleFavorite(movie.id) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .testTag("details_favorite_toggle")
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) CineCyan else Color.White
                                )
                            }

                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Watch '${movie.title}' on CineStream 2026!")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share movie"))
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Floating Play Button in Backdrop
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(CineCyan, CircleShape)
                            .align(Alignment.Center)
                            .clickable {
                                viewModel.playMovie(movie, startMs = historyItem?.positionMs ?: 0L)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play movie",
                            tint = Color.Black,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }

            // Movie Info Details Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = movie.title,
                        color = CineTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    if (!movie.originalTitle.isNullOrBlank() && movie.originalTitle != movie.title) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = movie.originalTitle,
                            color = CineCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Metadata Badges Row: Year • Content Rating • Runtime • Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${movie.releaseYear}",
                            color = CineTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CineSurfaceHigh
                        ) {
                            Text(
                                text = movie.contentRating,
                                color = CineCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "${movie.runtimeMinutes} min",
                            color = CineTextSecondary,
                            fontSize = 13.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = CineAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "%.1f".format(movie.rating),
                                color = CineTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "(${movie.ratingCount})",
                                color = CineTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quality & Audio format tags
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("4K UHD", "HDR10+", "DOLBY ATMOS", "CC").forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = CineSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder)
                            ) {
                                Text(
                                    text = tag,
                                    color = CineTextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Main Action Buttons: Watch Now & Download
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.playMovie(movie, startMs = historyItem?.positionMs ?: 0L)
                            },
                            modifier = Modifier
                                .weight(1.4f)
                                .height(48.dp)
                                .testTag("details_watch_now_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CineCyan,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            val btnText = if (historyItem != null && historyItem.progressPercentage > 0f) {
                                "Resume (${(historyItem.progressPercentage * 100).toInt()}%)"
                            } else {
                                "Watch Now"
                            }
                            Text(text = btnText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Button(
                            onClick = { showDownloadDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("details_download_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CineSurfaceHigh,
                                contentColor = CineTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (existingDownload != null) Icons.Default.CheckCircle else Icons.Default.Download,
                                contentDescription = null,
                                tint = if (existingDownload != null) CineGreen else CineTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (existingDownload != null) "Saved" else "Download",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Synopsis
                    Text(
                        text = "Synopsis",
                        color = CineTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = movie.description,
                        color = CineTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Cast & Crew info
                    Text(
                        text = "Director: ${movie.director}",
                        color = CineTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Starring: ${movie.cast.joinToString(", ")}",
                        color = CineTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Languages: ${movie.languages.joinToString(", ")}",
                        color = CineTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // TV Series Episodes (if applicable)
            if (movie.isSeries && !movie.seasons.isNullOrEmpty()) {
                item {
                    val season = movie.seasons.first()
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Text(
                            text = season.title,
                            color = CineCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        season.episodes.forEach { episode ->
                            EpisodeRowItem(
                                episode = episode,
                                onPlay = { viewModel.playMovie(movie, episode = episode) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Related / Recommended Titles
            item {
                val related = allMovies.filter { other ->
                    other.id != movie.id && other.genres.any { movie.genres.contains(it) }
                }.ifEmpty { allMovies.filter { it.id != movie.id } }

                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = "More Like This",
                        color = CineTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(related) { item ->
                            MovieCard(
                                movie = item,
                                cardWidth = 115.dp,
                                cardHeight = 170.dp,
                                onClick = { viewModel.openMovieDetails(item) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Download Quality Selection Modal
    if (showDownloadDialog) {
        val qualities = listOf(
            Triple("4K UHD", (movie.fileSizeMb * 2.2).toInt(), "Best visual quality for large screens"),
            Triple("1080p FHD", movie.fileSizeMb.toInt(), "Crisp high definition standard"),
            Triple("720p HD", (movie.fileSizeMb * 0.6).toInt(), "Fast download with balanced storage"),
            Triple("480p SD", (movie.fileSizeMb * 0.35).toInt(), "Data saver mode for mobile storage")
        )

        AlertDialog(
            onDismissRequest = { showDownloadDialog = false },
            title = {
                Text(
                    text = "Download '${movie.title}'",
                    color = CineTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Choose quality profile for offline viewing:",
                        color = CineTextSecondary,
                        fontSize = 12.sp
                    )

                    qualities.forEach { (profile, sizeMb, desc) ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = CineSurfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.startDownload(movie, profile)
                                    showDownloadDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = profile,
                                        color = CineCyan,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = desc,
                                        color = CineTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = "$sizeMb MB",
                                    color = CineTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDownloadDialog = false }) {
                    Text("Cancel", color = CineTextSecondary)
                }
            },
            containerColor = CineSurfaceHigh
        )
    }
}

@Composable
private fun EpisodeRowItem(
    episode: Episode,
    onPlay: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
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
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(CineSurfaceHigh, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${episode.episodeNumber}",
                    color = CineCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.title,
                    color = CineTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${episode.durationMinutes} min • ${episode.synopsis}",
                    color = CineTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onPlay) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Play episode",
                    tint = CineCyan,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
