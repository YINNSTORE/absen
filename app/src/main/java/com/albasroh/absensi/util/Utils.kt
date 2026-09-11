package com.albasroh.absensi.util

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object Hashing {
    fun sha256(value: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString("") { byte ->
                String.format("%02x", byte)
            }
    }
}

object Clock {
    fun date(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun time(): String {
        return SimpleDateFormat("HH:mm", Locale.US).format(Date())
    }
}

object Qr {
    const val PREFIX = "ABSENSI|STUDENT|"

    fun payload(token: String): String {
        return PREFIX + token
    }

    fun token(value: String): String? {
        if (!value.startsWith(PREFIX)) return null

        return value
            .removePrefix(PREFIX)
            .takeIf { it.length in 16..128 }
    }
}

object Tokens {
    fun new(): String {
        return UUID.randomUUID().toString().replace("-", "") +
            UUID.randomUUID().toString().replace("-", "")
    }
}
