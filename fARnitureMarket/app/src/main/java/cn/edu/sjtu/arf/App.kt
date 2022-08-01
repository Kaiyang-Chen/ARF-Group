package cn.edu.sjtu.arf

import android.app.Application
import android.util.Log
import cn.edu.sjtu.arf.kotlin.ar.ARModelStore
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
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (mInstance == null) {
            mInstance = this
        }
        ARModelStore.getARModel("f38b919c-1085-11ed-8be4-df44420a944c")
        Constants.initVolleyQueue()
    }

    fun getDefaultQueue():RequestQueue{
        return Constants.VolleyQueue
    }

    companion object{
        var loginHeader: MutableMap<String, String>? = null

        private var mInstance: App? = null
        fun get(): App{
            return mInstance!!
        }
    }
}