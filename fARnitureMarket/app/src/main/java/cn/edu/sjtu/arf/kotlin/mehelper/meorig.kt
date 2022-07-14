package cn.edu.sjtu.arf.kotlin.mehelper

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import cn.edu.sjtu.arf.App
import cn.edu.sjtu.arf.BuildConfig
import cn.edu.sjtu.arf.Constants.serverUrl
import cn.edu.sjtu.arf.kotlin.ar.HelloArActivity
import cn.edu.sjtu.arf.kotlin.product.ProductDetailActivity
import cn.edu.sjtu.arf.kotlin.product.productstore

import cn.edu.sjtu.arf.kotlin.uploadhelper.Productinfo
import cn.edu.sjtu.arf.kotlin.uploadhelper.postpicActivity
import cn.edu.sjtu.arf.networkUrl
import com.chuangsheng.face.utils.ToastUtil
import com.google.ar.core.dependencies.e
import kotlinx.android.synthetic.main.activity_upload_page.*
import kotlinx.android.synthetic.main.page_me_orig.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.security.AccessController.getContext


class meorig : AppCompatActivity() {
    private lateinit var username: TextView
    private lateinit var gender: TextView
    private lateinit var phone: TextView
    private lateinit var address: TextView
    private lateinit var email: TextView


    private val client = OkHttpClient()
    private val serverUrl = "https://101.132.97.115/"
    var shi = meupdate()
    lateinit var mePage_orig: meorig
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(cn.edu.sjtu.arf.R.layout.page_me_orig)
        supportActionBar?.setHomeAsUpIndicator(cn.edu.sjtu.arf.R.drawable.ic_action_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()

        meshit.getProductDetail(scope = lifecycleScope,errorListener = { err ->
            if (BuildConfig.DEBUG){
                err.printStackTrace()
            }
            String(err.networkResponse.data)
            ToastUtil.show(this, err.message ?: "网络异常")
        }){ pro ->
            username.text = pro.username
            gender.text = pro.gender
            println(pro.gender)
            phone.text = pro.phone
            address.text = pro.address
            email.text = pro.email
        }
        //initlistener()
    }
    fun initlistener(){
        var Login_main = findViewById<Button>(cn.edu.sjtu.arf.R.id.update_orig)
        Login_main.setOnClickListener {
            startActivity(Intent(this, meupdate::class.java))
        }
    }
    fun up(view: View?) = startActivity(Intent(this, MePage::class.java))
    /*fun refresh(){
        var refresh = requireView().findViewById<View>(cn.edu.sjtu.arf.R.id.refresh_orig) as Button
        refresh.setOnClickListener {
            submitinfo("", "", "", "", "", "")
            var a = requireView().findViewById<TextView>(cn.edu.sjtu.arf.R.id.username_orig)
            a.text = shi.username
            var b = requireView().findViewById<TextView>(cn.edu.sjtu.arf.R.id.gender_orig)
            b.text = shi.gender
            var c = requireView().findViewById<TextView>(cn.edu.sjtu.arf.R.id.phone_orig)
            c.text = shi.phone
            var d = requireView().findViewById<TextView>(cn.edu.sjtu.arf.R.id.address_orig)
            d.text = shi.address
            var e = requireView().findViewById<TextView>(cn.edu.sjtu.arf.R.id.email_orig)
            e.text = shi.email
            println(shi.email)
            println(shi.address)
        }
    }*/
    fun initView(){
        username = findViewById(cn.edu.sjtu.arf.R.id.username_orig)
        gender = findViewById(cn.edu.sjtu.arf.R.id.gender_orig)
        phone = findViewById(cn.edu.sjtu.arf.R.id.phone_orig)
        address = findViewById(cn.edu.sjtu.arf.R.id.address_orig)
        email = findViewById(cn.edu.sjtu.arf.R.id.email_orig)


    }
    //fun up(view: View?) = startActivity(Intent(this, meupdate::class.java))
    /*fun submitinfo(a : String?, b : String?,c : String?,d : String?, e : String?,f : String?){
        val info = Meinfo(
            username = a,
            gender = c,
            phone = d,
            address = e,
            email = f
        )
        var shit = meorig()
        var shi2 = getContext()

        if (shi2 != null){
            var contextd = shi2.getApplicationContext()
            shit.updateinfo(contextd, info)
        }

    }*/
    fun updateinfo(context: Context, meinfo: Meinfo){
        var a = meinfo.username
        var b = meinfo.password
        var c = meinfo.gender
        var d = meinfo.phone
        var e = meinfo.address
        var f = meinfo.email

        var jsonObj = mapOf(
            "username" to a,
            "password" to b,
            "gender" to c,
            "phone" to d,
            "address" to e,
            "email" to f
        )

        println(jsonObj)
        var jsonobj = JSONObject(jsonObj)
        val req = okhttp3.Request.Builder()
            .addHeader("Cookie", App.loginHeader?.get("Cookie")?:"")
            .url(serverUrl + "check/")
            .post(
                RequestBody.create(
                    "application/json".toMediaType(),
                    jsonobj.toString()
                )
            )
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("load", " failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("load", " successfully")
                    val responseReceived =
                        try{
                            JSONObject(response.body?.string() ?: "")
                        } catch (e: JSONException){
                            JSONObject()
                        }

                    shi.username = responseReceived.getString("username")
                    shi.gender = responseReceived.getString("gender")
                    shi.phone = responseReceived.getString("phone")
                    shi.address = responseReceived.getString("address")
                    shi.email = responseReceived.getString("email")
                    println(shi.email)
                    println(shi.address)
                }
            }
        })

        /*val postRequest = JsonObjectRequest(
            Request.Method.POST,
            serverUrl+"post_product/", JSONObject(jsonObj),
            { response ->
                println(response)
                str = response.getString("UID")
                println(response)
                Log.d("postprod", "response: $str")},
            { error -> Log.e("postprod", error.localizedMessage ?: "JsonObjectRequest error") }
        )
        if (!this::queue.isInitialized) {
            queue = Volley.newRequestQueue(context)
        }
        queue.add(postRequest)*/

    }
}