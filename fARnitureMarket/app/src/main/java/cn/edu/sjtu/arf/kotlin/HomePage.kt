package cn.edu.sjtu.arf.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import cn.edu.sjtu.arf.R
import androidx.fragment.app.Fragment
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemAdapter
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemDisplayStore.homeitemdisplays

class HomePage : Fragment() {
    private lateinit var homeItemListView: ListView
    private lateinit var homeItemAdapter: HomeItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout: View = inflater.inflate(R.layout.page_home,container,false)
        val context = context as NavigateActivity
        homeItemAdapter = HomeItemAdapter(context, homeitemdisplays)
        homeItemListView = layout.findViewById(R.id.homeItemListView)
        homeItemListView.setAdapter(homeItemAdapter)

        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



    }
}