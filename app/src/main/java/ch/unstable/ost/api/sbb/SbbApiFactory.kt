package ch.unstable.ost.api.sbb

import android.content.Context
import ch.unstable.ost.R
import ch.unstable.ost.api.StationsDAO
import ch.unstable.ost.api.sbb.auth.AuthInterceptor
import ch.unstable.ost.api.sbb.model.StationResponse
import ch.unstable.ost.preference.StationDaoLoader
import ch.unstable.sbb.api.json.StationDeserializer
import ch.unstable.sbb.api.json.StationResponseDeserializer
import ch.unstable.sbb.api.model.Station
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.CertificatePinner
import org.apache.commons.codec.digest.DigestUtils
import java.io.IOException
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import javax.net.ssl.*


class SbbApiFactory: StationDaoLoader.StationDAOFactory {

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
    @Throws(GeneralSecurityException::class)
    private fun trustManagerForCertificates(`in`: InputStream): X509TrustManager {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates = certificateFactory.generateCertificates(`in`)
        if (certificates.isEmpty()) {
            throw IllegalArgumentException("expected non-empty set of trusted certificates")
        }

        // Put the certificates a key store.
        val password = "password".toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)
        var index = 0
        for (certificate in certificates) {
            val certificateAlias = Integer.toString(index++)
            keyStore.setCertificateEntry(certificateAlias, certificate)
        }

        // Use it to build an X509 trust manager.
        val keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        val trustManagers = trustManagerFactory.getTrustManagers()
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }


    @Throws(GeneralSecurityException::class)
    private fun newEmptyKeyStore(password: CharArray): KeyStore {
        try {
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            val `in`: InputStream? = null // By convention, 'null' creates an empty key store.
            keyStore.load(`in`, password)
            return keyStore
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }


    override fun getStationsDAO(context: Context): StationsDAO {
        val baseUrl = "https://p1.sbbmobile.ch";


        val certHash = DigestUtils.sha1(context.resources.openRawResource(R.raw.ca_cert))

        val builder = OkHttpClient.Builder()

        val sslContext = SSLContext.getInstance("TLS")

        /*
        val allTrustManager = object: X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }
        };
        sslContext.init(null, arrayOf<TrustManager>(allTrustManager), null)

        builder.sslSocketFactory(sslContext.socketFactory, allTrustManager)
        builder.hostnameVerifier { _, _ -> true }
        */

        val cert = context.resources.openRawResource(R.raw.ca_cert)
        val trustManager = trustManagerForCertificates(cert)
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        val sslSocketFactory = sslContext.socketFactory
        builder.sslSocketFactory(sslSocketFactory, trustManager)


        val client = builder
                .addInterceptor(AuthInterceptor(certHash))
                .build()!!


        val gson = GsonBuilder()
                .registerTypeAdapter(Station::class.java, StationDeserializer())
                .registerTypeAdapter(StationResponse::class.java, StationResponseDeserializer())
                .create()
        val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .build()!!

        val api = retrofit.create(UnauthApi::class.java)!!
        return SbbStationDao(api)
    }
}
