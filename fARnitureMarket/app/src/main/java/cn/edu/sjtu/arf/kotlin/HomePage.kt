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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemAdapter
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemDisplayStore.homeitemdisplays
import cn.edu.sjtu.arf.kotlin.homepagehelper.HomeItemUIDStore.getHomeItemUIDs
import cn.edu.sjtu.arf.kotlin.product.ProductDetailActivity



import android.widget.ImageView

class HomePage : Fragment() {
    private lateinit var homeItemListView: ListView
    private lateinit var homeItemAdapter: HomeItemAdapter
    private lateinit var refresher: SwipeRefreshLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val layout: View = inflater.inflate(R.layout.page_home,container,false)

        homeItemListView = layout.findViewById(R.id.homeItemListView)

        refresher = layout.findViewById(R.id.homeRefreshContainer)

        return layout

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                if (item != null) {
                    goProductDetail(item.UID.toString())
                }
            }

        refresher.setOnRefreshListener {
            refreshTimeline()
        }
    }

    private val propertyObserver = object: ObservableList.OnListChangedCallback<ObservableArrayList<Int>>() {

        override fun onChanged(sender: ObservableArrayList<Int>?) {
            getActivity()?.runOnUiThread {
                homeItemAdapter.notifyDataSetChanged()
            }
        }
        override fun onItemRangeChanged(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) {
            getActivity()?.runOnUiThread {
                homeItemAdapter.notifyDataSetChanged()
            }
        }

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

                                      itemCount: Int) {
            getActivity()?.runOnUiThread {
                homeItemAdapter.notifyDataSetChanged()
            }
        }
        override fun onItemRangeRemoved(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) {
            getActivity()?.runOnUiThread {
                homeItemAdapter.notifyDataSetChanged()
            }
        }

    }


    private fun refreshTimeline() {
        homeitemdisplays.clear()
        getHomeItemUIDs()
        refresher.isRefreshing = false
    }


    private fun goProductDetail(uid: String) {
        ProductDetailActivity.start(requireContext(),uid)
    }

}