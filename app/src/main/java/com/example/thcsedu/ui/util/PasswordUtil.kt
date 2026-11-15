package com.example.thcsedu.ui.util

import java.security.MessageDigest

object PasswordUtil {
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        val computed = hashPassword(password)
        return computed.equals(storedHash.trim(), ignoreCase = true)
    }
}
