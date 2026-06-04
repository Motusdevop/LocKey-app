package com.middlespp.lockey.feature.passes.domain.model

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

class AccessPassTest {
    @Test
    fun `pass is valid five minutes before booking start`() {
        val pass = accessPass()

        assertTrue(pass.isValidAt(Instant.parse("2026-04-22T11:55:00Z")))
    }

    @Test
    fun `pass is not valid before grace period`() {
        val pass = accessPass()

        assertFalse(pass.isValidAt(Instant.parse("2026-04-22T11:54:59Z")))
    }

    @Test
    fun `pass is valid until booking end inclusively`() {
        val pass = accessPass()

        assertTrue(pass.isValidAt(Instant.parse("2026-04-22T14:00:00Z")))
    }

    @Test
    fun `pass rejects invalid booking window`() {
        assertFailsWith<IllegalArgumentException> {
            accessPass(
                bookingStartsAt = Instant.parse("2026-04-22T14:00:00Z"),
                bookingEndsAt = Instant.parse("2026-04-22T12:00:00Z")
            )
        }
    }

    private fun accessPass(
        bookingStartsAt: Instant = Instant.parse("2026-04-22T12:00:00Z"),
        bookingEndsAt: Instant = Instant.parse("2026-04-22T14:00:00Z")
    ): AccessPass = AccessPass(
        lockId = "studio-a1",
        accessCode = "ABCDEF1234",
        bookingStartsAt = bookingStartsAt,
        bookingEndsAt = bookingEndsAt
    )
}
