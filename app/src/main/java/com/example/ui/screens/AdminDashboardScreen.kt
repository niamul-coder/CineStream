package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Movie
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineStreamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: CineStreamViewModel,
    modifier: Modifier = Modifier
) {
    val movies by viewModel.movies.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showAddMovieDialog by remember { mutableStateOf(false) }
    var movieToDelete by remember { mutableStateOf<Movie?>(null) }

    BackHandler {
        viewModel.closeAdminDashboard()
    }

    Scaffold(
        containerColor = CineBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Admin Management Console",
                            color = CineTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CineViolet
                        ) {
                            Text(
                                text = "CORE 2026",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.closeAdminDashboard() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddMovieDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AddBox,
                            contentDescription = "Add Movie",
                            tint = CineCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CineSurface)
            )
        },
        modifier = modifier.testTag("admin_dashboard_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Metrics Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "Catalog Titles",
                        value = "${movies.size}",
                        accentColor = CineCyan,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Active Streams",
                        value = "1,842",
                        accentColor = CineGreen,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Bitrate Profile",
                        value = "4K / HEVC",
                        accentColor = CineElectricPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Catalog Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Content Catalog Management",
                        color = CineTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { showAddMovieDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CineCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Title", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Movies List
            items(movies, key = { it.id }) { movie ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CineSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = movie.title,
                                    color = CineTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    color = if (movie.published) CineGreen.copy(alpha = 0.2f) else CineRed.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (movie.published) "PUBLISHED" else "UNPUBLISHED",
                                        color = if (movie.published) CineGreen else CineRed,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${movie.releaseYear} • ${movie.genres.joinToString(", ")} • ${movie.languages.joinToString(", ")}",
                                color = CineTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        // Toggle published / unpublish
                        IconButton(
                            onClick = { viewModel.togglePublishMovie(movie.id) }
                        ) {
                            Icon(
                                imageVector = if (movie.published) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Publish",
                                tint = if (movie.published) CineCyan else CineTextMuted
                            )
                        }

                        // Delete
                        IconButton(
                            onClick = { movieToDelete = movie }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = CineRed
                            )
                        }
                    }
                }
            }

            // System Audit Event Logs
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "System Telemetry & Audit Trail",
                    color = CineTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CineSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        auditLogs.take(8).forEach { log ->
                            Text(
                                text = "• $log",
                                color = CineTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }

    // Add Movie Dialog
    if (showAddMovieDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newSynopsis by remember { mutableStateOf("") }
        var newYear by remember { mutableStateOf("2026") }
        var newGenre by remember { mutableStateOf("Sci-Fi") }
        var newLanguage by remember { mutableStateOf("English") }
        var newVideoUrl by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4") }

        AlertDialog(
            onDismissRequest = { showAddMovieDialog = false },
            title = { Text("Publish New Movie", color = CineTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Movie Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSynopsis,
                        onValueChange = { newSynopsis = it },
                        label = { Text("Synopsis / Plot") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newYear,
                            onValueChange = { newYear = it },
                            label = { Text("Year") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newGenre,
                            onValueChange = { newGenre = it },
                            label = { Text("Genre") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = newLanguage,
                        onValueChange = { newLanguage = it },
                        label = { Text("Language (e.g. Bengali / English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newVideoUrl,
                        onValueChange = { newVideoUrl = it },
                        label = { Text("Media Stream URL (MP4/HLS)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            val movie = Movie(
                                id = "custom_${System.currentTimeMillis()}",
                                title = newTitle,
                                slug = newTitle.lowercase().replace(" ", "-"),
                                description = newSynopsis.ifBlank { "Exciting cinematic premiere on CineStream 2026." },
                                releaseYear = newYear.toIntOrNull() ?: 2026,
                                runtimeMinutes = 110,
                                contentRating = "PG-13",
                                rating = 4.8f,
                                genres = listOf(newGenre),
                                languages = listOf(newLanguage),
                                director = "CineStream Studio",
                                cast = listOf("Lead Actor", "Supporting Cast"),
                                videoUrl = newVideoUrl,
                                featured = false
                            )
                            viewModel.addMovie(movie)
                            showAddMovieDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CineCyan, contentColor = Color.Black)
                ) {
                    Text("Publish to Catalog")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMovieDialog = false }) {
                    Text("Cancel", color = CineTextSecondary)
                }
            },
            containerColor = CineSurfaceHigh
        )
    }

    // Delete Confirmation Dialog
    if (movieToDelete != null) {
        val target = movieToDelete!!
        AlertDialog(
            onDismissRequest = { movieToDelete = null },
            title = { Text("Remove from Catalog?", color = CineRed, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${target.title}'? This action will remove it from all user carousels.", color = CineTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMovie(target.id)
                        movieToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CineRed, contentColor = Color.White)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { movieToDelete = null }) {
                    Text("Cancel", color = CineTextSecondary)
                }
            },
            containerColor = CineSurfaceHigh
        )
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CineSurfaceVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = accentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = CineTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
