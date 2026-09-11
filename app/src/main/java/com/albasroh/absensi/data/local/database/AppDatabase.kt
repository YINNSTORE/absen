package com.albasroh.absensi.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.albasroh.absensi.data.local.dao.AttendanceDao
import com.albasroh.absensi.data.local.dao.ClassDao
import com.albasroh.absensi.data.local.dao.SessionDao
import com.albasroh.absensi.data.local.dao.StudentDao
import com.albasroh.absensi.data.local.dao.TeacherDao
import com.albasroh.absensi.data.local.dao.UserDao
import com.albasroh.absensi.data.local.entity.Attendance
import com.albasroh.absensi.data.local.entity.AttendanceSession
import com.albasroh.absensi.data.local.entity.ClassRoom
import com.albasroh.absensi.data.local.entity.Student
import com.albasroh.absensi.data.local.entity.Teacher
import com.albasroh.absensi.data.local.entity.User
import com.albasroh.absensi.data.local.entity.LeaveRequest
import com.albasroh.absensi.data.local.entity.ChatMessage
import com.albasroh.absensi.data.local.dao.LeaveRequestDao
import com.albasroh.absensi.data.local.dao.ChatDao

@Database(
    entities = [
        User::class,
        Student::class,
        Teacher::class,
        ClassRoom::class,
        AttendanceSession::class,
        Attendance::class,
        LeaveRequest::class,
        ChatMessage::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun users(): UserDao
    abstract fun students(): StudentDao
    abstract fun teachers(): TeacherDao
    abstract fun classes(): ClassDao
    abstract fun sessions(): SessionDao
    abstract fun attendance(): AttendanceDao
    abstract fun leaveRequests(): LeaveRequestDao
    abstract fun chat(): ChatDao
}
