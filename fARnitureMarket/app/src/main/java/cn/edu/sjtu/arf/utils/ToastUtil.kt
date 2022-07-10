package com.chuangsheng.face.utils

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import kotlin.jvm.JvmOverloads
import com.chuangsheng.face.utils.ToastUtil
import java.lang.ref.WeakReference

object ToastUtil {
    private var sRef: WeakReference<Toast?>? = null
    @JvmOverloads
    fun show(context: Context?, msg: CharSequence?, duration: Int = Toast.LENGTH_LONG) {
        if (TextUtils.isEmpty(msg)) return
        if (sRef != null && sRef!!.get() != null) {
            sRef!!.get()!!.cancel()
        }
        val toast = Toast.makeText(context, null, duration)
        sRef = WeakReference(toast)
        toast.setText(msg)
        toast.show()
    }

}