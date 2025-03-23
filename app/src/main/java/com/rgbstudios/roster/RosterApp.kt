package com.rgbstudios.roster

import android.app.Application
import com.rgbstudios.roster.data.cache.OfflineCache

class RosterApp : Application() {
    override fun onCreate() {
        super.onCreate()
        OfflineCache.initialize(this)
    }
}