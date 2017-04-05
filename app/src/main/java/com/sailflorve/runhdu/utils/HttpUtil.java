package com.sailflorve.runhdu.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Sail on 2017/4/2 0002.
 * OkHttp
 */

public class HttpUtil {

    public static class HttpRequest {
        private static HttpRequest httpRequest = new HttpRequest();
        private static Request.Builder requestBuilder = new Request.Builder();
        private FormBody.Builder formBuilder = new FormBody.Builder();

        public static HttpRequest url(String url) {
            httpRequest.formBuilder = new FormBody.Builder();
            requestBuilder = requestBuilder.url(url);
            return httpRequest;
        }

        public HttpRequest header(String key, String value) {
            requestBuilder.addHeader(key, value);
            return httpRequest;
        }

        public HttpRequest add(String key, String value) {
            httpRequest.formBuilder = httpRequest.formBuilder.add(key, value);
            return httpRequest;
        }

        public void post(Callback callback) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(false)
                    .followSslRedirects(false)
                    .build();;
            Request request = requestBuilder.post(formBuilder.build()).build();
            client.newCall(request).enqueue(callback);
        }

        public void get(Callback callback) {
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = requestBuilder.build();
            client.newCall(request).enqueue(callback);
        }
    }
}
