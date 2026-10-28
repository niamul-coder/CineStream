package com.example.data.model

enum class UserRole {
    USER,
    ADMIN
}

data class UserAccount(
    val userId: String, // e.g. "cine_pilot"
    val displayName: String,
    val email: String,
    val avatarEmoji: String = "🎬",
    val role: UserRole = UserRole.USER,
    val isVip: Boolean = true,
    val preferredLanguage: String = "English", // "English" or "বাংলা"
    val darkTheme: Boolean = true,
    val wifiOnlyDownload: Boolean = false,
    val defaultQuality: String = "1080p FHD"
)
