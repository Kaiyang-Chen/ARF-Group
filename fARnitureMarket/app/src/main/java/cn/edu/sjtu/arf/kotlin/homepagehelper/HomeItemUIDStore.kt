package cn.edu.sjtu.arf.kotlin.homepagehelper

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.RequestQueue
import okhttp3.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.ar.core.dependencies.i
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.reflect.full.declaredMemberProperties

object HomeItemUIDStore {
    private val _homeitemUIDs = arrayListOf<HomeItemUID>()
    val homeitemUIDs: List<HomeItemUID> = _homeitemUIDs
    private val nFields = HomeItemUID::class.declaredMemberProperties.size

    private const val serverUrl = "https://101.132.97.115/"

    private val client = OkHttpClient()

    fun getHomeItemUIDs() {
        val request = Request.Builder().url(serverUrl+"fetch_home_products/").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getHomeItemUIDs", "Failed GET request")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("getHomeItemUIDs", "GET response")
                if (response.isSuccessful) {
                    val homeItemUIDReceived = try { JSONObject(response.body?.string() ?: "")} catch (e: JSONException) {JSONObject()}
                    val fullKey = homeItemUIDReceived.keys()
                    while (fullKey.hasNext()) {
                        Log.e("getHomeItemUIDs", "Received UID")
                        val idx = fullKey.next().toString()
                        if (homeItemUIDReceived.getString(idx).isNotEmpty()) {
                            _homeitemUIDs.add(
                                HomeItemUID(UID = homeItemUIDReceived.getString(idx)))
                            Log.e("getHomeItemUIDs", "Received UID")
                        } else {
                            Log.e("getHomeItemUIDs", "Received empty UID")
                        }
                    }
                }
            }
        })
    }
}