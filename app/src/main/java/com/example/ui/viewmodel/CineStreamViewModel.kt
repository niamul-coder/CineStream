package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.CineStreamRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class NavigationTab {
    HOME,
    EXPLORE,
    DOWNLOADS,
    MY_LIST,
    PROFILE
}

class CineStreamViewModel(application: Application) : AndroidViewModel(application) {

    val repository = CineStreamRepository(application)

    // Navigation and screen routing
    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    private val _playingMovie = MutableStateFlow<Movie?>(null)
    val playingMovie: StateFlow<Movie?> = _playingMovie.asStateFlow()

    private val _playingEpisode = MutableStateFlow<Episode?>(null)
    val playingEpisode: StateFlow<Episode?> = _playingEpisode.asStateFlow()

    private val _playerStartPositionMs = MutableStateFlow(0L)
    val playerStartPositionMs: StateFlow<Long> = _playerStartPositionMs.asStateFlow()

    private val _showAdminDashboard = MutableStateFlow(false)
    val showAdminDashboard: StateFlow<Boolean> = _showAdminDashboard.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    // Explore / Search filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("All")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _recentSearches = MutableStateFlow(listOf("Cyberpunk", "Bengali Cinema", "Nebula", "Anime", "Action"))
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    // Expose repository data
    val movies: StateFlow<List<Movie>> = repository.movies
    val currentUser: StateFlow<UserAccount> = repository.currentUser
    val myListMovieIds: StateFlow<Set<String>> = repository.myListMovieIds
    val watchHistory: StateFlow<List<WatchHistoryItem>> = repository.watchHistory
    val downloads: StateFlow<List<DownloadItem>> = repository.downloads
    val auditLogs: StateFlow<List<String>> = repository.auditLogs

    // Filtered movies for Explore screen
    val filteredMovies: StateFlow<List<Movie>> = combine(
        movies,
        searchQuery,
        selectedGenre,
        selectedLanguage
    ) { allMovies, query, genre, lang ->
        allMovies.filter { movie ->
            val matchesQuery = query.isBlank() ||
                movie.title.contains(query, ignoreCase = true) ||
                movie.description.contains(query, ignoreCase = true) ||
                movie.director.contains(query, ignoreCase = true) ||
                movie.cast.any { it.contains(query, ignoreCase = true) }

            val matchesGenre = genre == "All" || movie.genres.any { it.equals(genre, ignoreCase = true) }
            val matchesLang = lang == "All" || movie.languages.any { it.equals(lang, ignoreCase = true) }

            matchesQuery && matchesGenre && matchesLang
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Language localization helper
    val isBengali: Boolean
        get() = currentUser.value.preferredLanguage == "বাংলা"

    fun t(en: String, bn: String): String = if (isBengali) bn else en

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
        _selectedMovie.value = null
        _showAdminDashboard.value = false
    }

    fun openMovieDetails(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun closeMovieDetails() {
        _selectedMovie.value = null
    }

    fun playMovie(movie: Movie, episode: Episode? = null, startMs: Long = 0L) {
        _playingMovie.value = movie
        _playingEpisode.value = episode
        _playerStartPositionMs.value = startMs
    }

    fun closePlayer() {
        _playingMovie.value = null
        _playingEpisode.value = null
    }

    fun openAdminDashboard() {
        _showAdminDashboard.value = true
    }

    fun closeAdminDashboard() {
        _showAdminDashboard.value = false
    }

    fun openAuthDialog() {
        _showAuthDialog.value = true
    }

    fun closeAuthDialog() {
        _showAuthDialog.value = false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank() && !_recentSearches.value.contains(query)) {
            _recentSearches.update { (listOf(query) + it).take(8) }
        }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun selectGenre(genre: String) {
        _selectedGenre.value = genre
    }

    fun selectLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    // Repository actions
    fun toggleFavorite(movieId: String) = repository.toggleFavorite(movieId)

    fun startDownload(movie: Movie, quality: String = "1080p FHD") = repository.startDownload(movie, quality)

    fun pauseDownload(dlId: String) = repository.pauseDownload(dlId)

    fun resumeDownload(dlId: String) = repository.resumeDownload(dlId)

    fun cancelDownload(dlId: String) = repository.cancelDownload(dlId)

    fun deleteDownload(dlId: String) = repository.deleteDownload(dlId)

    fun updateWatchProgress(movieId: String, posMs: Long, durationMs: Long) =
        repository.updateWatchHistory(movieId, posMs, durationMs)

    fun removeFromHistory(movieId: String) = repository.removeFromHistory(movieId)

    fun clearWatchHistory() = repository.clearHistory()

    fun switchAccount(account: UserAccount) = repository.switchUser(account)

    fun signOut() = repository.signOut()

    fun deleteAccount() = repository.deleteAccount()

    fun signIn(idOrEmail: String, pass: String): Result<UserAccount> = repository.signIn(idOrEmail, pass)

    fun register(name: String, userId: String, email: String, pass: String): Result<UserAccount> =
        repository.register(name, userId, email, pass)

    fun updateSettings(lang: String, dark: Boolean, wifiOnly: Boolean, quality: String) =
        repository.updateUserSettings(lang, dark, wifiOnly, quality)

    fun addMovie(movie: Movie) = repository.addMovie(movie)

    fun deleteMovie(movieId: String) = repository.deleteMovie(movieId)

    fun togglePublishMovie(movieId: String) = repository.togglePublishMovie(movieId)
}
