package com.example.eventmanagement.events.data.models

import com.google.firebase.Timestamp

data class Events(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dateTime: Timestamp? = null,
    val location: String = "",
    val userId: String = ""
)
