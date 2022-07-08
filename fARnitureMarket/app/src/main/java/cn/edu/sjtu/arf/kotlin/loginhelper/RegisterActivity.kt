package cn.edu.sjtu.arf.kotlin.loginhelper

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.content.ContextCompat.startActivity
import cn.edu.sjtu.arf.R




class RegisterActivity : AppCompatActivity(){
    //private lateinit var view: ActivityPostBinding
    //private lateinit var view: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //view = ActivityPostBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_register)
        //println(findViewById<EditText>(R.id.et_account_reg).text.toString())
        initListener()
        //submitregister("74kottest4", "Asdf888822")
        //setContentView(view.root)
        //initListener()
        //sendPostRequest("cftest", "Asdf8888")
    }

    fun submitregister(username:String, password:String) {
        val chatt = Chatt(username = username,
            password = password)
        loginstore.postChatt(applicationContext, chatt)
        finish()
        //loginstore.postregister(chatt)
    }
    fun initListener() {
        //println(findViewById<EditText>(R.id.et_account_reg).text.toString())
        var Reglogin_main = findViewById<Button>(R.id.Button01_reg)
        var username = findViewById<EditText>(R.id.et_account_reg).text.toString()
        var password = findViewById<EditText>(R.id.et_password_reg).text.toString()
        //println(username)
        //println(findViewById<EditText>(R.id.et_password_reg).text.toString())
        Reglogin_main.setOnClickListener {
            println(findViewById<EditText>(R.id.et_account_reg).text.toString())
            println(findViewById<EditText>(R.id.et_password_reg).text.toString())
            //println(username)
            //println(password)
            //var confirmpassword = Reglogin_main.text.toString()
            submitregister(findViewById<EditText>(R.id.et_account_reg).text.toString(), findViewById<EditText>(R.id.et_password_reg).text.toString())
            //startActivity(Intent(this, ARTest::class.java))
        }
    }

//    fun submitChatt(username:String, password:String) {
//        val chatt = Chatt(username = username,
//            password = password)
//
//        loginstore.postlogin(this@RegisterActivity,chatt)
//        println("success")
//    }
}