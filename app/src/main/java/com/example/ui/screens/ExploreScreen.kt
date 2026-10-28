package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MovieCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineStreamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: CineStreamViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val filteredMovies by viewModel.filteredMovies.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    val genres = listOf("All", "Sci-Fi", "Cyberpunk", "Action", "Animation", "Drama", "Bengali Cinema", "Hindi Cinema", "Thriller")
    val languages = listOf("All", "English", "Bengali", "Hindi", "Korean", "Japanese")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CineBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("explore_screen")
    ) {
        // Search Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input_field"),
                placeholder = {
                    Text(
                        text = viewModel.t("Search movies, actors, genres...", "সিনেমা, শিল্পী বা বিভাগ খুঁজুন..."),
                        color = CineTextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CineCyan
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = CineTextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CineCyan,
                    unfocusedBorderColor = CineBorder,
                    focusedContainerColor = CineSurfaceVariant,
                    unfocusedContainerColor = CineSurfaceVariant,
                    focusedTextColor = CineTextPrimary,
                    unfocusedTextColor = CineTextPrimary
                )
            )
        }

        // Recent searches chips
        if (searchQuery.isEmpty() && recentSearches.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.t("Recent Searches", "সাম্প্রতিক অনুসন্ধান"),
                    color = CineTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = viewModel.t("Clear", "মুছুন"),
                    color = CineCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.clearRecentSearches() }
                )
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentSearches) { query ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CineSurfaceHigh,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder),
                        modifier = Modifier.clickable { viewModel.setSearchQuery(query) }
                    ) {
                        Text(
                            text = query,
                            color = CineTextPrimary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // Genre filter row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre ->
                val isSelected = selectedGenre == genre
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) CineCyan else CineSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CineCyan else CineBorder),
                    modifier = Modifier.clickable { viewModel.selectGenre(genre) }
                ) {
                    Text(
                        text = genre,
                        color = if (isSelected) Color.Black else CineTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Language filter row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(languages) { lang ->
                val isSelected = selectedLanguage == lang
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) CineElectricPurple else CineSurfaceHigh,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CineElectricPurple else CineBorder),
                    modifier = Modifier.clickable { viewModel.selectLanguage(lang) }
                ) {
                    Text(
                        text = lang,
                        color = if (isSelected) Color.White else CineTextMuted,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid Results or Empty State
        if (filteredMovies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        tint = CineTextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = viewModel.t("No movies match your search", "কোনো সিনেমা পাওয়া যায়নি"),
                        color = CineTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = viewModel.t("Try searching another keyword or clearing filters", "অন্য কোনো শব্দ দিয়ে খুঁজুন বা ফিল্টার পরিবর্তন করুন"),
                        color = CineTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.selectGenre("All")
                            viewModel.selectLanguage("All")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CineCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reset All Filters", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 110.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMovies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        cardWidth = 110.dp,
                        cardHeight = 165.dp,
                        onClick = { viewModel.openMovieDetails(movie) }
                    )
                }
            }
        }
    }
}
