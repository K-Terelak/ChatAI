package org.jetbrains.plugins.template

import org.jetbrains.plugins.template.chatApp.model.ChatMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatMessageTest {

    @Test
    fun matches_isCaseInsensitiveAndIgnoresBlankQuery() {
        val message = ChatMessage(content = "Hello Kotlin", author = "AI")

        assertTrue(message.matches("kotlin"))
        assertFalse(message.matches(""))
        assertFalse(message.matches("   "))
    }

    @Test
    fun formattedTime_usesProvidedFormatter() {
        val timestamp = LocalDateTime.of(2026, 5, 6, 9, 30)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val message = ChatMessage(content = "Hi", author = "AI", timestamp = timestamp)

        assertEquals("09:30", message.formattedTime(formatter))
    }
}

