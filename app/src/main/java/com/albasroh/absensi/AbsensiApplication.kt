package com.albasroh.absensi

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.albasroh.absensi.data.datastore.Prefs
import com.albasroh.absensi.data.local.database.AppDatabase
import com.albasroh.absensi.data.local.entity.ClassRoom
import com.albasroh.absensi.data.local.entity.Student
import com.albasroh.absensi.data.local.entity.Teacher
import com.albasroh.absensi.data.local.entity.User
import com.albasroh.absensi.data.repository.AttendanceRepo
import com.albasroh.absensi.data.repository.AuthRepo
import com.albasroh.absensi.util.Hashing
import com.albasroh.absensi.util.Tokens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.CompletableDeferred

class AbsensiApplication : Application() {
    lateinit var db: AppDatabase
        private set

    lateinit var prefs: Prefs
        private set

    lateinit var auth: AuthRepo
        private set

    lateinit var attendanceRepo: AttendanceRepo
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Sinyal bahwa database dan akun awal sudah siap. Splash menunggu ini agar tidak macet/race.
    val ready = CompletableDeferred<Unit>()

    override fun onCreate() {
        super.onCreate()

        val migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS leave_requests (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, studentId INTEGER NOT NULL, type TEXT NOT NULL, startDate TEXT NOT NULL, endDate TEXT NOT NULL, reason TEXT NOT NULL, attachmentName TEXT, status TEXT NOT NULL, reviewerNote TEXT NOT NULL, createdAt INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS chat_messages (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, roomName TEXT NOT NULL, senderId INTEGER NOT NULL, senderName TEXT NOT NULL, senderRole TEXT NOT NULL, message TEXT NOT NULL, sentAt INTEGER NOT NULL)")
            }
        }

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "absensi.db"
        ).addMigrations(migration).build()

        prefs = Prefs(this)
        auth = AuthRepo(db.users())
        attendanceRepo = AttendanceRepo(db.attendance())

        applicationScope.launch {
            try {
                seed()
                ready.complete(Unit)
            } catch (t: Throwable) {
                ready.completeExceptionally(t)
            }
        }
    }

    private suspend fun seed() {
        if (db.users().byUsername("admin") != null) return

        db.users().insert(
            User(
                username = "admin",
                passwordHash = Hashing.sha256("admin123"),
                nama = "Administrator",
                role = "ADMIN"
            )
        )

        val teacherId = db.users().insert(
            User(
                username = "guru",
                passwordHash = Hashing.sha256("guru123"),
                nama = "Bapak Ahmad",
                role = "GURU",
                nip = "19880001"
            )
        )

        val studentId = db.users().insert(
            User(
                username = "murid",
                passwordHash = Hashing.sha256("murid123"),
                nama = "Ahmad Fauzan",
                role = "MURID",
                nis = "20260001",
                kelas = "VII-A"
            )
        )

        db.classes().insert(
            ClassRoom(
                namaKelas = "VII-A",
                waliKelas = "Bapak Ahmad"
            )
        )

        db.classes().insert(
            ClassRoom(
                namaKelas = "VII-B",
                waliKelas = "Ibu Siti"
            )
        )

        db.teachers().insert(
            Teacher(
                userId = teacherId,
                nip = "19880001",
                nama = "Bapak Ahmad",
                mataPelajaran = "Matematika"
            )
        )

        db.students().insert(
            Student(
                userId = studentId,
                nis = "20260001",
                nama = "Ahmad Fauzan",
                kelas = "VII-A",
                qrToken = Tokens.new()
            )
        )

        repeat(5) { index ->
            val number = index + 2
            val nis = "2026000$number"
            val userId = db.users().insert(
                User(
                    username = "murid$number",
                    passwordHash = Hashing.sha256("murid123"),
                    nama = "Murid $number",
                    role = "MURID",
                    nis = nis,
                    kelas = "VII-A"
                )
            )

            db.students().insert(
                Student(
                    userId = userId,
                    nis = nis,
                    nama = "Murid $number",
                    kelas = "VII-A",
                    qrToken = Tokens.new()
                )
            )
        }
    }
}
