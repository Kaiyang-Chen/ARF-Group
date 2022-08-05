package cn.edu.sjtu.arf.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.mehelper.meorig
import cn.edu.sjtu.arf.kotlin.uploadhelper.UploadPage

class NavigateActivity : AppCompatActivity() {
    lateinit var mNavigation : RadioGroup
    lateinit var mFragments : SparseArray<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigate)
        initView()
        initListener()
    }
    fun toupload(view: View?) = startActivity(Intent(this, UploadPage::class.java))
    fun tome(view: View?) = startActivity(Intent(this, meorig::class.java))
    private fun initView() {
        mNavigation = findViewById(R.id.NavigatorTabs)
        mFragments = SparseArray<Fragment>()
        mFragments.append(R.id.NavigatorHome, HomePage())
        mFragments.append(R.id.NavigatorSearch, SearchPage())
        mFragments.append(R.id.NavigatorCart, CartPage())
        //mFragments.append(R.id.NavigatorMe, MePage())

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragments.get(R.id.NavigatorHome)).commit()
    }

    private fun initListener() {
        mNavigation.setOnCheckedChangeListener { _, checkedId ->
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragments.get(checkedId)).commit()
        }
    }
}