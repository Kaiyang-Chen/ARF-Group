package cn.edu.sjtu.arf.kotlin.searchhelper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.databinding.ItemlistHomeSearchBinding
import coil.load

class SearchItemAdapter(context: Context, searchItem: List<SearchItemDisplay>) :
        ArrayAdapter<SearchItemDisplay>(context, 0, searchItem) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(R.layout.itemlist_home_search, parent, false)
            rowView.tag = ItemlistHomeSearchBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ItemlistHomeSearchBinding

        getItem(position)?.run {
            listItemView.homepageItemName.text = name
            listItemView.homepageItemPrice.text = price
            imageUrl?.let {
                listItemView.homepageItemImage.setVisibility(View.VISIBLE)
                listItemView.homepageItemImage.load(it) {
                    crossfade(true)
                    crossfade(1000)
                }
            } ?: run {
                listItemView.homepageItemImage.setVisibility(View.GONE)
                listItemView.homepageItemImage.setImageBitmap(null)
            }
        }

        return listItemView.root
    }
}