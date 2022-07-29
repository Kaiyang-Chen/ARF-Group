package cn.edu.sjtu.arf.kotlin.ar

import android.util.Log
import androidx.databinding.ObservableArrayList
import cn.edu.sjtu.arf.Constants
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

object ARModelStore {

    val arModelDisplay = ARModelDisplay()
    private const val serverUrl = Constants.serverUrl
    private val client = OkHttpClient()

    fun getARModel(UID: String) {
        val jsonObj = JSONObject(mapOf("UID" to UID))
        Log.e("getARModel", jsonObj.toString())
        val request = Request.Builder()
            .url(serverUrl+"fetch_ar_model/")
            .post(RequestBody.create("application/json".toMediaType(), jsonObj.toString()))
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getARModel", "Failed GET getARModel")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val arModelreceived = try { JSONObject(response.body?.string() ?: "") } catch (e: JSONException) { JSONObject() }
                    Log.e("getARModel", arModelreceived.getString("name"))
                    arModelDisplay.UID = UID
                    arModelDisplay.name = arModelreceived.getString("name")
                    arModelDisplay.textureUrl =  arModelreceived.getString("texture")
                    arModelDisplay.modelUrl =  arModelreceived.getString("ar_model")
                }
            }
        })
    }
}