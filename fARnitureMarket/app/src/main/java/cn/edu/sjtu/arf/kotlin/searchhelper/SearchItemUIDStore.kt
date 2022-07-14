package cn.edu.sjtu.arf.kotlin.searchhelper

import android.util.Log
import cn.edu.sjtu.arf.App
import cn.edu.sjtu.arf.kotlin.searchhelper.SearchItemDisplayStore.getSearchItemDisplays
import okhttp3.OkHttpClient
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object SearchItemUIDStore {
    private val _searitemUIDS = arrayListOf<SearchItemUID>()
    val searchItemUIDs: List<SearchItemUID> = _searitemUIDS

    private const val serverUrl = "https://101.132.97.115/"

    private val client = OkHttpClient()

    fun getSearchItemUIDs(keywords: String) {
        val jsonObj = JSONObject(mapOf("keywords" to keywords))
        val request = Request.Builder()
                .addHeader("Cookie", App.loginHeader?.get("Cookie")?:"")
                .url(serverUrl+"fetch_searched_products/")
                .post(RequestBody.create("application/json".toMediaType(), jsonObj.toString()))
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getSearchItemUID", "Failed GET search item UID")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("getSearchItemUID", "Successfully GET search item UID")
                    _searitemUIDS.clear()
                    val searchItemUIDReceived = try {
                        JSONObject(response.body?.string() ?: "")
                    } catch (e: JSONException) {
                        JSONObject()
                    }
                    val fullKey = searchItemUIDReceived.keys()
                    while (fullKey.hasNext()) {
                        Log.e("getSearchItemUID", "Received UID")
                        val idx = fullKey.next().toString()
                        if (searchItemUIDReceived.getString(idx).isNotEmpty()) {
                            _searitemUIDS.add(SearchItemUID(UID = searchItemUIDReceived.getString(idx)))
                            getSearchItemDisplays(searchItemUIDReceived.getString(idx))
                            Log.e("getSearchItemUIDS", "Add Successfully")
                        } else {
                            Log.e("getSearchItemUIDS", "Received empty UID")
                        }
                    }
                }
            }
        })
    }
}