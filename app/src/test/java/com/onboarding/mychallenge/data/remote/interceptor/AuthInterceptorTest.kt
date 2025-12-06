package com.onboarding.mychallenge.data.remote.interceptor

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var authInterceptor: AuthInterceptor
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        authInterceptor = AuthInterceptor("test-bearer-token")
        client =
            OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `intercept should add Authorization header with Bearer token`() {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        val request =
            Request.Builder()
                .url(mockWebServer.url("/test"))
                .build()

        // Act
        val response = client.newCall(request).execute()

        // Assert
        assertEquals(200, response.code)
        val sentRequest = mockWebServer.takeRequest()
        assertEquals("Bearer test-bearer-token", sentRequest.getHeader("Authorization"))
        assertEquals("application/json", sentRequest.getHeader("accept"))
    }

    @Test
    fun `intercept should preserve original request headers`() {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        val request =
            Request.Builder()
                .url(mockWebServer.url("/test"))
                .header("Custom-Header", "custom-value")
                .build()

        // Act
        client.newCall(request).execute()

        // Assert
        val sentRequest = mockWebServer.takeRequest()
        assertEquals("custom-value", sentRequest.getHeader("Custom-Header"))
        assertEquals("Bearer test-bearer-token", sentRequest.getHeader("Authorization"))
    }

    @Test
    fun `intercept should proceed with modified request`() {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
        val request =
            Request.Builder()
                .url(mockWebServer.url("/test"))
                .build()

        // Act
        val response = client.newCall(request).execute()

        // Assert
        assertNotNull(response)
        assertEquals(200, response.code)
        assertEquals("Success", response.body?.string())
    }
}
