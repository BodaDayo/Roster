package com.rgbstudios.roster.data.model

data class StaffMember(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val role: Int,
    val unit: Int,
    val avatarUri: String,
    val onCallDates: List<Int>,
    val gymCallDates: List<Int>,
    val leaveDates: List<Int>,
    val phone: String,
)