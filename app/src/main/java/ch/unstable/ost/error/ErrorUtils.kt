package ch.unstable.ost.error

import android.content.pm.Signature
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import ch.unstable.ost.BuildConfig
import ch.unstable.ost.R
import ch.unstable.ost.utils.NavHelper.startErrorActivity
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object ErrorUtils {
    private const val TAG = "ErrorUtils"

    /*
    * Get the X.509 certificate.
    */
    @Throws(CertificateException::class)
    private fun getCertificateFromSignature(signature: Signature): X509Certificate {
        val rawCert = signature.toByteArray()
        val certStream: InputStream = ByteArrayInputStream(rawCert)
        val certFactory = CertificateFactory.getInstance("X509")
        return certFactory.generateCertificate(certStream) as X509Certificate
    }

    @Throws(CertificateEncodingException::class)
    fun getSHA256Fingerprint(certificate: X509Certificate): ByteArray {
        return try {
            getFingerprint(certificate, "SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    @Throws(NoSuchAlgorithmException::class, CertificateEncodingException::class)
    private fun getFingerprint(cert: X509Certificate, algorithm: String): ByteArray {
        val md = MessageDigest.getInstance(algorithm)
        val der = cert.encoded
        md.update(der)
        return md.digest()
    }

    fun printableSignature(signature: Signature): String {
        val certificate: X509Certificate = try {
            getCertificateFromSignature(signature)
        } catch (e: Exception) {
            return signature.toCharsString()
        }
        val builder = StringBuilder()
        builder.append("Subject: ")
        builder.append(certificate.subjectDN.name)
        builder.append('\n')
        builder.append("Issuer: ")
        builder.append(certificate.issuerDN.name)
        builder.append('\n')
        builder.append("Serial Number: ")
        builder.append(certificate.serialNumber)
        builder.append('\n')
        builder.append("Not After: ")
        builder.append(certificate.notAfter)
        builder.append('\n')
        builder.append("Not Before: ")
        builder.append(certificate.notBefore)
        builder.append('\n')
        return builder.toString()
    }

    @JvmStatic
    fun showErrorSnackbar(view: View, @StringRes errorMessage: Int, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, view.context.getString(errorMessage), throwable)
        }
        Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_resport_error) { view1: View -> startErrorActivity(view1.context, throwable!!) }
                .show()
    }
}