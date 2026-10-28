package com.example.data.model

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED
}

data class DownloadItem(
    val id: String,
    val movieId: String,
    val movieTitle: String,
    val posterUrl: String = "",
    val localPosterResId: Int? = null,
    val quality: String = "1080p FHD",
    val fileSizeMb: Long = 1200L,
    val progress: Float = 0f, // 0.0 to 1.0
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 1200L * 1024L * 1024L,
    val downloadSpeedMbps: Float = 14.5f,
    val status: DownloadStatus = DownloadStatus.DOWNLOADING,
    val localFilePath: String = "",
    val addedTime: Long = System.currentTimeMillis()
)

data class WatchHistoryItem(
    val movieId: String,
    val movieTitle: String,
    val posterUrl: String = "",
    val backdropUrl: String = "",
    val localPosterResId: Int? = null,
    val localBackdropResId: Int? = null,
    val positionMs: Long,
    val durationMs: Long,
    val lastWatchedTime: Long = System.currentTimeMillis()
) {
    val progressPercentage: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}
