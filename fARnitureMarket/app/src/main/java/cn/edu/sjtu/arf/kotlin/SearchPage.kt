package cn.edu.sjtu.arf.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.product.ProductDetailActivity
import cn.edu.sjtu.arf.kotlin.searchhelper.SearchItemAdapter
import cn.edu.sjtu.arf.kotlin.searchhelper.SearchItemDisplayStore.searchitemdisplays
import cn.edu.sjtu.arf.kotlin.searchhelper.SearchItemUIDStore.getSearchItemUIDs
import kotlinx.android.synthetic.main.page_search.*

class SearchPage : Fragment() {
    private lateinit var searchView : SearchView
    private lateinit var searchItemListView : ListView
    private lateinit var searchItemAdapter : SearchItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout: View = inflater.inflate(R.layout.page_search,container,false)
        searchView = layout.findViewById(R.id.search)
        searchItemListView = layout.findViewById(R.id.searchListView)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView.setIconifiedByDefault(false)

        val context = context as NavigateActivity
        searchItemAdapter = SearchItemAdapter(context, searchitemdisplays)

        searchItemListView.setAdapter(searchItemAdapter)
        searchitemdisplays.addOnListChangedCallback(propertyObserver)
        searchitemdisplays.clear()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchitemdisplays.clear()
                getSearchItemUIDs(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        searchItemListView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    val item = searchItemAdapter.getItem(position)
                    if (item != null) {
                        goProductDetail(item.UID.toString())
                    }
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
                searchItemAdapter.notifyDataSetChanged()
            }
        }
        override fun onItemRangeMoved(sender: ObservableArrayList<Int>?, fromPosition: Int, toPosition: Int,
                                      itemCount: Int) { }
        override fun onItemRangeRemoved(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) { }
    }

    private fun goProductDetail(uid: String) {
        ProductDetailActivity.start(requireContext(),uid)
    }

}