package cn.edu.sjtu.arf.kotlin

import android.R.attr.button
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemAdapter
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemDisplayStore.cartitemdisplays
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemUIDStore.getCartItemUIDs
import cn.edu.sjtu.arf.kotlin.product.ProductDetailActivity


class CartPage : Fragment() {
    private lateinit var cartItemListView: ListView
    private lateinit var cartItemAdapter: CartItemAdapter
    private lateinit var refresher: SwipeRefreshLayout
    lateinit var checkoutButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout: View = inflater.inflate(R.layout.page_cart,container,false)

        cartItemListView = layout.findViewById(R.id.cartListView)
        refresher = layout.findViewById(R.id.cartRefreshContainer)
        checkoutButton = layout.findViewById(R.id.checkoutbox)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkoutButton.text = "No Furniture To Check Out"

        val context = context as NavigateActivity
        cartItemAdapter = CartItemAdapter(context, cartitemdisplays)

        cartItemListView.setAdapter(cartItemAdapter)
        cartitemdisplays.addOnListChangedCallback(propertyObserver)

        cartitemdisplays.clear()
        getCartItemUIDs()

        cartItemListView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                // Intent jump to DetailActivity and pass UID to DetailActivity
                val item = cartItemAdapter.getItem(position)
                if (item != null) {
                    goProductDetail(item.UID.toString())
                }
            }

        refresher.setOnRefreshListener {
            refreshTimeline()
        }

        checkoutButton.setOnClickListener(View.OnClickListener {
            // Code here executes on main thread after user presses button
            if (checkoutButton.text.equals("Check Out")) {

            } else {
                Toast.makeText(activity, "No Furniture To Check Out", Toast.LENGTH_SHORT).show()
            }
        })
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
                cartItemAdapter.notifyDataSetChanged()
            }
            checkoutButton.text = "Check Out"
        }
        override fun onItemRangeMoved(sender: ObservableArrayList<Int>?, fromPosition: Int, toPosition: Int,
                                      itemCount: Int) { }
        override fun onItemRangeRemoved(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) { }
    }

    private fun goProductDetail(uid: String) {
        ProductDetailActivity.start(requireContext(),uid)
    }

    private fun refreshTimeline() {
        checkoutButton.text = "No Furniture To Check Out"
        cartitemdisplays.clear()
        getCartItemUIDs()
        refresher.isRefreshing = false
    }

}