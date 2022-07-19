package cn.edu.sjtu.arf.kotlin.mehelper

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sjtu.arf.App
import cn.edu.sjtu.arf.kotlin.loginhelper.Chatt
import cn.edu.sjtu.arf.kotlin.loginhelper.loginstore
import cn.edu.sjtu.arf.kotlin.uploadhelper.prodstore
import com.android.volley.RequestQueue
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.security.AccessController.getContext

class meupdate  {
    private val client = OkHttpClient()
    private val serverUrl = "https://101.132.97.115/"
    var reminder = ""
    var username = ""
    var gender = ""
    var phone = ""
    var address = ""
    var email = ""
    fun updateinfo(context: Context, meinfo: Meinfo){
        var a = meinfo.username
        var b = meinfo.password
        var c = meinfo.gender
        var d = meinfo.phone
        var e = meinfo.address
        var f = meinfo.email

        var jsonObj = mutableMapOf(
            "username" to a,
            "password" to b,
            "gender" to c,
            "phone" to d,
            "address" to e,
            "email" to f
        )
        if (a == "" || a == null){
            jsonObj.remove("username")
        }
        if (b == "" ||b == null){
            jsonObj.remove("password")
        }
        if (c == "" ||c == null){
            jsonObj.remove("gender")

        }
        if (d == "" ||d == null){
            jsonObj.remove("phone")

        }
        if (e == "" ||e == null){
            jsonObj.remove("address")

        }
        if (f == "" ||f == null){
            jsonObj.remove("email")

        }

        println(jsonObj)
        var jsonobj = JSONObject(jsonObj as Map<*, *>?)
        val req = okhttp3.Request.Builder()
            .addHeader("Cookie", App.loginHeader?.get("Cookie")?:"")
            .url(serverUrl + "update/")
            .post(
                RequestBody.create(
                    "application/json".toMediaType(),
                    jsonobj.toString()
                )
            )
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("update", " failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("update", " successfully")
                    val responseReceived =
                        try{
                            JSONObject(response.body?.string() ?: "")
                        } catch (e: JSONException){
                            JSONObject()
                        }
                    reminder = responseReceived.getString("msg")

                }
            }
        })

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