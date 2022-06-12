package cn.edu.sjtu.chenfred02.kotlinChatter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Register : AppCompatActivity() {
    lateinit var RegUsername : EditText
    lateinit var RegPassword : EditText
    lateinit var RegConfirmPassword : EditText
    lateinit var RegRegister : Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        initViews()
        initListener()
    }
    private fun initViews() {
        RegUsername = findViewById<EditText>(R.id.et_account)
        RegPassword = findViewById<EditText>(R.id.et_password)
        RegConfirmPassword = findViewById<EditText>(R.id.et_repassword)
        RegRegister = findViewById<Button>(R.id.Button03)
    }

    private fun initListener() {
        RegRegister.setOnClickListener{
            var username = RegUsername.text.toString()
            var password = RegPassword.text.toString()
            var confirmpassword = RegConfirmPassword.text.toString()
            if (username == "") {
                Toast.makeText(this, "Username cannot be empty!" , Toast.LENGTH_SHORT).show()
            }
            else if (password == "") {
                Toast.makeText(this, "Password cannot be empty!" , Toast.LENGTH_SHORT).show()
            }
            else if (confirmpassword == "") {
                Toast.makeText(this, "Confirm Password cannot be empty!" , Toast.LENGTH_SHORT).show()
            }
            else if (password != confirmpassword) {
                Toast.makeText(this, "Different Passwords!" , Toast.LENGTH_SHORT).show()
            }
            else{
                var validname = RegUsername.text.toString()
                var validpassword = RegPassword.text.toString()
                Toast.makeText(this, "Register Successfully!" , Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}