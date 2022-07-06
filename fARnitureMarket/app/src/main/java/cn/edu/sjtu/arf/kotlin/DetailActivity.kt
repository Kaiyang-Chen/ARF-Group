package cn.edu.sjtu.arf.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val img = intent.getIntExtra("img", R.drawable.ic_launcher)
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val money = intent.getStringExtra("money")

//        binding.detailIv.setImageResource(img)
//        binding.title.text = title
//        binding.content.text = content
//        binding.money.text = "￥$money"
    }

    fun back(view: View) {
        finish()
    }

    fun addCard(view: View) {
        Toast.makeText(this, "Succussfully Added to Cart!", Toast.LENGTH_SHORT).show()
    }

    fun toChat(view: View) {
        //CartPage替换为聊天页
        startActivity(Intent(this,MainActivity::class.java))
    }
}