package cn.edu.sjtu.arf.kotlin.uploadhelper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.NavigateActivity
import cn.edu.sjtu.arf.kotlin.ar.HelloArActivity
import cn.edu.sjtu.arf.kotlin.loginhelper.Chatt
import cn.edu.sjtu.arf.kotlin.loginhelper.RegisterActivity
import cn.edu.sjtu.arf.kotlin.loginhelper.loginstore
import cn.edu.sjtu.arf.kotlin.uploadhelper.prodstore.postprod
import com.google.ar.core.dependencies.e

class UploadPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_page)
        initListener()
    }


    fun initListener() {
        var Login_main = findViewById<Button>(R.id.publish)
        Login_main.setOnClickListener {
            var a = findViewById<EditText>(R.id.product_name).text.toString()
            var b = findViewById<EditText>(R.id.primary_class).text.toString()
            var c = findViewById<EditText>(R.id.secondary_class).text.toString()
            var d = findViewById<EditText>(R.id.description).text.toString()
            var e = findViewById<EditText>(R.id.color_style).text.toString()
            var f = findViewById<EditText>(R.id.price).text.toString()
            if (a.isNotBlank() and a.isNotBlank() and a.isNotBlank() and c.isNotBlank() and d.isNotBlank() and e.isNotBlank() and f.isNotBlank()) {
                submitprod(
                    a, b, c, d, e, f.toInt()
                )
                startActivity(Intent(this, postpicActivity::class.java))
            }else{
                Toast.makeText(this, "Please fill in the information of your product", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun Pic(view: View?) = startActivity(Intent(this, postpicActivity::class.java))
    fun submitprod(product_name:String, primary_class:String,
                   secondary_class:String, description:String,
                   color_style:String, price:Int) {
        val prod = Productinfo(
            product_name = product_name,
            primary_class = primary_class,
            secondary_class = secondary_class,
            description = description,
            color_style = color_style,
            price = price
        )
        if (postprod(applicationContext, prod) != "initstr"){
            Toast.makeText(this, "Publish Successfully", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this, "Failed, please check again", Toast.LENGTH_SHORT).show()
        }
        //finish()
        //loginstore.postregister(chatt)
    }
}