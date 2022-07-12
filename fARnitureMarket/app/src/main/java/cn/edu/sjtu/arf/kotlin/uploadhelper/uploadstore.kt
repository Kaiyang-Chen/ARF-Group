package cn.edu.sjtu.arf.kotlin.uploadhelper

import android.content.Context
import android.util.Log
import cn.edu.sjtu.arf.kotlin.loginhelper.Chatt
import cn.edu.sjtu.arf.kotlin.loginhelper.MyInterface
import cn.edu.sjtu.arf.kotlin.loginhelper.loginstore
import cn.edu.sjtu.arf.kotlin.loginhelper.loginstore.myInterface
import cn.edu.sjtu.arf.kotlin.loginhelper.loginstore.strtest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.eclipsesource.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.reflect.full.declaredMemberProperties

object prodstore {
    var str = "initstr"
    var uid = "inituid"
    private val nFields = Chatt::class.declaredMemberProperties.size
    private val client = OkHttpClient()
    private lateinit var queue: RequestQueue
    private const val serverUrl = "https://101.132.97.115/"
    //private const val serverUrl = "http://10.0.2.2/"

    fun strtest(ku: String): String {
        return if (ku != null && ku.startsWith("\ufeff")) ku.substring(1) else ku
    }
    fun postprod(context: Context, productinfo: Productinfo): String {
        var a = productinfo.product_name
        var b = productinfo.primary_class
        var c = productinfo.secondary_class
        var d = productinfo.description
        var e = productinfo.color_style
        var price = productinfo.price.toString()

        val jsonObj = mapOf(
            "name" to a,
            "primary_class" to b,
            "secondary_class" to c,
            "description" to d,
            "color_style" to e,
            "price" to price
        )
        println(JSONObject(jsonObj))
        var jsonobj = JSONObject(jsonObj)
        val req = okhttp3.Request.Builder()
            .addHeader("Cookie",loginstore.cook)
            .url(serverUrl + "post_product/")
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
                        try{JSONObject(response.body?.string() ?: "")} catch (e:JSONException){
                            JSONObject()
                        }

                    println(responseReceived)
                    //str = responseReceived.toString().split(":")[1].split("}")[0]
                    str = responseReceived.getString("UID")
                    //uid = str.split(":")[1].split("}")[0]
                    println(str)
                }
            }
        })
        return str
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