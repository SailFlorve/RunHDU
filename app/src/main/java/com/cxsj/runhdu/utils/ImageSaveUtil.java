package com.cxsj.runhdu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.cxsj.runhdu.constant.URLs;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Sail on 2017/5/21 0021.
 * 保存图片工具类
 */

public class ImageSaveUtil {
    /**
     * 保存图片至本地
     *
     * @param context  上下文
     * @param bitmap
     * @param fileName 文件名
     */
    public static String saveToSDCard(Context context, Bitmap bitmap, String fileName) {
        FileOutputStream fos;
        String saveDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + fileName;
        try {
            fos = new FileOutputStream(saveDir);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();
                return saveDir;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存图片至服务器
     *
     * @param username
     * @param bitmap
     */
    public static void saveToServer(String username, Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] imgArray = bos.toByteArray();
        String imgStr = Base64.encodeToString(imgArray, Base64.DEFAULT);
        HttpUtil.load(URLs.UPLOAD_PROFILE)
                .addParam("UserName", username)
                .addParam("Image", imgStr)
                .post(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                    }
                });
    }
}
