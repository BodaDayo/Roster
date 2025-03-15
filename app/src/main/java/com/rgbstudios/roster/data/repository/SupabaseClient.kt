package com.rgbstudios.roster.data.repository

import android.util.Log
import com.rgbstudios.roster.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object SupabaseClientInstance {
    private var client: SupabaseClient? = null
    private val lock = Mutex()

    suspend fun getClient(): SupabaseClient = lock.withLock {
        client ?: createClient().also { client = it }
    }

    private suspend fun createClient(): SupabaseClient = withContext(Dispatchers.IO) {
        try {
            createSupabaseClient(
                supabaseUrl = BuildConfig.SUPABASE_URL,
                supabaseKey = BuildConfig.SUPABASE_ANON_KEY
            ) {
                install(Auth)
                install(Storage)
                install(Realtime)
                install(Postgrest)

                httpEngine = OkHttp.create {
                    config {
                        connectTimeout(15, TimeUnit.SECONDS)
                        readTimeout(15, TimeUnit.SECONDS)
                        writeTimeout(15, TimeUnit.SECONDS)
                        addInterceptor(HttpLoggingInterceptor().apply {
                            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
                        })
                    }
                }
            }
        } catch (e: HttpRequestTimeoutException) {
            Log.e("SupabaseClient", "Request timed out, using cached data", e)
            throw Exception("Request timeout. Using cached data.")
        } catch (e: Exception) {
            throw Exception("Offline mode - using cached data", e)
        }
    }
}
