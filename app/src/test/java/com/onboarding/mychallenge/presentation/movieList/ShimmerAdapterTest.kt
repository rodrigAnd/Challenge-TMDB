package com.onboarding.mychallenge.presentation.movieList

import org.junit.Assert.assertEquals
import org.junit.Test

class ShimmerAdapterTest {
    @Test
    fun `getItemCount should return default item count`() {
        // Arrange
        val adapter = ShimmerAdapter()

        // Act
        val itemCount = adapter.itemCount

        // Assert
        assertEquals(5, itemCount)
    }

    @Test
    fun `getItemCount should return custom item count`() {
        // Arrange
        val customCount = 10
        val adapter = ShimmerAdapter(customCount)

        // Act
        val itemCount = adapter.itemCount

        // Assert
        assertEquals(customCount, itemCount)
    }

    @Test
    fun `getItemCount should return zero when itemCount is zero`() {
        // Arrange
        val adapter = ShimmerAdapter(0)

        // Act
        val itemCount = adapter.itemCount

        // Assert
        assertEquals(0, itemCount)
    }
}
