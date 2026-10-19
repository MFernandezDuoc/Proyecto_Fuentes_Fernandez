package com.panalbit.panalsuite.data.remote.api

import com.panalbit.panalsuite.data.remote.dto.*
import retrofit2.http.*

interface TicketApiService {
    @GET("tickets")
    suspend fun getTickets(
        @Query("status") status: String? = null,
        @Query("priority") priority: String? = null,
        @Query("assigned_to") assignedTo: String? = null
    ): ApiResponse<List<TicketDto>>

    @GET("tickets/{id}")
    suspend fun getTicketById(@Path("id") id: String): ApiResponse<TicketDto>

    @PATCH("tickets/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: String,
        @Body body: Map<String, String?>
    ): ApiResponse<Unit>

    @POST("tickets/{id}/comments")
    suspend fun addComment(
        @Path("id") ticketId: String,
        @Body body: Map<String, String>
    ): ApiResponse<CommentDto>

    @POST("tickets/{id}/evidences")
    suspend fun addEvidence(
        @Path("id") ticketId: String,
        @Body evidence: EvidenceDto
    ): ApiResponse<EvidenceDto>

    @PATCH("tickets/{id}/assignee")
    suspend fun reassign(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): ApiResponse<Unit>
}
