package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.CineStreamRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string resource matches CineStream`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CineStream", appName)
    }

    @Test
    fun `repository initializes with movies and default user`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = CineStreamRepository(context)

        val movies = repo.movies.value
        assertTrue("Catalog should contain movies", movies.isNotEmpty())

        val currentUser = repo.currentUser.value
        assertNotNull("Default user should be signed in", currentUser)
        assertEquals("cine_pilot", currentUser.userId)
    }

    @Test
    fun `favorite toggle works accurately`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = CineStreamRepository(context)

        val targetId = "movie_1"
        val initialStatus = repo.isFavorite(targetId)

        repo.toggleFavorite(targetId)
        assertEquals(!initialStatus, repo.isFavorite(targetId))

        repo.toggleFavorite(targetId)
        assertEquals(initialStatus, repo.isFavorite(targetId))
    }
}
