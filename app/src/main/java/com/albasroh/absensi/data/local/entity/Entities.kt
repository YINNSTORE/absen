package com.albasroh.absensi.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Role {
    ADMIN,
    GURU,
    MURID
}

enum class Status {
    HADIR,
    IZIN,
    SAKIT,
    ALPA
}

enum class Method {
    QR_SCAN,
    MANUAL
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val nama: String,
    val role: String,
    val nis: String? = null,
    val nip: String? = null,
    val kelas: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val nis: String,
    val nama: String,
    val kelas: String,
    val qrToken: String,
    val isActive: Boolean = true
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val nip: String,
    val nama: String,
    val mataPelajaran: String,
    val isActive: Boolean = true
)

@Entity(tableName = "class_rooms")
data class ClassRoom(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val namaKelas: String,
    val waliKelas: String
)

@Entity(tableName = "attendance_sessions")
data class AttendanceSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teacherId: Long,
    val classId: Long,
    val tanggal: String,
    val startTime: String,
    val endTime: String? = null,
    val isActive: Boolean = true
)

@Entity(
    tableName = "attendance",
    indices = [
        Index(
            value = ["sessionId", "studentId"],
            unique = true
        )
    ]
)
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val teacherId: Long,
    val tanggal: String,
    val waktu: String,
    val status: String,
    val method: String,
    val notes: String = "",
    val sessionId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
