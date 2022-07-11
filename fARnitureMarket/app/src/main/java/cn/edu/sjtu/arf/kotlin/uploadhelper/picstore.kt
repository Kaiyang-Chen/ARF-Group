package cn.edu.sjtu.arf.kotlin.uploadhelper

import android.content.Context
import android.net.Uri
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
import com.google.ar.core.dependencies.e
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.reflect.full.declaredMemberProperties

object picstore {
    private var cook = loginstore.cook
    private const val serverUrl = "https://101.132.97.115/"
    //private const val serverUrl = "http://10.0.2.2/"
    private val client = OkHttpClient()
    fun postpic(
        context: Context,
        uid: String?,
        pic_name: String?,
        imageUri: Uri?,
        completion: (String) -> Unit
    ){
        val mpFD = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("UID", uid ?: "")
            .addFormDataPart("picture", pic_name ?: "")

        imageUri?.run {
            toFile(context)?.let {
                mpFD.addFormDataPart("image", "chattImage",
                    it.asRequestBody("image/jpeg".toMediaType()))
            } ?: context.toast("Unsupported image format")
        }

        println(mpFD)
        //println(mpFD.build())
        val request = okhttp3.Request.Builder()
            .addHeader("Cookie",loginstore.cook)
            .url(serverUrl + "post_picture/")
            .post(mpFD.build())
            .build()

        /*val mpFD = mapOf(
            "UID" to loginstore.cook,
            "picture" to "wxy",
            "image" to imageUri?.run{
                toFile(context)
            }
        )

        var temp = JSONObject(mpFD)
        println(temp)
                /*val mpFD = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("UID", loginstore.cook ?: "")
                    .addFormDataPart("picture", "wxy" ?: "")*/

        val request = okhttp3.Request.Builder()
            .addHeader("Cookie",loginstore.cook)
            .url(serverUrl + "post_picture/")
            .post(RequestBody.create(
                "application/json".toMediaType(),
                temp.toString()
            ))
            .build()*/

            context.toast("Posting . . . wait for 'Chatt posted!'")

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    completion(e.localizedMessage ?: "Posting failed")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseReceived =
                            try{JSONObject(response.body?.string() ?: "")} catch (e:JSONException){
                                JSONObject()
                            }
                        println("ffffffffff")
                        println(responseReceived)
                        prodstore.str = responseReceived.toString()
                        println(prodstore.str)
                        completion("Chatt posted!")
                    }
                }
            })
        }
    }

