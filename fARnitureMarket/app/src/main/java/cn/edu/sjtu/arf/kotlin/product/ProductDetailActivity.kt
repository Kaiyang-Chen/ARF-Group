package cn.edu.sjtu.arf.kotlin.product

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.edu.sjtu.arf.App
import cn.edu.sjtu.arf.BuildConfig
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.ar.HelloArActivity
import cn.edu.sjtu.arf.networkUrl
import com.chuangsheng.face.utils.ToastUtil
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException


class ProductDetailActivity:  AppCompatActivity() {
    private lateinit var topIV: ImageView
    private lateinit var arIV: ImageView
    private lateinit var titleTV: TextView
    private lateinit var priceTV: TextView
    private lateinit var contentTV: TextView
    private lateinit var contactPhoneTV: TextView
    private lateinit var contactEmailTV: TextView

    private lateinit var sellerBtn: LinearLayout
    private lateinit var addCartBtn: LinearLayout

    private lateinit var uid: String





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_detail)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()


        uid = intent.getStringExtra(UID)?:""

        productstore.getProductDetail(uid = uid, scope = lifecycleScope,errorListener = { err ->
            if (BuildConfig.DEBUG){
                err.printStackTrace()
            }
            String(err.networkResponse.data)
            ToastUtil.show(this@ProductDetailActivity, err.message ?: "网络异常")
        }){ pro ->

            Log.i("initView","${pro.toString()}")
            titleTV.text = pro.name
            contentTV.text = pro.description
            println(pro.ic0)
            pro.price?.also { priceTV.text = String.format("%.3f",it) }
            pro.title?.also { topIV.networkUrl(it) }
            pro.phone?.also { contactPhoneTV.text = it }
            pro.email?.also { contactEmailTV.text = it }
        }

        getProductARModel()
    }

    private fun initView(){
        topIV = findViewById(R.id.top_pic)
        arIV = findViewById(R.id.ar_pic)
        titleTV = findViewById(R.id.title)
        priceTV = findViewById(R.id.price)
        contentTV = findViewById(R.id.content)

        sellerBtn = findViewById(R.id.seller_btn)
        addCartBtn = findViewById(R.id.cart_btn)
        contactPhoneTV = findViewById(R.id.contact_phone)
        contactEmailTV = findViewById(R.id.contact_email)

        arIV.setOnClickListener(::onClick)
        sellerBtn.setOnClickListener(::onClick)
        addCartBtn.setOnClickListener(::onClick)

        sellerBtn.isEnabled = false
    }


    //模型地址
    private var arModelUrl:String?=null
    private fun onClick(view: View){
        when(view){
            sellerBtn ->{

                Toast.makeText(this@ProductDetailActivity,"Contact Seller",Toast.LENGTH_SHORT).show()
            }
            addCartBtn ->{
                addToCart()
                Toast.makeText(this@ProductDetailActivity,"Add to Cart Successfully",Toast.LENGTH_SHORT).show()
            }
            arIV ->{
                arModelUrl?.let {
                    startActivity(Intent(this, HelloArActivity::class.java))
                }
                //Toast.makeText(this@ProductDetailActivity,"AR icon click",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getProductARModel(){
        productstore.getProductARModel(uid = "f38b919c-1085-11ed-8be4-df44420a944c" , scope = lifecycleScope,errorListener = { err ->
            if (BuildConfig.DEBUG){
                err.printStackTrace()
            }
            //失败回调
//            String(err.networkResponse.data)
            arIV.post {
                arIV.visibility=View.GONE
            }
//            ToastUtil.show(this@ProductDetailActivity, err.message ?: "网络异常")
        }){ pro ->
            // 成功回调
            arModelUrl =pro.ar_model
            if (arModelUrl==null){
                arIV.post {
                    arIV.visibility=View.GONE
                }
            }
            ToastUtil.show(this@ProductDetailActivity, "模型地址：${arModelUrl}")

        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object{
        const val UID:String = "uid"

        fun start(context: Context, uid: String){

            Log.i("initView","${uid.toString()}")
            context.startActivity(Intent(context,ProductDetailActivity::class.java).putExtra(UID,uid))
        }
    }

    private fun addToCart() {
        val client = OkHttpClient()
        val jsonObj = JSONObject(mapOf("UID" to uid))
        val request = Request.Builder()
                .addHeader("Cookie", App.loginHeader?.get("Cookie")?:"")
                .url("https://101.132.97.115/"+"add_to_cart/")
                .post(RequestBody.create("application/json".toMediaType(), jsonObj.toString()))
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("addCart", "Failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("addCart", "Successfully")
                }
            }
        })
    }
}