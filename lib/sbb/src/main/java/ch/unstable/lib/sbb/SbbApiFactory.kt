package ch.unstable.lib.sbb

import ch.unstable.lib.sbb.auth.AuthInterceptor
import ch.unstable.lib.sbb.json.StationDeserializer
import ch.unstable.lib.sbb.json.StationResponseDeserializer
import ch.unstable.lib.sbb.model.Station
import ch.unstable.lib.sbb.model.StationResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import org.apache.commons.codec.digest.DigestUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
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
            val certificateAlias = Integer.toString(index)
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


    val baseUrl = "https://p1.sbbmobile.ch"


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
                .create()!!

    private fun openCertificate() = javaClass.getResourceAsStream("ch/unstable/lib/sbb/ca_cert.crt")!!

    private val authInterceptor: AuthInterceptor
        get() {
            val certHash = DigestUtils.sha1(openCertificate())
            return AuthInterceptor(certHash)
        }

    private fun createTrustManager(): SSLConfig {
        val sslContext = SSLContext.getInstance("TLS")
        val trustManager = trustManagerForCertificates(openCertificate())
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        val sslSocketFactory = sslContext.socketFactory
        return SSLConfig(sslSocketFactory, trustManager)
    }


    private fun createTrustAllX509TrustManager(): SSLConfig {
        val sslContext = SSLContext.getInstance("TLS")
        val allTrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
        }
        sslContext.init(null, arrayOf<TrustManager>(allTrustManager), null)
        return SSLConfig(sslContext.socketFactory, allTrustManager)
    }


    private data class SSLConfig(val sslSocketFactory: SSLSocketFactory, val trustManager: X509TrustManager)

    fun createAPI(): UnauthApi {

        val (sslSocketFactory, trustManager) = createTrustManager()

        val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build()

        val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .build()!!

        return retrofit.create(UnauthApi::class.java)!!
    }
}
