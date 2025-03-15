package com.rgbstudios.roster.data.cache

import android.content.Context
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.data.model.Suggestion
import com.tencent.mmkv.MMKV
import kotlinx.serialization.json.Json

object OfflineCache {
    private lateinit var mmkv: MMKV

    fun initialize(context: Context) {
        if (!OfflineCache::mmkv.isInitialized) {
            MMKV.initialize(context)
            mmkv = MMKV.defaultMMKV()!!
        }
    }

    fun saveStaff(staff: List<StaffMember>) {
        mmkv.encode("staff", Json.encodeToString(staff))
    }

    fun getStaff(): List<StaffMember> {
        return mmkv.decodeString("staff")?.let {
            Json.decodeFromString(it)
        } ?: emptyList()
    }

    fun saveSuggestions(suggestions: List<Suggestion>) {
        mmkv.encode("suggestions", Json.encodeToString(suggestions))
    }

    fun getSuggestions(): List<Suggestion> {
        return mmkv.decodeString("suggestions")?.let {
            Json.decodeFromString(it)
        } ?: emptyList()
    }
}