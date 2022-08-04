package cn.edu.sjtu.arf.kotlin.carthelper

import android.util.Log
import androidx.databinding.ObservableArrayList
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemDisplay
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemDisplayStore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object CartItemDisplayStore {
    val cartitemdisplays = ObservableArrayList<CartItemDisplay>()
    var totalPrice = 0.0

    private const val serverUrl = "https://101.132.97.115/"

    private val client = OkHttpClient()

    fun getCartItemDisplays(UID: String) {
        val jsonObj = JSONObject(mapOf("UID" to UID))
        val request = Request.Builder()
                .url(serverUrl+"fetch_product_brief/")
                .post(RequestBody.create("application/json".toMediaType(), jsonObj.toString()))
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getCartDisplays", "Failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("getCartDisplays", "Successfully GET brief information")
                    val cartItemDisplayReceived = try { JSONObject(response.body?.string() ?: "") } catch (e: JSONException) { JSONObject() }
                    if (cartItemDisplayReceived.toString().contains("picture")) {
                        cartitemdisplays.add(CartItemDisplay(UID = UID,
                                name = cartItemDisplayReceived.getString("name"),
                                price = cartItemDisplayReceived.getString("price"),
                                imageUrl = cartItemDisplayReceived.getString("picture")))
                        totalPrice += cartItemDisplayReceived.getString("price").toDouble()
                    } else {
                        cartitemdisplays.add(
                            CartItemDisplay(
                                UID = UID,
                                name = cartItemDisplayReceived.getString("name"),
                                price = cartItemDisplayReceived.getString("price")
                            )
                        )
                        totalPrice += cartItemDisplayReceived.getString("price").toDouble()
                    }
                }
            }
        })
    }
}