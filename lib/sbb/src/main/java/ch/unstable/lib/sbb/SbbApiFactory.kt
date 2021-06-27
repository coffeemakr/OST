package ch.unstable.lib.sbb

import android.content.Context
import android.util.Log
import ch.unstable.lib.sbb.auth.AuthInterceptor
import ch.unstable.lib.sbb.json.*
import ch.unstable.lib.sbb.model.SbbConnectionPage
import ch.unstable.lib.sbb.model.StationResponse
import ch.unstable.ost.api.model.*
import ch.unstable.ost.api.model.Connection
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import javax.net.ssl.*


class SbbApiFactory {

    /**
     * Returns a trust manager that trusts `certificates` and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a `SSLHandshakeException`.
     *
     *
     * This can be used to replace the host platform's built-in trusted certificates with a custom
     * set. This is useful in development where certificate authority-trusted certificates aren't
     * available. Or in production, to avoid reliance on third-party certificate authorities.
     *
     *
     * See also [CertificatePinner], which can limit trusted certificates while still using
     * the host platform's built-in trust store.
     *
     * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
     *
     *
     * Relying on your own trusted certificates limits your server team's ability to update their
     * TLS certificates. By installing a specific set of trusted certificates, you take on additional
     * operational complexity and limit your ability to migrate between certificate authorities. Do
     * not use custom trusted certificates in production without the blessing of your server's TLS
     * administrator.
     */
    private fun trustManagerForCertificates(input: InputStream): X509TrustManager {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates = certificateFactory.generateCertificates(input)
        if (certificates.isEmpty()) {
            throw IllegalArgumentException("expected non-empty set of trusted certificates")
        }

        // Put the certificates a key store.
        val password = "2bU`dldNzSA0k(,f>oMku#Ak/`fox-?Z".toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)
        for ((index, certificate) in certificates.withIndex()) {
            val certificateAlias = index.toString()
            keyStore.setCertificateEntry(certificateAlias, certificate)
        }

        // Use it to build an X509 trust manager.
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        val trustManagers = trustManagerFactory.trustManagers
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }


    private fun newEmptyKeyStore(password: CharArray): KeyStore {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        // By convention, 'null' creates an empty key store.
        keyStore.load(null, password)
        return keyStore
    }

    val gson: Gson
        get() = GsonBuilder()
                .registerTypeAdapter(Station::class.java, StationDeserializer())
                .registerTypeAdapter(StationResponse::class.java, StationResponseDeserializer())
                .registerTypeAdapter(SbbConnectionPage::class.java, ConnectionPageDeserializer())
                .registerTypeAdapter(Connection::class.java, ConnectionDeserializer())
                .registerTypeAdapter(Section::class.java, SectionDeserializer())
                .registerTypeAdapter(TransportInfo::class.java, TransportInfoDeserializer())
                .registerTypeAdapter(RealtimeInfo::class.java, RealtimeInfoDeserializer())
                .create()!!

    private fun createTrustManager(certificate: InputStream): SSLConfig {
        val sslContext = SSLContext.getInstance("TLS")
        val trustManager = trustManagerForCertificates(certificate)
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        val sslSocketFactory = sslContext.socketFactory
        return SSLConfig(sslSocketFactory, trustManager)
    }


    data class SSLConfig(val sslSocketFactory: SSLSocketFactory, val trustManager: X509TrustManager)

    fun createSslContext(context: Context) = context.resources.openRawResource(R.raw.ca_cert).use {
        createTrustManager(it)
    }

    fun createAPI(sslConfig: SSLConfig): SbbApi {

        val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor())
                .addInterceptor(LoggingInterceptor())
                .sslSocketFactory(sslConfig.sslSocketFactory, sslConfig.trustManager)
                .build()
        return SbbApi(client = client, baseUrl = baseUrl, converter = gson)
    }

    internal class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
            val t1 = System.nanoTime()
            Log.d("OkHttp", java.lang.String.format("Sending request %s on %s%n%s",
                    request.url, chain.connection(), request.headers))
            val response: Response = chain.proceed(request)
            val t2 = System.nanoTime()
            Log.d("OkHttp", java.lang.String.format("Received response for %s in %.1fms%n%s",
                    response.request.url, (t2 - t1) / 1e6, response.headers))
            return response
        }
    }

    companion object {
        private const val baseUrl = "https://p1.sbbmobile.ch"
    }
}
