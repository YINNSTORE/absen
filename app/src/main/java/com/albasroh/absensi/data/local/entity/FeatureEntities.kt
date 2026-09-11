package com.albasroh.absensi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leave_requests")
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val type: String,
    val startDate: String,
    val endDate: String,
    val reason: String,
    val attachmentName: String? = null,
    val status: String = "MENUNGGU",
    val reviewerNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomName: String,
    val senderId: Long,
    val senderName: String,
    val senderRole: String,
    val message: String,
    val sentAt: Long = System.currentTimeMillis()
)
