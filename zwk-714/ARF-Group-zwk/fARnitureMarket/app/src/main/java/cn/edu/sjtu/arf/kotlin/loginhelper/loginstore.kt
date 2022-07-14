package cn.edu.sjtu.arf.kotlin.loginhelper

import android.content.Context
import android.util.Log
import cn.edu.sjtu.arf.App
import cn.edu.sjtu.arf.Constants
import cn.edu.sjtu.arf.utils.FakeX509TrustManager
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.full.declaredMemberProperties

object loginstore {
    private val _chatts = arrayListOf<Chatt>()
    val chatts: List<Chatt> = _chatts
    private val nFields = Chatt::class.declaredMemberProperties.size
    private const val serverUrl = Constants.serverUrl

    fun strtest(ku: String): String {
        return if (ku != null && ku.startsWith("\ufeff")) ku.substring(1) else ku
    }

    fun postChatt(context: Context, chatt: Chatt) {
        var ku = chatt.username?.let { strtest(it) }
        var kp = chatt.password?.let { strtest(it) }
        //println(context)
        //println(ku)
        //println(kp)
        val jsonObj = mapOf(
            "username" to ku,
            "password" to kp
        )
        //println(jsonObj)
        //println(JSONObject(jsonObj))
        var tempjson = JSONObject(jsonObj)
        //println(tempjson)
        //println(tempjson is JSONObject)
        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            serverUrl + "register/", tempjson,
            { Log.d("postreg", "login msg posted!") },
            { error -> Log.e("postreg", error.localizedMessage ?: "JsonObjectRequest error") }
        )
        //println(postRequest)
        //Thread.sleep(10000)
        Constants.VolleyQueue.add(postRequest)
    }


    fun postlogin(context: Context,
                  chatt: Chatt,
                  errorListener : Response.ErrorListener,
                  listener: Response.Listener<JSONObject>
    ) {
        val jsonObj = mapOf(
            "username" to chatt.username,
            "password" to chatt.password
        )
        FakeX509TrustManager.allowAllSSL()
        val postRequest = object : JsonObjectRequest(
            Request.Method.POST,
            serverUrl + "login/",
            JSONObject(jsonObj),
           listener,
            errorListener
        ) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
                App.loginHeader = mutableMapOf()
                var str = "";
                 response.allHeaders?.filter { it.name == "Set-Cookie"}?.forEach { header ->
                     val strs = header.value.split(";")
                     strs.filter { it.isNotEmpty() }.let {itt ->
                         str += itt[0]
                         str +="; "
                     }
                 }
                App.loginHeader?.put("Cookie",str.substring(0,str.length-2))
                return super.parseNetworkResponse(response)
            }
        }

        Constants.VolleyQueue.add(postRequest)

    }

}