package cn.edu.sjtu.chenfred02.kotlinChatter

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import cn.edu.sjtu.chenfred02.kotlinChatter.ChattStore.chatts
import cn.edu.sjtu.chenfred02.kotlinChatter.ChattStore.getChatts
import cn.edu.sjtu.chenfred02.kotlinChatter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var RegUsername_main : EditText
    lateinit var RegPassword_main : EditText
    lateinit var Reglogin_main : Button
    private var password_currect = true
    private lateinit var view: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }
    fun SA(view: View?) = startActivity(Intent(this, Register::class.java))

    fun SB(view: View?){
        startActivity(Intent(this, PostActivity::class.java))
        Toast.makeText(this, "Log In Successfully" , Toast.LENGTH_SHORT).show()
    }


    private fun initViews() {
        RegUsername_main = findViewById<EditText>(R.id.et_account_main)
        RegPassword_main = findViewById<EditText>(R.id.et_password_main)
        Reglogin_main = findViewById<Button>(R.id.Button01_main)
    }
}