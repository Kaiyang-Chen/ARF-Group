package cn.edu.sjtu.arf.kotlin.mehelper

import GsonRequest
import androidx.lifecycle.LifecycleCoroutineScope
import cn.edu.sjtu.arf.App
import cn.edu.sjtu.arf.Constants
import cn.edu.sjtu.arf.kotlin.product.Product
import cn.edu.sjtu.arf.kotlin.uploadhelper.prodstore.uid
import cn.edu.sjtu.arf.utils.FakeX509TrustManager
import com.android.volley.Request
import com.android.volley.Response
import com.google.ar.core.dependencies.e
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object meshit {
    private const val serverUrl = Constants.serverUrl
    fun getProductDetail(
        scope: LifecycleCoroutineScope,
        errorListener: Response.ErrorListener?,
        listener: Response.Listener<Meinfo>
    ) {

        FakeX509TrustManager.allowAllSSL()
        var strs = App.loginHeader?.get("Cookie")?.split("; ")
        strs = strs?.get(0)?.split("=")
        val csrftoken = strs?.get(strs.size - 1);
        val params = mapOf(
            "username" to "",
            "gender" to "",
            "phone" to "",
            "address" to "",
            "email" to ""
        )
        csrftoken?.let { App.loginHeader?.put("X-CSRFToken", it)}
        val postRequest = GsonRequest(
            method = Request.Method.POST,
            url = serverUrl + "check/",
            requestBody = params,
            headers = App.loginHeader,
            clazz = Meinfo::class.java,
            listener = { res ->
                scope.launch(Dispatchers.Main) {
                    listener.onResponse(res)
                }
            },
            errorListener = { err -> scope.launch(Dispatchers.Main) { errorListener?.onErrorResponse(err) } }
        )

        Constants.VolleyQueue.add(postRequest)
    }
}