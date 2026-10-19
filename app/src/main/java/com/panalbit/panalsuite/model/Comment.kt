package com.panalbit.panalsuite.model

/**
 * Comentario asociado a un ticket.
 */
data class Comment(
    val id: String,
    val authorName: String,
    val content: String,
    val createdAt: Long
)
