package cn.edu.sjtu.arf.kotlin.carthelper

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import cn.edu.sjtu.arf.App
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.databinding.ItemlistCartBinding
import coil.load
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class CartItemAdapter(context: Context, homeItem: List<CartItemDisplay>) :
        ArrayAdapter<CartItemDisplay>(context, 0, homeItem) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(R.layout.itemlist_cart, parent, false)
            rowView.tag = ItemlistCartBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ItemlistCartBinding

        getItem(position)?.run {
            listItemView.cartName.text = name
            listItemView.cartPrice.text = price
            imageUrl?.let {
                listItemView.cartImg.setVisibility(View.VISIBLE)
                listItemView.cartImg.load(it) {
                    crossfade(true)
                    crossfade(1000)
                }
            } ?: run {
                listItemView.cartImg.setVisibility(View.GONE)
                listItemView.cartImg.setImageBitmap(null)
            }
        }

        listItemView.cartRemove.setOnClickListener(View.OnClickListener {
            val jsonObj = JSONObject(mapOf("UID" to getItem(position)?.UID))

            val client = OkHttpClient()

            val request = Request.Builder()
                    .addHeader("Cookie", App.loginHeader?.get("Cookie")?:"")
                    .url("https://101.132.97.115/" +"delete_from_cart/")
                    .post(RequestBody.create("application/json".toMediaType(), jsonObj.toString()))
                    .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("DeleteCartItemUIDs", "Failed")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.e("DeleteCartItemUIDs", "GET response")
                    if (response.isSuccessful) {
                        CartItemDisplayStore.cartitemdisplays.clear()
                        CartItemUIDStore.getCartItemUIDs()
                    }
                }
            })

            Toast.makeText(listItemView.root.context, "Delete Successfully!", Toast.LENGTH_SHORT).show()

        })

        return listItemView.root
    }
}