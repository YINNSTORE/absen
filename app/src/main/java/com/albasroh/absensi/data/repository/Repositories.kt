package com.albasroh.absensi.data.repository

import com.albasroh.absensi.data.local.dao.AttendanceDao
import com.albasroh.absensi.data.local.dao.UserDao
import com.albasroh.absensi.data.local.entity.Attendance
import com.albasroh.absensi.data.local.entity.AttendanceSession
import com.albasroh.absensi.data.local.entity.Method
import com.albasroh.absensi.data.local.entity.Status
import com.albasroh.absensi.data.local.entity.Student
import com.albasroh.absensi.data.local.entity.Teacher
import com.albasroh.absensi.util.Clock
import com.albasroh.absensi.util.Hashing

class AuthRepo(private val dao: UserDao) {
    suspend fun login(username: String, password: String) = runCatching {
        val user = dao.byUsername(username.trim())
            ?: error("Username atau password salah")

        if (user.passwordHash != Hashing.sha256(password)) {
            error("Username atau password salah")
        }

        user
    }
}

class AttendanceRepo(private val dao: AttendanceDao) {
    suspend fun mark(
        student: Student,
        teacher: Teacher,
        session: AttendanceSession
    ) = runCatching {
        if (dao.find(session.id, student.id) != null) {
            error("Murid sudah melakukan absensi pada sesi ini")
        }

        dao.insert(
            Attendance(
                studentId = student.id,
                teacherId = teacher.id,
                tanggal = session.tanggal,
                waktu = Clock.time(),
                status = Status.HADIR.name,
                method = Method.QR_SCAN.name,
                sessionId = session.id
            )
        )
    }
}
