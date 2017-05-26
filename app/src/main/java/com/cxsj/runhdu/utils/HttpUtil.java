package com.cxsj.runhdu.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Sail on 2017/4/2 0002.
 * OkHttp
 */

public class HttpUtil {
    private static RequestManager manager;

    public static RequestManager load(String url) {
        manager = new RequestManager();
        manager.url(url);
        return manager;
    }

    public static class RequestManager {
        private Request.Builder requestBuilder = new Request.Builder();
        private FormBody.Builder formBuilder = new FormBody.Builder();
        private Call call;

        private void url(String url) {
            formBuilder = new FormBody.Builder();
            requestBuilder = new Request.Builder();

            requestBuilder = requestBuilder.url(url);
        }

        private void cancel() {
            if (call != null) {
                call.cancel();
            }
        }

        public RequestManager addHeader(String key, String value) {
            requestBuilder.addHeader(key, value);
            return this;
        }

        public RequestManager addParam(String key, String value) {
            formBuilder = formBuilder.add(key, value);
            return this;
        }

        public void post(Callback callback) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .followRedirects(false)
                    .followSslRedirects(false)
                    .build();

            Request request = requestBuilder.post(formBuilder.build()).build();
            client.newCall(request).enqueue(callback);
        }

        public void get(Callback callback) {
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = requestBuilder.build();
            call = client.newCall(request);
            call.enqueue(callback);
        }
    }
}
