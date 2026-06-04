package com.middlespp.lockey.feature.passes.domain.parse

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class PassLinkParserTest {
    private val parser = PassLinkParser()

    @Test
    fun `parses supported access pass deep link`() {
        val result = parser.parse(
            "lockey://open?lock_id=studio-a1&access_code=ABCDEF1234" +
                "&booking_starts_at=2026-04-22T12:00:00Z" +
                "&booking_ends_at=2026-04-22T14:00:00Z"
        )

        assertEquals("studio-a1", result?.lockId)
        assertEquals("ABCDEF1234", result?.accessCode)
        assertEquals(Instant.parse("2026-04-22T12:00:00Z"), result?.bookingStartsAt)
        assertEquals(Instant.parse("2026-04-22T14:00:00Z"), result?.bookingEndsAt)
    }

    @Test
    fun `returns null for unsupported scheme`() {
        val result = parser.parse(
            "https://open?lock_id=studio-a1&access_code=ABCDEF1234" +
                "&booking_starts_at=2026-04-22T12:00:00Z" +
                "&booking_ends_at=2026-04-22T14:00:00Z"
        )

        assertNull(result)
    }

    @Test
    fun `returns null without access code`() {
        val result = parser.parse(
            "lockey://open?lock_id=studio-a1" +
                "&booking_starts_at=2026-04-22T12:00:00Z" +
                "&booking_ends_at=2026-04-22T14:00:00Z"
        )

        assertNull(result)
    }

    @Test
    fun `returns null for invalid time`() {
        val result = parser.parse(
            "lockey://open?lock_id=studio-a1&access_code=ABCDEF1234" +
                "&booking_starts_at=invalid" +
                "&booking_ends_at=2026-04-22T14:00:00Z"
        )

        assertNull(result)
    }

    @Test
    fun `returns null when booking end is before start`() {
        val result = parser.parse(
            "lockey://open?lock_id=studio-a1&access_code=ABCDEF1234" +
                "&booking_starts_at=2026-04-22T14:00:00Z" +
                "&booking_ends_at=2026-04-22T12:00:00Z"
        )

        assertNull(result)
    }
}
