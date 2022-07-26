package cn.edu.sjtu.arf.kotlin.checkouthelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sjtu.arf.App
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.NavigateActivity
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemDisplayStore
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemDisplayStore.totalPrice
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemUIDStore
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemUIDStore.cartitemUIDs
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class CheckoutActivity : AppCompatActivity() {
    lateinit var address: EditText
    lateinit var checkoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()

        listener()
    }

    private fun initViews() {
        address = findViewById<EditText>(R.id.pay_address)
        checkoutButton = findViewById<Button>(R.id.pay)
        checkoutButton.text = "Confirm and Pay ¥ " + totalPrice.toString().format("%.1f")
    }

    private fun listener() {
        checkoutButton.setOnClickListener(View.OnClickListener {
            if (address.text.toString().isEmpty()) {
                Toast.makeText(this, "Empty Address", Toast.LENGTH_SHORT).show()
            }
            else {
                val client = OkHttpClient()
                var flag = true

                for (i in cartitemUIDs) {
                    val jsonObj = JSONObject(mapOf("UID" to i.UID))
                    val request = Request.Builder()
                            .addHeader("Cookie", App.loginHeader?.get("Cookie")?:"")
                            .url("https://101.132.97.115/"+"buy_product/")
                            .post(RequestBody.create("application/json".toMediaType(), jsonObj.toString()))
                            .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("Buy","Failed")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            Log.e("Buy","Get response")
                            val received = try { JSONObject(response.body?.string() ?: "") } catch (e: JSONException) { JSONObject() }
                            println(received.toString())
                            if (received.toString().contains("url")) {
                                Log.e("Buy","URL")
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                intent.setData(Uri.parse(received.getString("url")))
                                startActivity(intent)
                            } else {
                                if (flag) {
                                    Handler(Looper.getMainLooper()).post(Runnable {
                                        Toast.makeText(this@CheckoutActivity,
                                                "Paied Successfully", Toast.LENGTH_SHORT).show()
                                    })
                                }
                                flag = false
                            }

                            val deleteRequest = Request.Builder()
                                    .addHeader("Cookie", App.loginHeader?.get("Cookie")?:"")
                                    .url("https://101.132.97.115/"+"delete_from_cart/")
                                    .post(RequestBody.create("application/json".toMediaType(), jsonObj.toString()))
                                    .build()

                            client.newCall(deleteRequest).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    Log.e("Delete","Failed")
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    Log.e("Delete","Get response")
                                    CartItemDisplayStore.cartitemdisplays.clear()
                                    totalPrice = 0.0
                                    CartItemUIDStore.getCartItemUIDs()
                                }
                            })
                        }
                    })
                }
            }
            startActivity(Intent(this , NavigateActivity::class.java))
        })
    }

    private fun jump() {
        startActivity(Intent(this , NavigateActivity::class.java))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}