package com.albasroh.absensi.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.albasroh.absensi.data.local.entity.Attendance
import com.albasroh.absensi.data.local.entity.AttendanceSession
import com.albasroh.absensi.data.local.entity.ClassRoom
import com.albasroh.absensi.data.local.entity.Student
import com.albasroh.absensi.data.local.entity.Teacher
import com.albasroh.absensi.data.local.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND isActive = 1 LIMIT 1")
    suspend fun byUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): User?

    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE role = 'ADMIN' OR role = 'GURU' ORDER BY nama")
    fun observeStaff(): Flow<List<User>>
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY nama")
    fun observeAll(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE isActive = 1 ORDER BY nama")
    fun observeActive(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): Student?

    @Query("SELECT * FROM students WHERE userId = :userId LIMIT 1")
    suspend fun byUser(userId: Long): Student?

    @Query("SELECT * FROM students WHERE qrToken = :token AND isActive = 1 LIMIT 1")
    suspend fun byToken(token: String): Student?

    @Insert
    suspend fun insert(student: Student): Long

    @Query("UPDATE students SET isActive = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)
}

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers ORDER BY nama")
    fun observeAll(): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE userId = :userId LIMIT 1")
    suspend fun byUser(userId: Long): Teacher?

    @Insert
    suspend fun insert(teacher: Teacher): Long
}

@Dao
interface ClassDao {
    @Query("SELECT * FROM class_rooms ORDER BY namaKelas")
    fun observeAll(): Flow<List<ClassRoom>>

    @Query("SELECT * FROM class_rooms WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): ClassRoom?

    @Insert
    suspend fun insert(classRoom: ClassRoom): Long

    @Delete
    suspend fun delete(classRoom: ClassRoom)
}

@Dao
interface SessionDao {
    @Query(
        "SELECT * FROM attendance_sessions " +
            "WHERE teacherId = :teacherId AND tanggal = :date AND isActive = 1 " +
            "LIMIT 1"
    )
    suspend fun active(teacherId: Long, date: String): AttendanceSession?

    @Query("SELECT * FROM attendance_sessions ORDER BY tanggal DESC, startTime DESC")
    fun observeAll(): Flow<List<AttendanceSession>>

    @Insert
    suspend fun insert(session: AttendanceSession): Long
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY tanggal DESC, waktu DESC")
    fun observeAll(): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY tanggal DESC, waktu DESC")
    fun byStudent(studentId: Long): Flow<List<Attendance>>

    @Query(
        "SELECT * FROM attendance " +
            "WHERE sessionId = :sessionId AND studentId = :studentId LIMIT 1"
    )
    suspend fun find(sessionId: Long, studentId: Long): Attendance?

    @Insert
    suspend fun insert(attendance: Attendance): Long

    @Query("SELECT COUNT(*) FROM attendance WHERE tanggal = :date AND status = 'HADIR'")
    fun countPresent(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance WHERE tanggal = :date AND status = 'IZIN'")
    fun countIzin(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance WHERE tanggal = :date AND status = 'SAKIT'")
    fun countSakit(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance WHERE tanggal = :date AND status = 'ALPA'")
    fun countAlpa(date: String): Flow<Int>
}
