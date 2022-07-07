package cn.edu.sjtu.arf

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 *
 * @Author:         zhozicho
 * @Date:     2022/7/6 13:37
 * @Desc:
 */
object Constants {

    const val Host = "101.132.97.115"
    const val serverUrl = "https://$Host/"

    lateinit var VolleyQueue: RequestQueue
}