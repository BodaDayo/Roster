package com.rgbstudios.roster.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Suggestion(
    @SerialName("id") val id: String = "",
    @SerialName("text") val text: String = "",
    @SerialName("author") val author: String = "Anonymous",
    @SerialName("resolved") val resolved: Boolean = false,
    @SerialName("resolved_by") val resolvedBy: String? = null,
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @SerialName("resolved_timestamp") val resolvedTimestamp: Long? = null,
    @SerialName("resolution_note") val resolutionNote: String? = null
)
