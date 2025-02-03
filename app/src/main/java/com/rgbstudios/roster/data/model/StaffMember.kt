package com.rgbstudios.roster.data.model

data class StaffMember(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val role: Int,
    val unit: Int,
    val avatarUri: String,
    val onCallDates: List<Pair<Int, List<Int>>>,
    val gymCallDates: List<Pair<Int, List<Int>>>,
    val leaveDates: List<Pair<Int, Int>>,
    val phone: String,
)