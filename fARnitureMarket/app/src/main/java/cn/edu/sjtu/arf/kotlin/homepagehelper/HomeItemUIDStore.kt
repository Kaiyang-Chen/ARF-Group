package cn.edu.sjtu.arf.kotlin.homepagehelper

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.google.ar.core.dependencies.i
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.full.declaredMemberProperties

object HomeItemUIDStore {
    private val _homeitemUIDs = arrayListOf<HomeItemUID>()
    val homeitemUIDs: List<HomeItemUID> = _homeitemUIDs
    private val nFields = HomeItemUID::class.declaredMemberProperties.size

    private lateinit var queue: RequestQueue
    private const val serverUrl = "https://101.132.97.115/" //IP address needs to be changed

    fun getHomeItemUIDs(context: Context, completion: () -> Unit) {
        val getRequest = JsonObjectRequest(serverUrl+"fetch_home_products/",
            { response ->
                _homeitemUIDs.clear()
                val fullidx = response.keys()
                while (fullidx.hasNext()) {
                    val idx = fullidx.next().toString()
                    if (response.getString(idx).isNotEmpty()) {
                        _homeitemUIDs.add(
                            HomeItemUID(UID = response.getString(idx)))
                    } else {
                        Log.e("getHomeItemUIDs", "Received empty UID")
                    }
                }
                completion()
            }, { completion() }
        )
    }
}