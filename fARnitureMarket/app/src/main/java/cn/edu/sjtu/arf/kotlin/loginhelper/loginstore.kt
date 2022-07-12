package cn.edu.sjtu.arf.kotlin.loginhelper

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import cn.edu.sjtu.arf.kotlin.NavigateActivity
import cn.edu.sjtu.arf.kotlin.ar.HelloArActivity
import cn.edu.sjtu.arf.kotlin.uploadhelper.Productinfo
import cn.edu.sjtu.arf.kotlin.uploadhelper.prodstore
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.reflect.full.declaredMemberProperties

interface MyInterface{
    fun onCallback(response:String): Boolean
}

object loginstore : MyInterface{
    val myInterface = this
    var str = "initstr"
    override fun onCallback(response: String): Boolean {
        return response == "{}"
    }
    public var cook = ""
    private val _chatts = arrayListOf<Chatt>()
    val chatts: List<Chatt> = _chatts
    private val nFields = Chatt::class.declaredMemberProperties.size
    private val client = OkHttpClient()
    private lateinit var queue: RequestQueue
    private const val serverUrl = "https://101.132.97.115/"

    fun strtest(ku: String): String {
        return if (ku != null && ku.startsWith("\ufeff")) ku.substring(1) else ku
    }

    fun postChatt(context: Context, chatt: Chatt) {
        var ku = chatt.username?.let { strtest(it) }
        var kp = chatt.password?.let { strtest(it) }
        val jsonObj = mapOf(
            "username" to ku,
            "password" to kp
        )

        var tempjson = JSONObject(jsonObj)
        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            serverUrl+"register/", tempjson,
            { Log.d("postreg", "login msg posted!") },
            { error -> Log.e("postreg", error.localizedMessage ?: "JsonObjectRequest error") }
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(postRequest)
    }

    /*fun postlogin(context: Context,chatt: Chatt): Boolean {
        var ku = chatt.username?.let { strtest(it) }
        var kp = chatt.password?.let { strtest(it) }
        val jsonObj = mapOf(
            "username" to ku,
            "password" to kp
        )

        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            serverUrl+"login/", JSONObject(jsonObj),
            { response ->
                str = response.toString()
                //println(response.getString("code"))
                if (myInterface.onCallback("$str")){
                    println("login successfully")
                }
                else{
                    //throw Exception("wrong password")
                    println(str)
                }
                Log.d("postlogin", "response: $str")},
            { error -> Log.e("postreg", error.localizedMessage ?: "JsonObjectRequest error") }
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        println(myInterface.onCallback("{}"))
        println(myInterface)
        println(postRequest)
        queue.add(postRequest)
        return str == "{}"
    }*/

    fun postlog(context: Context, chatt: Chatt): Boolean {
        var ku = chatt.username?.let { strtest(it) }
        var kp = chatt.password?.let { strtest(it) }
        val jsonObj = mapOf(
            "username" to ku,
            "password" to kp
        )

        println(JSONObject(jsonObj))
        var jsonobj = JSONObject(jsonObj)
        val req = okhttp3.Request.Builder()
            .url(loginstore.serverUrl + "login/")
            .post(
                RequestBody.create(
                    "application/json".toMediaType(),
                    jsonobj.toString()
                )
            )
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Publish", " failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("Publish", " successfully")
                    val responseReceived =
                        try{JSONObject(response.body?.string() ?: "")} catch (e: JSONException){
                            JSONObject()
                        }
                    prodstore.str = responseReceived.toString()
                    println(prodstore.str)
                    val cookie = response.headers.values("Set-Cookie").toString()
                        //.split(";")[0]
                    cook =  cookie.split(";")[4].split(",")[1].substring(1)
                }
            }
        })

        return prodstore.str == "{}"
        /*val postRequest = JsonObjectRequest(
            Request.Method.POST,
            serverUrl+"post_product/", JSONObject(jsonObj),
            { response ->
                println(response)
                str = response.getString("UID")
                println(response)
                Log.d("postprod", "response: $str")},
            { error -> Log.e("postprod", error.localizedMessage ?: "JsonObjectRequest error") }
        )
        if (!this::queue.isInitialized) {
            queue = Volley.newRequestQueue(context)
        }
        queue.add(postRequest)*/

    }
}