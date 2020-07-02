package com.cxsj.runhdu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.view.View;

import com.cxsj.runhdu.constant.Types;
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
 * 图片处理工具
 */

public class ImageUtil {
    /**
     * 保存图片到相册。
     *
     * @param context  上下文
     * @param bitmap
     * @param fileName 保存的路径
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

    /**
     * 对Activity截图
     *
     * @param activity
     * @return Bitmap对象。
     */
    public static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public static Bitmap takeScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap tBitmap = view.getDrawingCache();
        tBitmap = Bitmap.createBitmap(tBitmap);
        view.setDrawingCacheEnabled(false);
        return tBitmap;
    }

    public static void openPhotoChooser(Activity activity, int resultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        activity.startActivityForResult(intent, resultCode);
    }

    public static void openPhotoCutter(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 192);
        intent.putExtra("outputY", 192);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, Types.TYPE_SAVE_PROFILE);
    }
}
