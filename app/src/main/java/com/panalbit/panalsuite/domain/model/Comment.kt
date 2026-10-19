package com.panalbit.panalsuite.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String,
    val ticketId: String,
    val authorId: String,
    val authorName: String,
    val content: String,
    val createdAt: String
)
