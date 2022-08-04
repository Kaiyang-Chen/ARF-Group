package cn.edu.sjtu.arf.kotlin.searchhelper

import android.util.Log
import androidx.databinding.ObservableArrayList
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object SearchItemDisplayStore {
    private val _searchitemdisplays = arrayListOf<SearchItemDisplay>()
    val searchitemdisplays = ObservableArrayList<SearchItemDisplay>()

    private const val serverUrl = "https://101.132.97.115/"

    private val client = OkHttpClient()

    fun getSearchItemDisplays(UID: String) {
        val jsonObj = JSONObject(mapOf("UID" to UID))
        Log.e("getSearchItemDisplays", jsonObj.toString())
        val request = Request.Builder()
                .url(serverUrl+"fetch_product_brief/")
                .post(RequestBody.create("application/json".toMediaType(), jsonObj.toString()))
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getSearchItemDisplays", "Failed GET brief information")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("getSearchItemDisplays", "Successfully GET response")
                    val searchItemDisplayReceived = try {
                        JSONObject(response.body?.string() ?: "")
                    } catch (e: JSONException) { JSONObject() }
                    if (searchItemDisplayReceived.toString().contains("picture")) {
                        searchitemdisplays.add(SearchItemDisplay(UID = UID,
                        name = searchItemDisplayReceived.getString("name"),
                        price = searchItemDisplayReceived.getString("price"),
                        imageUrl = searchItemDisplayReceived.getString("picture")))
                    } else {
                        searchitemdisplays.add(SearchItemDisplay(UID = UID,
                                name = searchItemDisplayReceived.getString("name"),
                                price = searchItemDisplayReceived.getString("price")))
                    }
                }
            }
        })
    }
}