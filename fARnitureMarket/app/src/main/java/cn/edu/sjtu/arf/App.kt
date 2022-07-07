package cn.edu.sjtu.arf

import android.app.Application
import cn.edu.sjtu.arf.utils.FakeX509TrustManager
import cn.edu.sjtu.arf.utils.MyStack
import com.android.volley.toolbox.Volley
/**
  *
  * @Author:   zhozicho
  * @Date:     2022/7/6 15:15
  * @Desc:
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (mInstance == null) {
            mInstance = this
        }

        Constants.VolleyQueue = Volley.newRequestQueue(instance, MyStack(null,FakeX509TrustManager.buildSSLSocketFactory(
            mInstance!!,R.raw.selfsigned)))
    }

    companion object{
        var loginHeader: MutableMap<String, String>? = null

        private var mInstance: App? = null
        val instance: App
            get() = mInstance!!
    }
}