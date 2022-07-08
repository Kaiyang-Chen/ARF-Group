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

/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Circle Internet Financial
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * OkHttp backed {@link com.android.volley.toolbox.HttpStack HttpStack} that
 * does not use okhttp-urlconnection
 */
public class OkHttpStack extends BaseHttpStack {

    private final OkHttpClient mClient;

    public OkHttpStack(OkHttpClient client) {
        this.mClient = client;
    }

    public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {

        // okhttp 3.0以后的版本构建OkHttpClient使用Builder
        OkHttpClient.Builder builder = mClient.newBuilder();
        OkHttpClient client = builder.build();

        okhttp3.Request.Builder okHttpRequestBuilder = new okhttp3.Request.Builder();
        okHttpRequestBuilder.url(request.getUrl());

        Map<String, String> headers = request.getHeaders();
        for (final String name : headers.keySet()) {
            okHttpRequestBuilder.addHeader(name, headers.get(name));
        }
        for (final String name : additionalHeaders.keySet()) {
            // 这里用header方法，如果有重复的name，会覆盖，否则某些请求会被判定为非法
            okHttpRequestBuilder.header(name, additionalHeaders.get(name));
        }

        List<Cookie> cookies = mClient.cookieJar().loadForRequest(HttpUrl.get(request.getUrl()));
        StringBuilder sb = new StringBuilder();
        for (final Cookie cookie : cookies) {
            // 这里用header方法，如果有重复的name，会覆盖，否则某些请求会被判定为非法
            okHttpRequestBuilder.header(cookie.name(), cookie.value());
            sb.append(cookie.name());
            sb.append("=");
            sb.append(cookie.value());
            sb.append(";");
        }
        okHttpRequestBuilder.header("Cookie", sb.delete(sb.length()-1,sb.length()).toString());
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
            // OkHttp内部默认的的判断逻辑是POST 不能为空，这里做了规避
            if (r.getMethod() == Request.Method.POST) {
                body = "".getBytes();
            } else {
                return null;
            }
        }

        return RequestBody.create(MediaType.parse(r.getBodyContentType()), body);
    }
}