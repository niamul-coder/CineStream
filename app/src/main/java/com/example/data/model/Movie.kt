package com.example.data.model

data class SubtitleTrack(
    val languageCode: String,
    val languageName: String,
    val isDefault: Boolean = false
)

data class Episode(
    val id: String,
    val episodeNumber: Int,
    val title: String,
    val durationMinutes: Int,
    val synopsis: String,
    val videoUrl: String,
    val thumbnailResId: Int? = null,
    val thumbnailUrl: String = ""
)

data class Season(
    val seasonNumber: Int,
    val title: String,
    val episodes: List<Episode>
)

data class Movie(
    val id: String,
    val title: String,
    val originalTitle: String? = null,
    val slug: String,
    val description: String,
    val posterUrl: String = "",
    val backdropUrl: String = "",
    val localPosterResId: Int? = null,
    val localBackdropResId: Int? = null,
    val releaseYear: Int,
    val runtimeMinutes: Int,
    val contentRating: String, // "PG-13", "TV-MA", "PG", "U/A"
    val rating: Float, // e.g. 4.9f
    val ratingCount: String = "12.4K",
    val genres: List<String>,
    val languages: List<String>,
    val director: String,
    val cast: List<String>,
    val videoUrl: String,
    val downloadUrl: String = "",
    val availableQualities: List<String> = listOf("4K UHD", "1080p FHD", "720p HD", "480p SD"),
    val subtitles: List<SubtitleTrack> = listOf(
        SubtitleTrack("en", "English", true),
        SubtitleTrack("bn", "বাংলা (Bengali)"),
        SubtitleTrack("es", "Español")
    ),
    val isSeries: Boolean = false,
    val seasons: List<Season>? = null,
    val featured: Boolean = false,
    val categorySlugs: List<String> = emptyList(),
    val downloadAllowed: Boolean = true,
    val fileSizeMb: Long = 1450L,
    val published: Boolean = true
)
