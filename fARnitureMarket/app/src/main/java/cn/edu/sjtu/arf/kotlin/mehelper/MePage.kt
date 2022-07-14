package cn.edu.sjtu.arf.kotlin.mehelper

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import cn.edu.sjtu.arf.kotlin.uploadhelper.Productinfo
import cn.edu.sjtu.arf.kotlin.uploadhelper.postpicActivity
import com.google.ar.core.dependencies.e
import kotlinx.android.synthetic.main.activity_upload_page.*
import java.security.AccessController.getContext


class MePage : AppCompatActivity() {
    lateinit var mePage: MePage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(cn.edu.sjtu.arf.R.layout.page_me)
        initListener()
    }
    fun initListener() {

        var Login_main = findViewById<Button>(cn.edu.sjtu.arf.R.id.update_me)
        Login_main.setOnClickListener {
            var a = findViewById<EditText>(cn.edu.sjtu.arf.R.id.username_me).text.toString()
            var b = findViewById<EditText>(cn.edu.sjtu.arf.R.id.password_me).text.toString()
            var c = findViewById<EditText>(cn.edu.sjtu.arf.R.id.gender_me).text.toString()
            var d = findViewById<EditText>(cn.edu.sjtu.arf.R.id.phone_me).text.toString()
            var e = findViewById<EditText>(cn.edu.sjtu.arf.R.id.address_me).text.toString()
            var f = findViewById<EditText>(cn.edu.sjtu.arf.R.id.email_me).text.toString()

            submitinfo(
                a, b, c, d, e, f
            )


        }
    }
    fun submitinfo(a : String?, b : String?,c : String?,d : String?, e : String?,f : String?){
        val info = Meinfo(
            username = a,
            password = b,
            gender = c,
            phone = d,
            address = e,
            email = f
        )
        var shit = meupdate()

        shit.updateinfo(applicationContext, info)


    }
}