package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.NavigationTab

data class NavItem(
    val tab: NavigationTab,
    val labelEn: String,
    val labelBn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun CineBottomNav(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    isBengali: Boolean,
    downloadCount: Int = 0,
    watchlistCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(NavigationTab.HOME, "Home", "হোম", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem(NavigationTab.EXPLORE, "Explore", "অনুসন্ধান", Icons.Filled.Explore, Icons.Outlined.Explore),
        NavItem(NavigationTab.DOWNLOADS, "Downloads", "ডাউনলোড", Icons.Filled.Download, Icons.Outlined.Download, badgeCount = downloadCount),
        NavItem(NavigationTab.MY_LIST, "My List", "তালিকা", Icons.Filled.Bookmarks, Icons.Outlined.Bookmarks, badgeCount = watchlistCount),
        NavItem(NavigationTab.PROFILE, "Profile", "প্রোফাইল", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = CineSurface.copy(alpha = 0.96f),
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder.copy(alpha = 0.5f))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(68.dp)
        ) {
            items.forEach { item ->
                val selected = selectedTab == item.tab
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(item.tab) },
                    modifier = Modifier.testTag("nav_tab_${item.tab.name.lowercase()}"),
                    icon = {
                        BadgedBox(
                            badge = {
                                if (item.badgeCount > 0) {
                                    Badge(
                                        containerColor = CineCyan,
                                        contentColor = Color.Black
                                    ) {
                                        Text(
                                            text = "${item.badgeCount}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.labelEn,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = if (isBengali) item.labelBn else item.labelEn,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) CineCyan else CineTextSecondary
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CineCyan,
                        unselectedIconColor = CineTextSecondary,
                        selectedTextColor = CineCyan,
                        unselectedTextColor = CineTextSecondary,
                        indicatorColor = CineSurfaceHigh
                    )
                )
            }
        }
    }
}
