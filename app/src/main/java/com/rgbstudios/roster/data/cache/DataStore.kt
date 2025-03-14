package com.rgbstudios.roster.data.cache

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Create DataStore instance
private val Context.dataStore by preferencesDataStore(name = "clerking_notes")

object DataStoreManager {
    private val CLINICAL_NOTES_KEY = stringPreferencesKey("clinical_notes")
    private val REMINDERS_KEY = stringPreferencesKey("reminders_list")

    @Serializable
    data class Reminder(val staffId: String, val title: String, val message: String)

    // Save notes
    suspend fun saveNotes(context: Context, notes: String) {
        context.dataStore.edit { prefs ->
            prefs[CLINICAL_NOTES_KEY] = notes
        }
    }

    // Load notes
    suspend fun loadNotes(context: Context): String {
        val prefs = context.dataStore.data.first()
        return prefs[CLINICAL_NOTES_KEY] ?: ""
    }

    // Save a new reminder
    suspend fun saveReminder(context: Context, reminder: Reminder) {
        context.dataStore.edit { prefs ->
            val existingReminders = prefs[REMINDERS_KEY]?.let {
                Json.decodeFromString<List<Reminder>>(it)
            } ?: emptyList()

            val updatedReminders = existingReminders + reminder
            prefs[REMINDERS_KEY] = Json.encodeToString(updatedReminders)
        }
    }

    // Get all reminders
    suspend fun getReminders(context: Context): List<Reminder> {
        val prefs = context.dataStore.data.first()
        return prefs[REMINDERS_KEY]?.let {
            Json.decodeFromString(it)
        } ?: emptyList()
    }

    // Remove a single reminder
    suspend fun removeReminder(context: Context, staffId: String, title: String) {
        context.dataStore.edit { prefs ->
            val existingReminders = prefs[REMINDERS_KEY]?.let {
                Json.decodeFromString<List<Reminder>>(it)
            } ?: emptyList()

            val updatedReminders = existingReminders.filterNot { it.staffId == staffId && it.title == title }
            prefs[REMINDERS_KEY] = Json.encodeToString(updatedReminders)
        }
    }

    // Remove all reminders for a specific staff
    suspend fun removeAllRemindersForStaff(context: Context, staffId: String) {
        context.dataStore.edit { prefs ->
            val existingReminders = prefs[REMINDERS_KEY]?.let {
                Json.decodeFromString<List<Reminder>>(it)
            } ?: emptyList()

            val updatedReminders = existingReminders.filterNot { it.staffId == staffId }
            prefs[REMINDERS_KEY] = Json.encodeToString(updatedReminders)
        }
    }

}