package ch.unstable.lib.sbb.auth

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.security.MessageDigest

class KeysTest {
    /**
     * This tests main purpose is to recalculate the hash if required :)
     */
    @Test
    fun test_macKey() {
        val secretKey = "c3eAd3eC3a7845dE98f73942b3d5f9c0"
        val certBase64 = "WdfnzdQugRFUF5b812hZl3lAahM="
        val macKey = DigestUtils.getSha256Digest().apply {
            update(certBase64.toByteArray())
            update(secretKey.toByteArray())
        }.hexDigest()
        assertEquals(macKey, Keys.macKey)
    }
}

private fun MessageDigest.hexDigest(): String {
    return String(Hex.encodeHex(digest()))
}