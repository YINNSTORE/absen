package com.albasroh.absensi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.albasroh.absensi.data.local.entity.ChatMessage
import com.albasroh.absensi.data.local.entity.LeaveRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveRequestDao {
    @Query("SELECT * FROM leave_requests ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun byStudent(studentId: Long): Flow<List<LeaveRequest>>

    @Insert
    suspend fun insert(request: LeaveRequest): Long

    @Query("UPDATE leave_requests SET status = :status, reviewerNote = :note WHERE id = :id")
    suspend fun review(id: Long, status: String, note: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE roomName = :room ORDER BY sentAt ASC")
    fun observeRoom(room: String): Flow<List<ChatMessage>>

    @Insert
    suspend fun insert(message: ChatMessage): Long
}
