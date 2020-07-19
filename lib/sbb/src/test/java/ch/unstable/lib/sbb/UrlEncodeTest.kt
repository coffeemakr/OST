package ch.unstable.lib.sbb

import org.junit.Test

import org.junit.Assert.*

class UrlEncodeTest {

    @Test
    fun urlEncodeTest() {
        assertEquals("%20", urlEncodePathSegment(" "))
        assertEquals("Test", urlEncodePathSegment("Test"))
        assertEquals("Z%C3%BCrich", urlEncodePathSegment("Zürich"))
    }
}