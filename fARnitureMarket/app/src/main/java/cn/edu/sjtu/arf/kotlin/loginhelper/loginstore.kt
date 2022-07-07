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

    private lateinit var queue: RequestQueue
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
        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
            //println("yes, there is a problem")
        }
        println(postRequest)
        //println(postRequest.toString())
        queue.add(postRequest)
    }

    fun postregister(context: Context, chatt: Chatt) {
        val jsonObj = mapOf(
            "username" to chatt.username,
            "password" to chatt.password
        )
        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            serverUrl + "register/", JSONObject(jsonObj),
            { Log.d("postregister", "register msg posted!") },
            { error -> Log.e("postregister", error.localizedMessage ?: "JsonObjectRequest error") }
        )
        println(jsonObj)
        println(postRequest)

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
//                App.loginHeader = response.headers?.filterKeys { it != "Content-Length" }
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
                App.loginHeader?.put("Host",Constants.Host)
//                App.loginHeader?.put("Referer","same-origin")
//                response.headers?.filterKeys { it != "Content-Length" }
//                    ?.let { App.loginHeader?.putAll(it) }
                return super.parseNetworkResponse(response)
            }
        }

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(postRequest)

    }

    fun getChatts(context: Context, completion: () -> Unit) {
        val getRequest = JsonObjectRequest(serverUrl + "getchatts/",
            { response ->
                _chatts.clear()
                val chattsReceived = try {
                    response.getJSONArray("chatts")
                } catch (e: JSONException) {
                    JSONArray()
                }
                for (i in 0 until chattsReceived.length()) {
                    val chattEntry = chattsReceived[i] as JSONArray
                    if (chattEntry.length() == nFields) {
                        _chatts.add(
                            Chatt(
                                username = chattEntry[0].toString(),
                                password = chattEntry[1].toString(),
                            )
                        )
                    } else {
                        Log.e(
                            "getChatts",
                            "Received unexpected number of fields: " + chattEntry.length()
                                .toString() + " instead of " + nFields.toString()
                        )
                    }
                }
                completion()
            }, { completion() }
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(getRequest)
    }
}