package cn.edu.sjtu.arf.kotlin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

import com.google.ar.core.ArCoreApk
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.databinding.ActivityMainBinding
import cn.edu.sjtu.arf.kotlin.ar.HelloArActivity
import cn.edu.sjtu.arf.kotlin.loginhelper.Chatt
import cn.edu.sjtu.arf.kotlin.loginhelper.RegisterActivity
import cn.edu.sjtu.arf.kotlin.loginhelper.loginstore
//import cn.edu.sjtu.arf.kotlin.loginhelper.loginstore.httpGET1
import com.google.ar.core.dependencies.e
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var view: ActivityMainBinding
    lateinit var RegUsername_main : EditText
    lateinit var RegPassword_main : EditText
    lateinit var Reglogin_main : Button
    lateinit var enterhomepage : Button
    private var password_currect = true
    //private lateinit var view: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initListener()


        // maybeEnableArButton() Check whether this device could support AR, need to use https or add android:usesCleartextTraffic="true"

    }
    fun initListener() {
        var Login_main = findViewById<Button>(R.id.Button03)

        Login_main.setOnClickListener {
            var acc = findViewById<EditText>(R.id.et_account_main).text.toString()
            var pas = findViewById<EditText>(R.id.et_password_main).text.toString()
            if (acc.isNotBlank() and pas.isNotBlank()) {
                submitlogin(acc, pas)
            }else{
                Toast.makeText(this, "Please enter the account and the password and try again", Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun SA(view: View?) = startActivity(Intent(this, HelloArActivity::class.java))
    fun Reg(view: View?) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
    fun enter(view: View?) = startActivity(Intent(this, NavigateActivity::class.java))

    //fun SB(view: View?){
    //   startActivity(Intent(this, PostActivity::class.java))
    //    Toast.makeText(this, "Log In Successfully" , Toast.LENGTH_SHORT).show()
    //}

    fun submitlogin(username:String, password:String) {
        val chatt = Chatt(username = username,
            password = password)
        if (loginstore.postlog(applicationContext, chatt)){
            startActivity(Intent(this, NavigateActivity::class.java))
        }else{
            Toast.makeText(this, "Wrong password or username", Toast.LENGTH_SHORT).show()
        }
        //finish()
        //loginstore.postregister(chatt)
    }
    private fun initViews() {
        RegUsername_main = findViewById<EditText>(R.id.et_account_main)
        RegPassword_main = findViewById<EditText>(R.id.et_password_main)
        Reglogin_main = findViewById<Button>(R.id.Button01_main)
        enterhomepage = findViewById<Button>(R.id.Buttonenterhomepage)
    }
    fun maybeEnableArButton() {
        val availability = ArCoreApk.getInstance().checkAvailability(this)
        if (availability.isTransient) {
            // Continue to query availability at 5Hz while compatibility is checked in the background.
            Handler(Looper.getMainLooper()).postDelayed({
                maybeEnableArButton()
            }, 200)
        }
        if (availability.isSupported) {
            Reglogin_main.visibility = View.VISIBLE
            Reglogin_main.isEnabled = true
            val toast = Toast.makeText(applicationContext, "AR available", Toast.LENGTH_SHORT)
            toast.show()
        } else { // The device is unsupported or unknown.
            Reglogin_main.visibility = View.INVISIBLE
            Reglogin_main.isEnabled = false
        }
    }
}