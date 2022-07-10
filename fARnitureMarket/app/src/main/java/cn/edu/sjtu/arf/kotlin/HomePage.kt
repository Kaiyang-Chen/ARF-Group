package cn.edu.sjtu.arf.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemAdapter
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemDisplayStore.homeitemdisplays
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemUIDStore.getHomeItemUIDs


class HomePage : Fragment() {
    private lateinit var homeItemListView: ListView
    private lateinit var homeItemAdapter: HomeItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout: View = inflater.inflate(R.layout.page_home,container,false)

        homeItemListView = layout.findViewById(R.id.homeItemListView)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //var idx = 0

        val context = context as NavigateActivity
        homeItemAdapter = HomeItemAdapter(context, homeitemdisplays)

        homeItemListView.setAdapter(homeItemAdapter)
        homeitemdisplays.addOnListChangedCallback(propertyObserver)

        homeitemdisplays.clear()
        getHomeItemUIDs()

        homeItemListView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                // Intent jump to DetailActivity and pass UID to DetailActivity
                val item = homeItemAdapter.getItem(position)
                val intent = Intent(activity, DetailActivity::class.java)
                val bundle = Bundle()
                if (item != null) {
                    bundle.putString("UID", item.UID.toString())
                }
                intent.putExtras(bundle)
                startActivity(intent)
            }
    }

    private val propertyObserver = object: ObservableList.OnListChangedCallback<ObservableArrayList<Int>>() {
        override fun onChanged(sender: ObservableArrayList<Int>?) { }
        override fun onItemRangeChanged(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) { }
        override fun onItemRangeInserted(
            sender: ObservableArrayList<Int>?,
            positionStart: Int,
            itemCount: Int
        ) {
            println("onItemRangeInserted: $positionStart, $itemCount")
            getActivity()?.runOnUiThread {
                homeItemAdapter.notifyDataSetChanged()
            }
        }
        override fun onItemRangeMoved(sender: ObservableArrayList<Int>?, fromPosition: Int, toPosition: Int,
                                      itemCount: Int) { }
        override fun onItemRangeRemoved(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) { }
    }


}