package com.rgbstudios.roster.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuggestionUpdate(
    val resolved: Boolean,
    @SerialName("resolved_by") val resolvedBy: String,
    @SerialName("resolved_timestamp") val resolvedTimestamp: Long,
    @SerialName("resolution_note") val resolutionNote: String
)
