package cn.edu.sjtu.arf.kotlin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope

//import com.google.ar.core.ArCoreApk
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.ar.HelloArActivity
import cn.edu.sjtu.arf.kotlin.loginhelper.Chatt
import cn.edu.sjtu.arf.kotlin.loginhelper.RegisterActivity
import cn.edu.sjtu.arf.kotlin.loginhelper.loginstore
import com.chuangsheng.face.utils.ToastUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    lateinit var RegUsername_main : EditText
    lateinit var RegPassword_main : EditText
    lateinit var Reglogin_main : Button
    lateinit var enterhomepage : Button
    lateinit var loginBtn : Button
    private var password_currect = true
    //private lateinit var view: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        // maybeEnableArButton() Check whether this device could support AR, need to use https or add android:usesCleartextTraffic="true"

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


    private fun initViews() {
        RegUsername_main = findViewById<EditText>(R.id.et_account_main)
        RegPassword_main = findViewById<EditText>(R.id.et_password_main)
        Reglogin_main = findViewById<Button>(R.id.Button01_main)
        enterhomepage = findViewById<Button>(R.id.Buttonenterhomepage)
        loginBtn = findViewById<Button>(R.id.Button03)
    }
//    fun maybeEnableArButton() {
//        val availability = ArCoreApk.getInstance().checkAvailability(this)
//        if (availability.isTransient) {
//            // Continue to query availability at 5Hz while compatibility is checked in the background.
//            Handler(Looper.getMainLooper()).postDelayed({
//                maybeEnableArButton()
//            }, 200)
//        }
//        if (availability.isSupported) {
//            Reglogin_main.visibility = View.VISIBLE
//            Reglogin_main.isEnabled = true
//            val toast = Toast.makeText(applicationContext, "AR available", Toast.LENGTH_SHORT)
//            toast.show()
//        } else { // The device is unsupported or unknown.
//            Reglogin_main.visibility = View.INVISIBLE
//            Reglogin_main.isEnabled = false
//        }
//    }

    fun onClick(view: View) {
        when(view){
            loginBtn ->{
                val username = findViewById<TextView>(R.id.et_account_main).text.toString()
                val password = findViewById<TextView>(R.id.et_password_main).text.toString()

                val chatt = Chatt(username = username,
                    password = password)

                loginstore.postlogin(this@MainActivity,chatt,{ error ->
                    run {
                        lifecycleScope.launch(Dispatchers.Main) {
                            ToastUtil.show(this@MainActivity, error.message ?: "网络异常")
                        }
                    }
                }){
                    run {
                        lifecycleScope.launch(Dispatchers.Main) {
                            startActivity(Intent(this@MainActivity, NavigateActivity::class.java))
                        }
                    }
                }
            }
        }
    }
}