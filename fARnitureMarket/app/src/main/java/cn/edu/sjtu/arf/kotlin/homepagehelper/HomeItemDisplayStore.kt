package cn.edu.sjtu.arf.kotlin.homepagehelper

import android.util.Log
import androidx.databinding.ObservableArrayList
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemDisplay
import com.android.volley.RequestQueue
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.reflect.full.declaredMemberProperties

object HomeItemDisplayStore {
    private val _homeitemdisplays = arrayListOf<HomeItemDisplay>()
    val homeitemdisplays = ObservableArrayList<HomeItemDisplay>()
    private val nFields = HomeItemDisplay::class.declaredMemberProperties.size

    private const val serverUrl = "https://101.132.97.115/"

    private val client = OkHttpClient()

    fun getHomeItemDisplays(UID: String) {
        val jsonObj = JSONObject(mapOf("UID" to UID))
        Log.e("getHomeItemDisplays", jsonObj.toString())
        val request = Request.Builder()
            .url(serverUrl+"fetch_product_brief/")
            .post(RequestBody.create("application/json".toMediaType(), jsonObj.toString()))
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getHomeItemDisplays", "Failed GET brief information")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("getHomeItemDisplays", "Successfully GET brief information")
                    val homeItemDisplayReceived = try { JSONObject(response.body?.string() ?: "") } catch (e: JSONException) { JSONObject() }
                    homeitemdisplays.add(HomeItemDisplay(UID = UID,
                        name = homeItemDisplayReceived.getString("name"),
                        price = homeItemDisplayReceived.getString("price")))
                }
            }
        })
    }
}