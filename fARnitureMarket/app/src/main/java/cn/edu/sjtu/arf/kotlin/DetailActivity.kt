package cn.edu.sjtu.arf.kotlin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sjtu.arf.R


class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        // get the UID passed from home page
        val intent = intent
        val bundle = intent.extras
        val UID = bundle?.getString("UID")
        // print UID in blank activity page
        val test = findViewById<TextView>(R.id.testTextView)
        test.setText(UID)
    }
}