package cn.edu.sjtu.arf.utils;

import androidx.annotation.VisibleForTesting;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.Request;
import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.HttpResponse;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kotlin.Pair;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class OkHttpStack extends BaseHttpStack {

    private final OkHttpClient mClient;

    public OkHttpStack(OkHttpClient client) {
        this.mClient = client;
    }

    public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
        
        OkHttpClient.Builder builder = mClient.newBuilder();
        OkHttpClient client = builder.build();

        okhttp3.Request.Builder okHttpRequestBuilder = new okhttp3.Request.Builder();
        okHttpRequestBuilder.url(request.getUrl());

        Map<String, String> headers = request.getHeaders();
        for (final String name : headers.keySet()) {
            okHttpRequestBuilder.addHeader(name, headers.get(name));
        }
        for (final String name : additionalHeaders.keySet()) {
            okHttpRequestBuilder.header(name, additionalHeaders.get(name));
        }

        List<Cookie> cookies = mClient.cookieJar().loadForRequest(HttpUrl.get(request.getUrl()));
        StringBuilder sb = new StringBuilder();
        for (final Cookie cookie : cookies) {
            okHttpRequestBuilder.header(cookie.name(), cookie.value());
            sb.append(cookie.name());
            sb.append("=");
            sb.append(cookie.value());
            sb.append(";");
        }
        if(sb.length()>0){
            okHttpRequestBuilder.header("Cookie", sb.delete(sb.length()-1,sb.length()).toString());
        }

        setConnectionParametersForRequest(okHttpRequestBuilder, request);

        okhttp3.Request okHttpRequest = okHttpRequestBuilder.build();
        Call okHttpCall = client.newCall(okHttpRequest);
        Response okHttpResponse = okHttpCall.execute();

        ResponseBody body = okHttpResponse.body();
        return new HttpResponse(
                okHttpResponse.code(),
                convertHeaders(okHttpResponse.headers()),
                body !=null? (int) body.contentLength() :0,
                body !=null? body.byteStream() :null);
    }

    @VisibleForTesting
    static List<Header> convertHeaders(Headers responseHeaders) {
        List<Header> headerList = new ArrayList<>(responseHeaders.size());
        for (String name : responseHeaders.names()) {
            // HttpUrlConnection includes the status line as a header with a null key; omit it here
            // since it's not really a header and the rest of Volley assumes non-null keys.
            if (name != null) {
                for (String value : responseHeaders.values(name)) {
                    headerList.add(new Header(name, value));
                }
            }
        }
        return headerList;
    }

    @SuppressWarnings("deprecation")
    private static void setConnectionParametersForRequest(
            okhttp3.Request.Builder builder, Request<?> request) throws IOException,
            AuthFailureError {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                // Ensure backwards compatibility. Volley assumes a request with
                // a null body is a GET.
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    builder.post(RequestBody.create(
                            MediaType.parse(request.getPostBodyContentType()), postBody));
                }
                break;
            case Request.Method.GET:
                builder.method("GET", createRequestBody(request));
                break;
            case Request.Method.DELETE:
                builder.delete();
                break;
            case Request.Method.POST:
                builder.post(createRequestBody(request));
                break;
            case Request.Method.PUT:
                builder.put(createRequestBody(request));
                break;
            case Request.Method.HEAD:
                builder.head();
                break;
            case Request.Method.OPTIONS:
                builder.method("OPTIONS", null);
                break;
            case Request.Method.TRACE:
                builder.method("TRACE", null);
                break;
            case Request.Method.PATCH:
                builder.patch(createRequestBody(request));
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static RequestBody createRequestBody(Request r) throws AuthFailureError {
        byte[] body = r.getBody();
        if (body == null) {
            if (r.getMethod() == Request.Method.POST) {
                body = "".getBytes();
            } else {
                return null;
            }
        }

        return RequestBody.create(MediaType.parse(r.getBodyContentType()), body);
    }
}
