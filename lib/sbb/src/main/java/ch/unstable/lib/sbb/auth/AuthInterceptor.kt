package ch.unstable.lib.sbb.auth

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.Util.UTF_8
import org.apache.commons.codec.binary.Base64.encodeBase64
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class AuthInterceptor(certHash: ByteArray, private val dateSource: DateSource) : Interceptor {

    constructor(certHash: ByteArray) : this(certHash, DefaultDateSource())

    private val secretKey: ByteArray = "c3eAd3eC3a7845dE98f73942b3d5f9c0".toByteArray(UTF_8)


    private val userAgent = run {
        val versionName = "10.6.0"
        val androidVersion = "9"
        val deviceName = "Google;Android SDK built for x86"
        "SBBmobile/flavorprodRelease-$versionName-RELEASE Android/$androidVersion ($deviceName)"
    }

    private val token = UUID.randomUUID().toString()

    private val macKey: SecretKeySpec

    private fun generateMacKey(certificateHash: ByteArray): SecretKeySpec {
        //val certBase64 = encodeBase64(certificateHash)!!
        val certBase64 = "WdfnzdQugRFUF5b812hZl3lAahM=".toByteArray(UTF_8)
        val keyDigest = DigestUtils.getSha256Digest()!!
        keyDigest.update(certBase64)
        keyDigest.update(secretKey)
        val macKey = keyDigest.hexDigest();
        return SecretKeySpec(macKey,"HmacSHA1")
    }

    private fun createMac(): Mac {
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(macKey)
        return mac
    }

    fun createNewRequest(original: Request): Request {
        val date = SimpleDateFormat("YYYY-MM-dd", Locale.US).format(dateSource.currentDate)
        val path = original
                .url()
                .encodedPath()

        val mac = createMac()
        mac.update(path.toByteArray(UTF_8))
        mac.update(date.toByteArray(UTF_8))

        val key = encodeBase64(mac.doFinal()).toString(UTF_8)

        return original.newBuilder()
                .addHeader("X-API-AUTHORIZATION", key)
                .addHeader("X-API-DATE", date)
                .addHeader("X-APP-TOKEN", token)
                .addHeader("User-Agent", userAgent)
                .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = createNewRequest(chain.request())
        return chain.proceed(request)
    }

    init {
        macKey = generateMacKey(certHash)
    }
}

interface DateSource {
    val currentDate: Date
}

class DefaultDateSource: DateSource {
    override val currentDate: Date
        get() = Date()
}

class CustomDateSource(override val currentDate: Date) : DateSource

private fun MessageDigest.hexDigest(): ByteArray {
    return String(Hex.encodeHex(digest())).toByteArray(UTF_8)
}