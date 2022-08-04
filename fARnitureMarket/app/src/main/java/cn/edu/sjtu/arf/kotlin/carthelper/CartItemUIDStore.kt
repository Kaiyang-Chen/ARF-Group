package cn.edu.sjtu.arf.kotlin.carthelper

import android.util.Log
import android.widget.Button
import cn.edu.sjtu.arf.App
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object CartItemUIDStore {
    private val _cartitemUIDs = arrayListOf<CartItemUID>()
    val cartitemUIDs: List<CartItemUID> = _cartitemUIDs

    private const val serverUrl = "https://101.132.97.115/"

    private val client = OkHttpClient()

    fun getCartItemUIDs() {
        val request = Request.Builder()
                .addHeader("Cookie", App.loginHeader?.get("Cookie")?:"")
                .url(serverUrl+"get_cart/")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getCartItemUIDs", "Failed")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("getCartItemUIDs", "GET response")
                if (response.isSuccessful) {
                    _cartitemUIDs.clear()
                    val cartItemUIDReceived = try { JSONObject(response.body?.string() ?: "")
                    } catch (e: JSONException) {
                        JSONObject()
                    }
                    val fullKey = cartItemUIDReceived.keys()
                    while (fullKey.hasNext()) {
                        Log.e("getCartItemUIDs", "Received UID")
                        val idx = fullKey.next().toString()
                        if (cartItemUIDReceived.getString(idx).isNotEmpty()) {
                            _cartitemUIDs.add(
                                    CartItemUID(UID = cartItemUIDReceived.getString(idx)))
                            CartItemDisplayStore.getCartItemDisplays(cartItemUIDReceived.getString(idx))
                            Log.e("getCartItemUIDs", "Add Successfully")
                        } else {
                            Log.e("getCartItemUIDs", "Received empty UID")
                        }
                    }
                }
            }
        })
    }
}