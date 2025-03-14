package com.rgbstudios.roster.utils

sealed class DatabaseField(val name: String) {
    data object Id : DatabaseField("id")
    data object FirstName : DatabaseField("first_name")
    data object LastName : DatabaseField("last_name")
    data object Role : DatabaseField("role")
    data object Unit : DatabaseField("unit")
    data object OnCallDates : DatabaseField("on_call_dates")
    data object GymCallDates : DatabaseField("gym_call_dates")
    data object LeaveDates : DatabaseField("leave_dates")
    data object Phone : DatabaseField("phone")
    data object ImageUrl : DatabaseField("image_url")
}