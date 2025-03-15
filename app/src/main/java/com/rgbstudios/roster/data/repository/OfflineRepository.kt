package com.rgbstudios.roster.data.repository

import com.rgbstudios.roster.data.cache.OfflineCache
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.data.model.Suggestion

object OfflineRepository {
    fun getStaff(): List<StaffMember> = OfflineCache.getStaff()
    fun getSuggestions(): List<Suggestion> = OfflineCache.getSuggestions()

    fun saveStaff(staff: List<StaffMember>) {
        OfflineCache.saveStaff(staff)
    }

    fun saveSuggestions(suggestions: List<Suggestion>) {
        OfflineCache.saveSuggestions(suggestions)
    }
}