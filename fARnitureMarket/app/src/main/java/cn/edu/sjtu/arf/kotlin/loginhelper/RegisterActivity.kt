package cn.edu.sjtu.arf.kotlin.loginhelper

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

        Reglogin_main.setOnClickListener {
            if (findViewById<EditText>(R.id.et_account_reg).text.toString().length > 20){
                Toast.makeText(this, "failed: username too long", Toast.LENGTH_SHORT).show()
            }
            else if (findViewById<EditText>(R.id.et_account_reg).text.toString().length < 2){
                Toast.makeText(this, "failed: username too short", Toast.LENGTH_SHORT).show()
            }
            else if ((findViewById<EditText>(R.id.et_password_reg).text.toString().length < 8) or (findViewById<EditText>(R.id.et_password_reg).text.toString().length > 20)){
                Toast.makeText(this, "failed: illegal password length: 8-20 required", Toast.LENGTH_SHORT).show()
            }
            else if ((!lowcheck(findViewById<EditText>(R.id.et_password_reg).text.toString())) or (!capcheck(findViewById<EditText>(R.id.et_password_reg).text.toString()))){
                Toast.makeText(this, "failed: a-z and A-Z characters required", Toast.LENGTH_SHORT).show()
            }
            else if (!numcheck(findViewById<EditText>(R.id.et_password_reg).text.toString())){
                Toast.makeText(this, "failed: numbers required", Toast.LENGTH_SHORT).show()
            }
            //var confirmpassword = Reglogin_main.text.toString()
            else{
                submitregister(findViewById<EditText>(R.id.et_account_reg).text.toString(), findViewById<EditText>(R.id.et_password_reg).text.toString())
                Toast.makeText(this, "Successfully register!", Toast.LENGTH_SHORT).show()
            }

            //startActivity(Intent(this, ARTest::class.java))
        }
    }

    fun capcheck(pw:String): Boolean {
        val cap = Regex("""[A-Z]""")
        if (cap.containsMatchIn(input = pw)){
            return true
            }
        return false
    }
    fun lowcheck(pw:String): Boolean {
        val cap = Regex("""[a-z]""")
        if (cap.containsMatchIn(input = pw)){
            return true
        }
        return false
    }
    fun numcheck(pw:String): Boolean {
        val cap = Regex("""[0-9]""")
        if (cap.containsMatchIn(input = pw)){
            return true
        }
        return false
    }



//    fun submitChatt(username:String, password:String) {
//        val chatt = Chatt(username = username,
//            password = password)
//
//        loginstore.postlogin(this@RegisterActivity,chatt)
//        println("success")
//    }

}