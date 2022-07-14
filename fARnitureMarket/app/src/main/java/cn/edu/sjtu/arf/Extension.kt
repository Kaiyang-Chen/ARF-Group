package cn.edu.sjtu.arf

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.networkUrl(url: String) {
    Glide.with(this)
        .load(url)
        .into(this);
}