package com.rgbstudios.roster.utils

import com.rgbstudios.roster.R

sealed class ResourceItem(val name: String, val iconRes: Int) {
        data object MainScreen : ResourceItem("Main Screen", R.drawable.ic_resources)
        data object Organogram : ResourceItem("Department Organogram", R.drawable.ic_organogram)
        data object Clerking : ResourceItem("Clerking", R.drawable.ic_document_write)
        data object Notifications : ResourceItem("Notifications", R.drawable.ic_notification)
        data object Suggestions : ResourceItem("Suggestions Box", R.drawable.ic_box)
        data object Login : ResourceItem("Login", R.drawable.ic_login)
        data object Logout : ResourceItem("Logout", R.drawable.ic_logout)
        data object About : ResourceItem("About", R.drawable.ic_about)
}