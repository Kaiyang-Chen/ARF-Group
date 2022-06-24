package cn.edu.sjtu.farnituremarket

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment


class SearchPage : Fragment() {
    lateinit var searchView : SearchView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout: View = inflater.inflate(cn.edu.sjtu.farnituremarket.R.layout.searchpage,container,false)
        searchView = layout.findViewById(cn.edu.sjtu.farnituremarket.R.id.search)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        searchView.setIconifiedByDefault(false)


    }
}