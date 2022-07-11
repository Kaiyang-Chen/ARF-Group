package cn.edu.sjtu.arf.kotlin.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.edu.sjtu.arf.R


class ProductDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
    }

    val uid = intent.extras?.getString("UID") // The UID passed from homepage
}