package cn.edu.sjtu.arf.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import cn.edu.sjtu.arf.R

class SearchPage : Fragment() {
    lateinit var searchView : SearchView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout: View = inflater.inflate(R.layout.search_page,container,false)
        searchView = layout.findViewById(R.id.search)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        searchView.setIconifiedByDefault(false)


    }
}