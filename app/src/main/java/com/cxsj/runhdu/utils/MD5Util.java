package com.cxsj.runhdu.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sail on 2017/5/18 0018.
 * MD5加密和解密工具类
 */

public class MD5Util {
    //加密次数
    private final static int ENCODE_TIMES = 3;

    private static String encodeOnce(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encode(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String encodeStr = encodeOnce(str);
        for (int i = 0; i < ENCODE_TIMES - 1; i++) {
            encodeStr = encodeOnce(encodeStr);
        }
        return encodeStr;
    }
}
