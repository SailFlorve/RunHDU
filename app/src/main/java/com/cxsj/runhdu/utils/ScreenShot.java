package com.cxsj.runhdu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.cxsj.runhdu.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Sail on 2017/5/4 0004.
 * 截图并分享。
 */

public class ScreenShot {
    // 获取指定Activity的截屏，保存到png文件
    private static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    private static Bitmap takeScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap tBitmap = view.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = Bitmap.createBitmap(tBitmap);
        view.setDrawingCacheEnabled(false);
        if (tBitmap != null) {
            return tBitmap;
        } else {
            return null;
        }
    }


    // 保存到sdcard
    private static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 程序入口
    private static String shoot(Activity a) {
        String strFileName = a.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + String.valueOf(System.currentTimeMillis()) + ".png";
        ScreenShot.savePic(ScreenShot.takeScreenShot(a), strFileName);
        return strFileName;
    }

    public static void takeAndShare(Activity activity) {
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            activity.runOnUiThread(() -> {
                String imagePath = shoot(activity);
                Intent intent = new Intent(Intent.ACTION_SEND);
                File file = new File(imagePath);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/jpeg");
                Intent chooser = Intent.createChooser(intent, "分享运动数据");
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(chooser);
                }
            });
        }).start();
    }
}
