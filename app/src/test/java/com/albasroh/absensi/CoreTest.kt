package com.albasroh.absensi

import com.albasroh.absensi.data.local.entity.Attendance
import com.albasroh.absensi.data.local.entity.Role
import com.albasroh.absensi.data.local.entity.Status
import com.albasroh.absensi.util.Hashing
import com.albasroh.absensi.util.Qr
import com.albasroh.absensi.util.Tokens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreTest {
    @Test
    fun passwordHashWorks() {
        assertEquals(Hashing.sha256("abc"), Hashing.sha256("abc"))
        assertNotEquals(Hashing.sha256("abc"), Hashing.sha256("abd"))
    }

    @Test
    fun qrIsSafe() {
        val token = Tokens.new()
        assertEquals(token, Qr.token(Qr.payload(token)))
        assertNull(Qr.token("password"))
    }

    @Test
    fun rolesAndStatuses() {
        assertTrue(
            Role.entries.map { it.name }.containsAll(
                listOf("ADMIN", "GURU", "MURID")
            )
        )
        assertEquals(4, Status.entries.size)
    }

    @Test
    fun duplicateConstraintModelExists() {
        assertTrue(
            Attendance::class.java.declaredFields.any {
                it.name == "sessionId"
            }
        )
    }
}
