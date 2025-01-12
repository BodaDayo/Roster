package com.rgbstudios.roster.data.model

data class StaffMember(
    val id: Int,
    val name: String,
    val role: Int,
    val unit: Int,
    val avatarUri: String,
    val onCallDates: List<Int>,
    val leaveDates: List<Int>,
    val phone: String,
)