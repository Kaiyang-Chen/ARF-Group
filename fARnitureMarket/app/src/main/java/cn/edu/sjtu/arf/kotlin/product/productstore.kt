package cn.edu.sjtu.arf.kotlin.product

import GsonRequest
import androidx.lifecycle.LifecycleCoroutineScope
import cn.edu.sjtu.arf.App
import cn.edu.sjtu.arf.Constants
import cn.edu.sjtu.arf.utils.FakeX509TrustManager
import com.android.volley.Request
import com.android.volley.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object productstore {
    private const val serverUrl = Constants.serverUrl
    fun getProductDetail(
        uid: String,
        scope: LifecycleCoroutineScope,
        errorListener: Response.ErrorListener?,
        listener: Response.Listener<Product>
    ) {

        FakeX509TrustManager.allowAllSSL()
        var strs = App.loginHeader?.get("Cookie")?.split("; ")
        strs = strs?.get(0)?.split("=")
        val csrftoken = strs?.get(strs.size - 1);
        val params = mapOf(
            "UID" to uid
        )

        App.loginHeader?.put("csrftoken",csrftoken?:"")
        App.loginHeader?.put("csrftoken",csrftoken?:"")

        val postRequest = GsonRequest(
            method = Request.Method.GET,
            url = serverUrl + "fetch_product_detailed/",
            requestBody = params,
            headers = App.loginHeader,
            clazz = Product::class.java,
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