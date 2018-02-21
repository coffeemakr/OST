package ch.unstable.ost.api.sbb.auth

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import ch.unstable.ost.BuildConfig
import ch.unstable.ost.utils.StandartCharsetCompat.UTF_8
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
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

    private val tag = "AuthInterceptor"

    private val secretKey: ByteArray = "c3eAd3eC3a7845dE98f73942b3d5f9c0".toByteArray(UTF_8)

    private val macKey: SecretKeySpec

    private fun generateMacKey(certificateHash: ByteArray): SecretKeySpec {
        //val certBase64 = encodeBase64(certificateHash)!!
        //assert("WdfnzdQugRFUF5b812hZl3lAahM=" == String(certBase64))
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
        Log.d(tag, "Using date: " + date)
        Log.d(tag, "Using path: " + path)

        val mac = createMac()
        mac.update(path.toByteArray(UTF_8))
        mac.update(date.toByteArray(UTF_8))

        val key = encodeBase64(mac.doFinal()).toString(UTF_8)
        Log.d(tag, "Using key: " + key)
        Build.VERSION.RELEASE


        val versionName = "7.1.1"
        //val userAgent = "SBBmobile/" + versionName + " Android/" + Build.VERSION.RELEASE + "(" + Build.MANUFACTURER + ";" + Build.MODEL + ")"
        val userAgent = "SBBmobile/flavorprodRelease-7.3.0-RELEASE Android/8.1.0 (Google;Android SDK built for x86)"
        Log.d(tag, "Using user-agent: " + userAgent)

        return original.newBuilder()
                .addHeader("X-API-AUTHORIZATION", key)
                .addHeader("X-API-DATE", date)
                .addHeader("User-Agent", userAgent)
                .build()

    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = createNewRequest(original);
        return chain.proceed(request)
    }

    init {
        macKey = generateMacKey(certHash!!)
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