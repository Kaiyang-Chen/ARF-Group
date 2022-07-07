import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


/**
 * Make a GET request and return a parsed object from JSON.
 *
 * @param url URL of the request to make
 * @param clazz Relevant class object, for Gson's reflection
 * @param headers Map of request headers
 */
class GsonRequest<T>(
    method: Int,
    url: String,
    private val requestBody: Map<String, String?>? = null,
    private val clazz: Class<T>,
    private val headers: MutableMap<String, String>? = null,
    private val listener: Response.Listener<T>,
    errorListener: Response.ErrorListener
) : Request<T>(method, url, errorListener) {
    private val gson = Gson()

    override fun getHeaders(): MutableMap<String, String> {
        return  headers ?: super.getHeaders()
    }

    override fun deliverResponse(response: T) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
        return try {
            val json = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
            Response.success(
                gson.fromJson(json,clazz),
                HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    override fun getParams(): MutableMap<String, String>? {
        return requestBody as MutableMap<String, String>
    }

    override fun getBody(): ByteArray? {
        return try {
            requestBody?.let {
                gson.toJson(it).toByteArray(Charsets.UTF_8)
            }
        } catch (uee: UnsupportedEncodingException) {
            VolleyLog.wtf(
                "Unsupported Encoding while trying to get the bytes of %s using %s",
                requestBody, Charsets.UTF_8.toString()
            )
            null
        }
    }
}

