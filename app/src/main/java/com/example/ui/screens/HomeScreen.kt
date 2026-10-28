package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Movie
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.model.WatchHistoryItem
import com.example.ui.components.HeroBanner
import com.example.ui.components.MovieCard
import com.example.ui.components.MovieSectionRow
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineStreamViewModel
import com.example.ui.viewmodel.NavigationTab

@Composable
fun HomeScreen(
    viewModel: CineStreamViewModel,
    modifier: Modifier = Modifier
) {
    val movies by viewModel.movies.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val myListIds by viewModel.myListMovieIds.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()

    // Featured movies list for hero banner carousel
    val featuredMovies = remember(movies) {
        movies.filter { it.featured }.ifEmpty { movies.take(3) }
    }
    var currentHeroIndex by remember { mutableIntStateOf(0) }
    val currentHero = featuredMovies.getOrNull(currentHeroIndex % featuredMovies.size.coerceAtLeast(1))

    // Category filter pills
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Sci-Fi", "Bengali", "Action", "Animation", "Korean", "Hindi")

    val watchHistoryMap = remember(watchHistory) {
        watchHistory.associate { it.movieId to it.progressPercentage }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CineBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Top App Bar with CineStream Logo & Quick Actions
        item {
            HomeTopBar(
                user = currentUser,
                onSearchClick = { viewModel.selectTab(NavigationTab.EXPLORE) },
                onProfileClick = { viewModel.selectTab(NavigationTab.PROFILE) },
                onAdminClick = { viewModel.openAdminDashboard() },
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // Category Quick Filter Pills
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategoryFilter == category
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) CineCyan else CineSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CineCyan else CineBorder
                        ),
                        modifier = Modifier
                            .testTag("filter_chip_$category")
                            .clickable { selectedCategoryFilter = category }
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.Black else CineTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Hero Banner Premiere
        if (currentHero != null) {
            item {
                HeroBanner(
                    movie = currentHero,
                    isFavorite = myListIds.contains(currentHero.id),
                    onWatchClick = { viewModel.playMovie(currentHero) },
                    onDetailsClick = { viewModel.openMovieDetails(currentHero) },
                    onFavoriteToggle = { viewModel.toggleFavorite(currentHero.id) },
                    onNextHero = {
                        currentHeroIndex = (currentHeroIndex + 1) % featuredMovies.size
                    }
                )
            }
        }

        // Continue Watching Section (if history exists)
        if (watchHistory.isNotEmpty()) {
            item {
                ContinueWatchingSection(
                    historyItems = watchHistory,
                    movies = movies,
                    onResume = { historyItem ->
                        val movie = movies.find { it.id == historyItem.movieId }
                        if (movie != null) {
                            viewModel.playMovie(movie, startMs = historyItem.positionMs)
                        }
                    },
                    onRemove = { historyItem ->
                        viewModel.removeFromHistory(historyItem.movieId)
                    }
                )
            }
        }

        // Filtered or Grouped Sections
        if (selectedCategoryFilter != "All") {
            item {
                val filtered = movies.filter { movie ->
                    movie.genres.any { it.contains(selectedCategoryFilter, ignoreCase = true) } ||
                    movie.languages.any { it.contains(selectedCategoryFilter, ignoreCase = true) } ||
                    movie.categorySlugs.any { it.contains(selectedCategoryFilter, ignoreCase = true) }
                }
                MovieSectionRow(
                    title = "$selectedCategoryFilter Releases",
                    movies = filtered,
                    watchHistoryMap = watchHistoryMap,
                    onMovieClick = { viewModel.openMovieDetails(it) }
                )
            }
        } else {
            // Trending Movies
            item {
                val trending = movies.filter { it.categorySlugs.contains("trending") }
                MovieSectionRow(
                    title = viewModel.t("Trending Movies 2026", "জনপ্রিয় চলচ্চিত্র ২০২৬"),
                    movies = trending,
                    accentColor = CineRed,
                    watchHistoryMap = watchHistoryMap,
                    onMovieClick = { viewModel.openMovieDetails(it) }
                )
            }

            // Bengali Cinema Spotlight
            item {
                val bengali = movies.filter { it.languages.contains("Bengali") || it.categorySlugs.contains("bengali") }
                MovieSectionRow(
                    title = viewModel.t("Bengali Masterpieces", "বাংলা চলচ্চিত্রের শ্রেষ্ঠ সংগ্রহ"),
                    movies = bengali,
                    accentColor = CineCyan,
                    watchHistoryMap = watchHistoryMap,
                    onMovieClick = { viewModel.openMovieDetails(it) }
                )
            }

            // Sci-Fi & Cyberpunk 2026
            item {
                val sciFi = movies.filter { it.genres.any { g -> g.contains("Sci-Fi") || g.contains("Cyberpunk") } }
                MovieSectionRow(
                    title = viewModel.t("Sci-Fi & Cyberpunk Universe", "সাই-ফাই ও সাইবারপাঙ্ক ইউনিভার্স"),
                    movies = sciFi,
                    accentColor = CineElectricPurple,
                    watchHistoryMap = watchHistoryMap,
                    onMovieClick = { viewModel.openMovieDetails(it) }
                )
            }

            // Top-Rated Titles
            item {
                val topRated = movies.filter { it.rating >= 4.8f }
                MovieSectionRow(
                    title = viewModel.t("Top-Rated by Audience", "সর্বোচ্চ রেটেড সিনেমা"),
                    movies = topRated,
                    accentColor = CineAmber,
                    watchHistoryMap = watchHistoryMap,
                    onMovieClick = { viewModel.openMovieDetails(it) }
                )
            }

            // Hindi Cinema Blockbusters
            item {
                val hindi = movies.filter { it.languages.contains("Hindi") || it.categorySlugs.contains("hindi") }
                MovieSectionRow(
                    title = viewModel.t("Hindi Cinema Blockbusters", "হিন্দি সিনেমা ব্লকবাস্টার"),
                    movies = hindi,
                    accentColor = CineRed,
                    watchHistoryMap = watchHistoryMap,
                    onMovieClick = { viewModel.openMovieDetails(it) }
                )
            }

            // Animation & Anime
            item {
                val animation = movies.filter { it.genres.any { g -> g.contains("Animation") || g.contains("Fantasy") } }
                MovieSectionRow(
                    title = viewModel.t("Animation & Anime Gems", "অ্যানিমেশন ও অ্যানিমে"),
                    movies = animation,
                    accentColor = CineCyan,
                    watchHistoryMap = watchHistoryMap,
                    onMovieClick = { viewModel.openMovieDetails(it) }
                )
            }

            // Korean Thrillers & Series
            item {
                val korean = movies.filter { it.languages.contains("Korean") || it.categorySlugs.contains("korean") }
                MovieSectionRow(
                    title = viewModel.t("Korean Series & Thrillers", "কোরিয়ান ড্রামা ও থ্রিলার"),
                    movies = korean,
                    accentColor = CineViolet,
                    watchHistoryMap = watchHistoryMap,
                    onMovieClick = { viewModel.openMovieDetails(it) }
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    user: UserAccount,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & 2026 Edition badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Brush.linearGradient(listOf(CineCyan, CineViolet)),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CINE",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "STREAM",
                        color = CineCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "FUTURE STREAMING 2026",
                    color = CineTextMuted,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        // Actions: Admin icon, Search button, Profile avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (user.role == UserRole.ADMIN) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CineViolet.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CineViolet),
                    modifier = Modifier.clickable(onClick = onAdminClick)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Dashboard",
                            tint = CineElectricPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ADMIN",
                            color = CineElectricPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(CineSurfaceVariant, CircleShape)
                    .testTag("home_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = CineTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Surface(
                shape = CircleShape,
                color = CineSurfaceHigh,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CineCyan),
                modifier = Modifier
                    .size(40.dp)
                    .clickable(onClick = onProfileClick)
                    .testTag("home_profile_avatar")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.avatarEmoji,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ContinueWatchingSection(
    historyItems: List<WatchHistoryItem>,
    movies: List<Movie>,
    onResume: (WatchHistoryItem) -> Unit,
    onRemove: (WatchHistoryItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(18.dp)
                    .background(CineCyan, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Continue Watching",
                color = CineTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historyItems, key = { it.movieId }) { item ->
                val movie = movies.find { it.id == item.movieId }
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .clickable { onResume(item) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CineSurfaceVariant)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(115.dp)
                                .background(CineSurfaceHigh)
                        ) {
                            if (item.localBackdropResId != null) {
                                Image(
                                    painter = painterResource(id = item.localBackdropResId),
                                    contentDescription = item.movieTitle,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else if (item.localPosterResId != null) {
                                Image(
                                    painter = painterResource(id = item.localPosterResId),
                                    contentDescription = item.movieTitle,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }

                            // Play overlay icon
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                                    .align(Alignment.Center),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Resume",
                                    tint = CineCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Remove cross button
                            IconButton(
                                onClick = { onRemove(item) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Progress Bar
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

                        // Info
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = item.movieTitle,
                                color = CineTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val minutesLeft = if (item.durationMs > item.positionMs) {
                                ((item.durationMs - item.positionMs) / 60000).toInt()
                            } else 10
                            Text(
                                text = "$minutesLeft min remaining",
                                color = CineTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
