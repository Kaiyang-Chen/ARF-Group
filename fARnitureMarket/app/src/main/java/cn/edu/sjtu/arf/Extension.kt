package cn.edu.sjtu.arf

import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 *
 * @Author:         zhozicho
 * @Date:     2022/7/6 15:41
 * @Desc:
 */
fun ImageView.networkUrl(url: String) {
    Glide.with(this)
        .load(url)
        .into(this);
}