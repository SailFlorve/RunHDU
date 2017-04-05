package com.sailflorve.runhdu.httputils;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Sail on 2017/4/2 0002.
 * OkHttp
 */

public class HttpUtil {

    public static RequestManager load(String url) {
        RequestManager manager = new RequestManager();
        manager.url(url);
        return manager;
    }

    public static class RequestManager {
        private Request.Builder requestBuilder = new Request.Builder();
        private FormBody.Builder formBuilder = new FormBody.Builder();

        private RequestManager url(String url) {
            formBuilder = new FormBody.Builder();
            requestBuilder = new Request.Builder();

            requestBuilder = requestBuilder.url(url);
            return this;
        }

        public RequestManager addHeader(String key, String value) {
            requestBuilder.addHeader(key, value);
            StringBuilder stringBuilder = new StringBuilder();
            return this;
        }

        public RequestManager addParams(String key, String value) {
            formBuilder = formBuilder.add(key, value);
            return this;
        }

        public void post(Callback callback) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(false)
                    .followSslRedirects(false)
                    .build();
            ;
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
