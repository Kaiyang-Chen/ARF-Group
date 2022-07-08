package cn.edu.sjtu.arf.kotlin.product

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
import cn.edu.sjtu.arf.BuildConfig
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.networkUrl
import com.chuangsheng.face.utils.ToastUtil

/**
 *
 * @Author:         zhozicho
 * @Date:     2022/7/6 10:45
 * @Desc:
 */
class ProductDetailActivity:  AppCompatActivity() {
    private lateinit var topIV: ImageView
    private lateinit var arIV: ImageView
    private lateinit var titleTV: TextView
    private lateinit var priceTV: TextView
    private lateinit var contentTV: TextView

    private lateinit var sellerBtn: LinearLayout
    private lateinit var addCartBtn: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_detail)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()

        productstore.getProductDetail(uid = "81a5f3aa-fd9e-11ec-9629-4b19c64262c0", scope = lifecycleScope,errorListener = { err ->
            if (BuildConfig.DEBUG){
                err.printStackTrace()
            }
            String(err.networkResponse.data)
            ToastUtil.show(this@ProductDetailActivity, err.message ?: "网络异常")
        }){ pro ->
            titleTV.text = pro.name
            contentTV.text = pro.description

            pro.price?.also { priceTV.text = String.format("%.3f",it) }
            pro.picture?.also { topIV.networkUrl(pro.picture!!) }
        }
    }

    private fun initView(){
        topIV = findViewById(R.id.top_pic)
        arIV = findViewById(R.id.ar_pic)
        titleTV = findViewById(R.id.title)
        priceTV = findViewById(R.id.price)
        contentTV = findViewById(R.id.content)

        sellerBtn = findViewById(R.id.seller_btn)
        addCartBtn = findViewById(R.id.cart_btn)

        arIV.setOnClickListener(::onClick)
        sellerBtn.setOnClickListener(::onClick)
        addCartBtn.setOnClickListener(::onClick)

        sellerBtn.isEnabled = false
    }

    private fun onClick(view: View){
        when(view){
            sellerBtn ->{
                Toast.makeText(this@ProductDetailActivity,"Contact Seller",Toast.LENGTH_SHORT).show()
            }
            addCartBtn ->{
                Toast.makeText(this@ProductDetailActivity,"Add to Cart",Toast.LENGTH_SHORT).show()
            }
            arIV ->{
                Toast.makeText(this@ProductDetailActivity,"AR icon click",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}