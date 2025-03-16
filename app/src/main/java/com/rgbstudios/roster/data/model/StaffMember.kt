package com.rgbstudios.roster.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class StaffMember(
    @SerialName("id") val id: String = "",
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("role") val role: Int,
    @SerialName("unit") val unit: Int,
    @SerialName("on_call_dates") val onCallDates: List<Pair<Int, List<Int>>> = emptyList(),
    @SerialName("gym_call_dates") val gymCallDates: List<Pair<Int, List<Int>>> = emptyList(),
    @SerialName("leave_dates") val leaveDates: List<Pair<Int, Int>> = emptyList(),
    @SerialName("phone") val phone: String,
    @SerialName("image_url") val imageUrl: String = ""
)
