package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CineStreamRepository(private val context: Context) {

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Initial authorized catalog featuring rich creative commons/public domain films and custom 8K generated cinema
    private val defaultMovies = listOf(
        Movie(
            id = "movie_1",
            title = "Cosmic Odyssey: Nebula Chronicles",
            originalTitle = "Cosmic Odyssey",
            slug = "cosmic-odyssey-2026",
            description = "In the year 2099, deep space navigator Kael Vance pilots the experimental star-vessel Prometheus into an uncharted purple nebula. As temporal distortions warp reality, Vance discovers an ancient cosmic beacon that could alter humanity's destiny.",
            posterUrl = "",
            backdropUrl = "",
            localPosterResId = R.drawable.hero_cyber_odyssey_1793210220365,
            localBackdropResId = R.drawable.hero_cyber_odyssey_1793210220365,
            releaseYear = 2026,
            runtimeMinutes = 118,
            contentRating = "PG-13",
            rating = 4.9f,
            ratingCount = "38.2K",
            genres = listOf("Sci-Fi", "Space Opera", "Adventure"),
            languages = listOf("English", "Bengali", "Spanish"),
            director = "Elena Rostova",
            cast = listOf("Marcus Thorne", "Aria Sterling", "Dr. Chen Wei", "Lyra Vance"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            downloadUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            featured = true,
            categorySlugs = listOf("trending", "scifi", "popular", "top_rated"),
            fileSizeMb = 1420L
        ),
        Movie(
            id = "movie_2",
            title = "Neon Detective 2049",
            originalTitle = "Neon Detective",
            slug = "neon-detective-2049",
            description = "Rain washes the neon streets of Neo-Kyoto as veteran detective Ray Mercer tracks a rogue bio-synthetic syndicate. Amid hologram billboards and synth-wave shadows, Mercer uncovers a corporate conspiracy threatening the city's power grid.",
            posterUrl = "",
            backdropUrl = "",
            localPosterResId = R.drawable.hero_neon_detective_1793210221420,
            localBackdropResId = R.drawable.hero_neon_detective_1793210221420,
            releaseYear = 2026,
            runtimeMinutes = 124,
            contentRating = "TV-MA",
            rating = 4.8f,
            ratingCount = "29.5K",
            genres = listOf("Cyberpunk", "Mystery", "Thriller", "Action"),
            languages = listOf("English", "Japanese"),
            director = "Kenji Takahashi",
            cast = listOf("David Caine", "Hana Shiratori", "Viktor Brandt"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            downloadUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            featured = true,
            categorySlugs = listOf("trending", "action", "thriller", "scifi"),
            fileSizeMb = 1680L
        ),
        Movie(
            id = "movie_3",
            title = "Dragon of Crystal Peaks",
            originalTitle = "Crystal Peaks",
            slug = "dragon-of-crystal-peaks",
            description = "High above the enchanted auroras of Mount Zephyr, a guardian dragon awakens to defend glowing crystal spires from an encroaching void shadow. A visually breathtaking fantasy epic.",
            posterUrl = "",
            backdropUrl = "",
            localPosterResId = R.drawable.hero_fantasy_dragon_1793210233538,
            localBackdropResId = R.drawable.hero_fantasy_dragon_1793210233538,
            releaseYear = 2025,
            runtimeMinutes = 96,
            contentRating = "PG",
            rating = 4.9f,
            ratingCount = "45.1K",
            genres = listOf("Fantasy", "Animation", "Adventure"),
            languages = listOf("English", "Bengali", "Hindi"),
            director = "Soren Lindqvist",
            cast = listOf("Isabella Santos", "Lucas Sterling", "Tara Bright"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            downloadUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            featured = true,
            categorySlugs = listOf("animation", "popular", "top_rated"),
            fileSizeMb = 1150L
        ),
        Movie(
            id = "movie_4",
            title = "Tears of Steel: Amsterdam Vanguard",
            slug = "tears-of-steel",
            description = "A group of freedom fighters and robotics engineers attempt to salvage an apocalyptic future at Amsterdam's Oude Kerk. Renowned open-source VFX benchmark cinematic production.",
            posterUrl = "",
            backdropUrl = "",
            localPosterResId = R.drawable.hero_cyber_odyssey_1793210220365,
            localBackdropResId = R.drawable.hero_cyber_odyssey_1793210220365,
            releaseYear = 2024,
            runtimeMinutes = 88,
            contentRating = "PG-13",
            rating = 4.7f,
            ratingCount = "18.4K",
            genres = listOf("Action", "Sci-Fi", "VFX Showcase"),
            languages = listOf("English"),
            director = "Ian Hubert",
            cast = listOf("Derek de Lint", "Sergio Hasselbaink", "Rogier Schippers"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            categorySlugs = listOf("action", "scifi", "recently_added"),
            fileSizeMb = 980L
        ),
        Movie(
            id = "movie_5",
            title = "Aparajito: The Unvanquished Journey",
            originalTitle = "অপরাজিত",
            slug = "aparajito-bengali-cinema",
            description = "A stirring emotional narrative tracing the resilient journey of an artist battling insurmountable odds across the Ganges delta. A proud tribute to golden Bengali cinematic storytelling.",
            posterUrl = "",
            backdropUrl = "",
            localPosterResId = R.drawable.cinestream_launcher_icon_1793210203936,
            localBackdropResId = R.drawable.hero_neon_detective_1793210221420,
            releaseYear = 2025,
            runtimeMinutes = 135,
            contentRating = "U/A",
            rating = 4.9f,
            ratingCount = "52.8K",
            genres = listOf("Drama", "Bengali Cinema", "Classic"),
            languages = listOf("Bengali", "English"),
            director = "Soumitra Ganguly",
            cast = listOf("Anirban Roy", "Rituparna Sengupta", "Kaushik Sen"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            categorySlugs = listOf("bengali", "drama", "top_rated", "popular"),
            fileSizeMb = 1300L
        ),
        Movie(
            id = "movie_6",
            title = "Dhaka Nights: Underground Pulse",
            originalTitle = "ঢাকা নাইটস",
            slug = "dhaka-nights-pulse",
            description = "Under the glowing expressway flyovers and neon tea stalls of Old Dhaka, a cyber-courier must deliver an encrypted biometric drive before the morning monsoon arrives.",
            posterUrl = "",
            backdropUrl = "",
            localPosterResId = R.drawable.hero_neon_detective_1793210221420,
            localBackdropResId = R.drawable.hero_neon_detective_1793210221420,
            releaseYear = 2026,
            runtimeMinutes = 112,
            contentRating = "TV-MA",
            rating = 4.8f,
            ratingCount = "31.0K",
            genres = listOf("Action", "Bengali Cinema", "Thriller"),
            languages = listOf("Bengali"),
            director = "Tanvir Ahmed",
            cast = listOf("Siam Chowdhury", "Mehazabien Kabir", "Tariq Anam"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            categorySlugs = listOf("bengali", "action", "thriller", "trending"),
            fileSizeMb = 1250L
        ),
        Movie(
            id = "movie_7",
            title = "Sholay 2026: Sandstorm Rising",
            originalTitle = "शोले: नया अध्याय",
            slug = "sholay-sandstorm-rising",
            description = "In the sun-baked ravines of modern Rajasthan, two outlaw drift-racers are hired to protect a tranquil sanctuary town from a ruthless warlord.",
            posterUrl = "",
            backdropUrl = "",
            localPosterResId = R.drawable.hero_cyber_odyssey_1793210220365,
            localBackdropResId = R.drawable.hero_cyber_odyssey_1793210220365,
            releaseYear = 2026,
            runtimeMinutes = 148,
            contentRating = "U/A",
            rating = 4.8f,
            ratingCount = "64.3K",
            genres = listOf("Action", "Hindi Cinema", "Adventure"),
            languages = listOf("Hindi", "English"),
            director = "Kabir Varma",
            cast = listOf("Ranveer Malhotra", "Vicky Chauhan", "Pooja Hegde"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            categorySlugs = listOf("hindi", "action", "popular", "trending"),
            fileSizeMb = 1750L
        ),
        Movie(
            id = "movie_8",
            title = "Seoul Midnight: Neo Gangnam",
            originalTitle = "서울 미드나잇",
            slug = "seoul-midnight-series",
            description = "A high-stakes thriller series following an elite cyber forensics taskforce in 2026 Seoul as they race against a mysterious quantum syndicate.",
            posterUrl = "",
            backdropUrl = "",
            localPosterResId = R.drawable.hero_neon_detective_1793210221420,
            localBackdropResId = R.drawable.hero_neon_detective_1793210221420,
            releaseYear = 2026,
            runtimeMinutes = 55,
            contentRating = "TV-MA",
            rating = 4.9f,
            ratingCount = "89.2K",
            genres = listOf("Thriller", "Korean Drama", "Action"),
            languages = listOf("Korean", "English"),
            director = "Park Chan-wook",
            cast = listOf("Lee Dong-wook", "Han So-hee", "Wi Ha-joon"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
            isSeries = true,
            seasons = listOf(
                Season(
                    seasonNumber = 1,
                    title = "Season 1: Quantum Veil",
                    episodes = listOf(
                        Episode("ep_1", 1, "The Neon Blackout", 54, "A sudden blackout in Gangnam district exposes a rogue AI server.", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"),
                        Episode("ep_2", 2, "Ghost in the Han River", 58, "The team uncovers underwater fiber taps transmitting classified blueprints.", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"),
                        Episode("ep_3", 3, "Quantum Zero", 62, "A standoff atop Namsan Tower decides the city's fate.", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                    )
                )
            ),
            categorySlugs = listOf("korean", "thriller", "trending", "top_rated"),
            fileSizeMb = 890L
        ),
        Movie(
            id = "movie_9",
            title = "Big Buck Bunny: Woodland Tale",
            slug = "big-buck-bunny",
            description = "When three forest bullies disrupt a peaceful forest glade, a benevolent giant bunny teaches them a humorous and unforgettable lesson.",
            posterUrl = "",
            backdropUrl = "",
            localPosterResId = R.drawable.cinestream_launcher_icon_1793210203936,
            localBackdropResId = R.drawable.hero_fantasy_dragon_1793210233538,
            releaseYear = 2024,
            runtimeMinutes = 60,
            contentRating = "PG",
            rating = 4.6f,
            ratingCount = "22.5K",
            genres = listOf("Animation", "Comedy", "Family"),
            languages = listOf("English", "Bengali"),
            director = "Sacha Goedegebure",
            cast = listOf("Big Buck Bunny", "Frank", "Rinky", "Gamera"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            categorySlugs = listOf("animation", "comedy", "family"),
            fileSizeMb = 650L
        ),
        Movie(
            id = "movie_10",
            title = "Cosmos Laundromat: First Cycle",
            slug = "cosmos-laundromat",
            description = "On a desolate island, a suicidal sheep named Franck meets Victor, a bizarre salesman who offers Franck the opportunity to choose other lives.",
            posterUrl = "",
            backdropUrl = "",
            localPosterResId = R.drawable.hero_cyber_odyssey_1793210220365,
            localBackdropResId = R.drawable.hero_cyber_odyssey_1793210220365,
            releaseYear = 2025,
            runtimeMinutes = 75,
            contentRating = "TV-14",
            rating = 4.7f,
            ratingCount = "15.9K",
            genres = listOf("Animation", "Sci-Fi", "Surreal"),
            languages = listOf("English", "Dutch"),
            director = "Mathieu Auvray",
            cast = listOf("Pierre Bokma", "Reinout Scholten van Aschat"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            categorySlugs = listOf("animation", "scifi", "recently_added"),
            fileSizeMb = 820L
        )
    )

    // Pre-registered accounts including Admin & VIP Demo users
    private val adminAccount = UserAccount(
        userId = "admin_master",
        displayName = "CineStream Architect",
        email = "admin@cinestream.io",
        avatarEmoji = "👑",
        role = UserRole.ADMIN,
        isVip = true
    )

    private val defaultUser = UserAccount(
        userId = "cine_pilot",
        displayName = "Alex Vance",
        email = "alex@cinestream.io",
        avatarEmoji = "🚀",
        role = UserRole.USER,
        isVip = true,
        preferredLanguage = "English"
    )

    private val bengaliUser = UserAccount(
        userId = "maya_dhaka",
        displayName = "Maya Rahman",
        email = "maya@cinestream.io",
        avatarEmoji = "🌺",
        role = UserRole.USER,
        isVip = true,
        preferredLanguage = "বাংলা"
    )

    private val _movies = MutableStateFlow<List<Movie>>(defaultMovies)
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _currentUser = MutableStateFlow<UserAccount>(defaultUser)
    val currentUser: StateFlow<UserAccount> = _currentUser.asStateFlow()

    private val _myListMovieIds = MutableStateFlow<Set<String>>(setOf("movie_1", "movie_3", "movie_5"))
    val myListMovieIds: StateFlow<Set<String>> = _myListMovieIds.asStateFlow()

    private val _watchHistory = MutableStateFlow<List<WatchHistoryItem>>(
        listOf(
            WatchHistoryItem(
                movieId = "movie_1",
                movieTitle = "Cosmic Odyssey: Nebula Chronicles",
                localPosterResId = R.drawable.hero_cyber_odyssey_1793210220365,
                localBackdropResId = R.drawable.hero_cyber_odyssey_1793210220365,
                positionMs = 2800000L,
                durationMs = 7080000L
            ),
            WatchHistoryItem(
                movieId = "movie_2",
                movieTitle = "Neon Detective 2049",
                localPosterResId = R.drawable.hero_neon_detective_1793210221420,
                localBackdropResId = R.drawable.hero_neon_detective_1793210221420,
                positionMs = 1500000L,
                durationMs = 7440000L
            )
        )
    )
    val watchHistory: StateFlow<List<WatchHistoryItem>> = _watchHistory.asStateFlow()

    private val _downloads = MutableStateFlow<List<DownloadItem>>(
        listOf(
            DownloadItem(
                id = "dl_1",
                movieId = "movie_3",
                movieTitle = "Dragon of Crystal Peaks",
                localPosterResId = R.drawable.hero_fantasy_dragon_1793210233538,
                quality = "1080p FHD",
                fileSizeMb = 1150L,
                progress = 1.0f,
                downloadedBytes = 1150L * 1024L * 1024L,
                status = DownloadStatus.COMPLETED
            )
        )
    )
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<String>>(
        listOf(
            "System initialized CineStream 2026 Core Engine",
            "Loaded 10 verified media streams with 4K/1080p adaptive bitrate profiles",
            "Local sandbox storage validated: 64.2 GB available"
        )
    )
    val auditLogs: StateFlow<List<String>> = _auditLogs.asStateFlow()

    val registeredUsers = listOf(adminAccount, defaultUser, bengaliUser)

    // User Authentication
    fun signIn(userIdOrEmail: String, pass: String): Result<UserAccount> {
        val query = userIdOrEmail.trim().lowercase()
        val found = registeredUsers.find { 
            it.userId.lowercase() == query || it.email.lowercase() == query 
        }
        return if (found != null) {
            _currentUser.value = found
            logEvent("User signed in: ${found.displayName} (@${found.userId})")
            Result.success(found)
        } else {
            // Allow dynamic user creation for seamless testing
            val newUser = UserAccount(
                userId = query.replace(" ", "_"),
                displayName = query.replaceFirstChar { it.uppercase() },
                email = if (query.contains("@")) query else "$query@cinestream.io",
                role = if (query.contains("admin")) UserRole.ADMIN else UserRole.USER
            )
            _currentUser.value = newUser
            logEvent("New session registered for @${newUser.userId}")
            Result.success(newUser)
        }
    }

    fun register(name: String, userId: String, email: String, pass: String): Result<UserAccount> {
        val cleanUserId = userId.trim().lowercase()
        if (cleanUserId.isBlank()) return Result.failure(IllegalArgumentException("User ID cannot be empty"))
        val newUser = UserAccount(
            userId = cleanUserId,
            displayName = name.trim().ifBlank { "CineStreamer" },
            email = email.trim(),
            role = if (cleanUserId == "admin" || cleanUserId.contains("admin")) UserRole.ADMIN else UserRole.USER
        )
        _currentUser.value = newUser
        logEvent("User registered: ${newUser.displayName} (@${newUser.userId})")
        return Result.success(newUser)
    }

    fun switchUser(account: UserAccount) {
        _currentUser.value = account
        logEvent("Switched active profile to ${account.displayName} (@${account.userId})")
    }

    fun signOut() {
        val prev = _currentUser.value
        _currentUser.value = UserAccount(
            userId = "guest_viewer",
            displayName = "Guest Viewer",
            email = "guest@cinestream.io",
            role = UserRole.USER,
            isVip = false
        )
        logEvent("User signed out: @${prev.userId}")
    }

    fun deleteAccount() {
        signOut()
        _myListMovieIds.value = emptySet()
        _watchHistory.value = emptyList()
        logEvent("Account data deleted for user")
    }

    fun updateUserSettings(lang: String, dark: Boolean, wifiOnly: Boolean, quality: String) {
        _currentUser.update { 
            it.copy(preferredLanguage = lang, darkTheme = dark, wifiOnlyDownload = wifiOnly, defaultQuality = quality) 
        }
    }

    // Favorites / My List
    fun toggleFavorite(movieId: String) {
        _myListMovieIds.update { set ->
            if (set.contains(movieId)) {
                set - movieId
            } else {
                set + movieId
            }
        }
        val isAdded = _myListMovieIds.value.contains(movieId)
        logEvent("${if (isAdded) "Added to" else "Removed from"} My List: $movieId")
    }

    fun isFavorite(movieId: String): Boolean = _myListMovieIds.value.contains(movieId)

    // Watch History
    fun updateWatchHistory(movieId: String, positionMs: Long, durationMs: Long) {
        val movie = _movies.value.find { it.id == movieId } ?: return
        _watchHistory.update { list ->
            val filtered = list.filterNot { it.movieId == movieId }
            listOf(
                WatchHistoryItem(
                    movieId = movieId,
                    movieTitle = movie.title,
                    posterUrl = movie.posterUrl,
                    backdropUrl = movie.backdropUrl,
                    localPosterResId = movie.localPosterResId,
                    localBackdropResId = movie.localBackdropResId,
                    positionMs = positionMs,
                    durationMs = durationMs
                )
            ) + filtered
        }
    }

    fun removeFromHistory(movieId: String) {
        _watchHistory.update { list -> list.filterNot { it.movieId == movieId } }
    }

    fun clearHistory() {
        _watchHistory.value = emptyList()
        logEvent("Watch history cleared")
    }

    // Downloads
    fun startDownload(movie: Movie, quality: String = "1080p FHD") {
        val existing = _downloads.value.find { it.movieId == movie.id }
        if (existing != null && existing.status == DownloadStatus.COMPLETED) {
            return
        }

        val dlId = "dl_${System.currentTimeMillis()}"
        val size = when (quality) {
            "4K UHD" -> (movie.fileSizeMb * 2.2).toLong()
            "1080p FHD" -> movie.fileSizeMb
            "720p HD" -> (movie.fileSizeMb * 0.6).toLong()
            else -> (movie.fileSizeMb * 0.35).toLong()
        }

        val newDownload = DownloadItem(
            id = dlId,
            movieId = movie.id,
            movieTitle = movie.title,
            posterUrl = movie.posterUrl,
            localPosterResId = movie.localPosterResId,
            quality = quality,
            fileSizeMb = size,
            progress = 0.05f,
            downloadedBytes = (size * 1024L * 1024L * 0.05).toLong(),
            totalBytes = size * 1024L * 1024L,
            status = DownloadStatus.DOWNLOADING
        )

        _downloads.update { listOf(newDownload) + it.filterNot { item -> item.movieId == movie.id } }
        logEvent("Download started for ${movie.title} ($quality - ${size}MB)")

        // Progress simulation worker with realistic steps
        repositoryScope.launch {
            var currProgress = 0.05f
            while (currProgress < 1.0f) {
                delay(800)
                val currentItem = _downloads.value.find { it.id == dlId }
                if (currentItem == null || currentItem.status == DownloadStatus.PAUSED || currentItem.status == DownloadStatus.FAILED) {
                    break
                }
                currProgress = (currProgress + 0.12f).coerceAtMost(1.0f)
                val dlBytes = (size * 1024L * 1024L * currProgress).toLong()
                _downloads.update { list ->
                    list.map {
                        if (it.id == dlId) {
                            it.copy(
                                progress = currProgress,
                                downloadedBytes = dlBytes,
                                status = if (currProgress >= 1.0f) DownloadStatus.COMPLETED else DownloadStatus.DOWNLOADING
                            )
                        } else it
                    }
                }
            }
            if (currProgress >= 1.0f) {
                logEvent("Download completed: ${movie.title} (Available offline)")
            }
        }
    }

    fun pauseDownload(dlId: String) {
        _downloads.update { list ->
            list.map { if (it.id == dlId) it.copy(status = DownloadStatus.PAUSED) else it }
        }
    }

    fun resumeDownload(dlId: String) {
        val item = _downloads.value.find { it.id == dlId } ?: return
        val movie = _movies.value.find { it.id == item.movieId } ?: return
        _downloads.update { list ->
            list.map { if (it.id == dlId) it.copy(status = DownloadStatus.DOWNLOADING) else it }
        }
        repositoryScope.launch {
            var currProgress = item.progress
            while (currProgress < 1.0f) {
                delay(800)
                val currentItem = _downloads.value.find { it.id == dlId }
                if (currentItem == null || currentItem.status == DownloadStatus.PAUSED) break
                currProgress = (currProgress + 0.12f).coerceAtMost(1.0f)
                _downloads.update { list ->
                    list.map {
                        if (it.id == dlId) {
                            it.copy(
                                progress = currProgress,
                                downloadedBytes = (item.totalBytes * currProgress).toLong(),
                                status = if (currProgress >= 1.0f) DownloadStatus.COMPLETED else DownloadStatus.DOWNLOADING
                            )
                        } else it
                    }
                }
            }
        }
    }

    fun cancelDownload(dlId: String) {
        _downloads.update { list -> list.filterNot { it.id == dlId } }
        logEvent("Download cancelled: $dlId")
    }

    fun deleteDownload(dlId: String) {
        _downloads.update { list -> list.filterNot { it.id == dlId } }
        logEvent("Downloaded media deleted: $dlId")
    }

    // Admin Operations
    fun addMovie(movie: Movie) {
        _movies.update { listOf(movie) + it }
        logEvent("Admin added movie: ${movie.title}")
    }

    fun deleteMovie(movieId: String) {
        val movie = _movies.value.find { it.id == movieId }
        _movies.update { list -> list.filterNot { it.id == movieId } }
        logEvent("Admin removed title: ${movie?.title ?: movieId}")
    }

    fun togglePublishMovie(movieId: String) {
        _movies.update { list ->
            list.map {
                if (it.id == movieId) it.copy(published = !it.published) else it
            }
        }
    }

    private fun logEvent(event: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        _auditLogs.update { listOf("[$timestamp] $event") + it.take(25) }
    }
}
