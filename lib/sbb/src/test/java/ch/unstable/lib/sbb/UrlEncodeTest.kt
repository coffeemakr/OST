package ch.unstable.lib.sbb

import org.junit.Test

import org.junit.Assert.*

class UrlEncodeTest {

    @Test
    fun urlEncodeTest() {
        assertEquals("%20", urlEncode(" "))
        assertEquals("Test", urlEncode("Test"))
        assertEquals("Z%C3%BCrich", urlEncode("Zürich"))
    }
}