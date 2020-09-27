package de.fayard.refreshVersions.core.internal

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.fayard.refreshVersions.core.extensions.okhttp.await
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.HttpException
import retrofit2.Response

internal class GradleUpdateChecker(val httpClient: OkHttpClient) {
    val API_URL = "https://services.gradle.org/versions/current"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    suspend fun fetchGradleCurrentVersion(): GradleCurrentVersion? {
        val request = Request.Builder().url(API_URL).build()
        return httpClient.newCall(request).await().use { response ->
            if (response.isSuccessful) {
                moshi.adapter(GradleCurrentVersion::class.java)
                    .fromJson(response.body!!.source())
            } else throw HttpException(Response.error<Any?>(response.code, response.body!!))
        }
    }
}

/**
 * Result of GET [GradleUpdateChecker.API_URL]
 * See plugins/core/src/test/resources/gradle-current-version.json
 */
internal data class GradleCurrentVersion(
    val version: String,
    val buildTime: String,
    val current: Boolean,
    val snapshot: Boolean,
    val nightly: Boolean,
    val activeRc: Boolean,
    val rcFor: String,
    val milestoneFor: String,
    val broken: Boolean,
    val downloadUrl: String,
    val checksumUrl: String,
    val wrapperChecksumUrl: String
)