package cn.edu.sjtu.arf.utils

import android.content.Context
import java.io.IOException
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

internal class FakeX509TrustManager : X509TrustManager {
    @Throws(CertificateException::class)
    override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    fun isClientTrusted(chain: Array<X509Certificate?>?): Boolean {
        return true
    }

    fun isServerTrusted(chain: Array<X509Certificate?>?): Boolean {
        return true
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return _AcceptedIssuers
    }

    companion object {
        private var trustManagers: Array<TrustManager>? = null
        private val _AcceptedIssuers = arrayOf<X509Certificate>()
        fun allowAllSSL() : SSLSocketFactory {
            HttpsURLConnection.setDefaultHostnameVerifier() { arg0, arg1 -> // TODO Auto-generated method stub
                true
            }
            var context: SSLContext? = null
            if (trustManagers == null) {
                trustManagers = arrayOf(FakeX509TrustManager())
            }
            try {
                context = SSLContext.getInstance("TLS")
                context.init(null, trustManagers, SecureRandom())
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(context!!.socketFactory)

            return context!!.socketFactory
        }

        fun buildSSLSocketFactory(
            context: Context,
            certRawResId: Int
        ): SSLSocketFactory {
            var keyStore: KeyStore? = null
            try {
                keyStore = buildKeyStore(context, certRawResId)
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            } catch (e: CertificateException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            var tmf: TrustManagerFactory? = null
            try {
                tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
                tmf.init(keyStore)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            }
            var sslContext: SSLContext? = null
            try {
                sslContext = SSLContext.getInstance("TLS")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            try {
                sslContext!!.init(null, tmf!!.trustManagers, null)
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
            return sslContext!!.socketFactory
        }


        @Throws(
            KeyStoreException::class,
            CertificateException::class,
            NoSuchAlgorithmException::class,
            IOException::class
        )
        private fun buildKeyStore(context: Context, certRawResId: Int): KeyStore? {
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            val cert = readCert(context, certRawResId)
            keyStore.setCertificateEntry("ca", cert)
            return keyStore
        }

        private fun readCert(context: Context, certResourceID: Int): Certificate? {
            val inputStream = context.resources.openRawResource(
                certResourceID
            )
            var ca: Certificate? = null
            var cf: CertificateFactory? = null
            try {
                cf = CertificateFactory.getInstance("X.509")
                ca = cf.generateCertificate(inputStream)
            } catch (e: CertificateException) {
                e.printStackTrace()
            }
            return ca
        }
    }
}