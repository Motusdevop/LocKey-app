package com.middlespp.lockey.feature.scanner.domain.parse

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LockQrParserTest {
    private val parser = LockQrParser()

    @Test
    fun `parses supported lock QR code`() {
        val result = parser.parse("http://45.154.35.214/LocKey/open/studio-a1?s=A1B2C3")

        assertEquals("studio-a1", result?.lockId)
        assertEquals("A1B2C3", result?.lockCode)
    }

    @Test
    fun `returns null for another host`() {
        val result = parser.parse("http://example.com/LocKey/open/studio-a1?s=A1B2C3")

        assertNull(result)
    }

    @Test
    fun `returns null without lock code`() {
        val result = parser.parse("http://45.154.35.214/LocKey/open/studio-a1")

        assertNull(result)
    }

    @Test
    fun `returns null for malformed query parameter`() {
        val result = parser.parse("http://45.154.35.214/LocKey/open/studio-a1?s")

        assertNull(result)
    }

    @Test
    fun `returns null for unsupported path`() {
        val result = parser.parse("http://45.154.35.214/LocKey/locks/studio-a1?s=A1B2C3")

        assertNull(result)
    }
}
