package cn.edu.sjtu.arf

import android.util.Log
import cn.edu.sjtu.arf.utils.FakeX509TrustManager
import cn.edu.sjtu.arf.utils.OkHttpStack
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object Constants {

    private const val Host = "101.132.97.115"
    const val serverUrl = "https://$Host/"
    private const val timeoutMill = 5000L

    internal lateinit var VolleyQueue: RequestQueue

    fun initVolleyQueue() {
        VolleyQueue = getRequestQueue()
    }

    fun getSingleQueue():RequestQueue{
        return VolleyQueue
    }

    fun getRequestQueue():RequestQueue{
        val cookieJar: ClearableCookieJar =
            PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(App.get()))

        val logging = HttpLoggingInterceptor { Log.d("OkHttp",it) }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .hostnameVerifier { _, _ -> true }
            .sslSocketFactory(
                FakeX509TrustManager.buildSSLSocketFactory(App.get(),R.raw.selfsigned),
                FakeX509TrustManager()
            )
            .connectTimeout(timeoutMill, TimeUnit.MILLISECONDS)
            .readTimeout(timeoutMill, TimeUnit.MILLISECONDS)
            .writeTimeout(timeoutMill, TimeUnit.MILLISECONDS)
            .cookieJar(cookieJar)
            .build()
//        Constants.VolleyQueue = Volley.newRequestQueue(instance, MyStack(null,FakeX509TrustManager.buildSSLSocketFactory(mInstance!!,R.raw.selfsigned)))
        return Volley.newRequestQueue(App.get(), OkHttpStack(okClient))
    }
}