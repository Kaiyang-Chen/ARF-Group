package cn.edu.sjtu.arf.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.edu.sjtu.arf.R
import androidx.fragment.app.Fragment
import cn.edu.sjtu.arf.kotlin.product.ProductDetailActivity

class HomePage : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.home_page,container,false)
        view.findViewById<ImageView>(R.id.icon).setOnClickListener {view-> goProductDetail("81a5f3aa-fd9e-11ec-9629-4b19c64262c0")  }
        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



    }

    private fun goProductDetail(uid: String) {
        ProductDetailActivity.start(requireContext(),uid)
    }
}