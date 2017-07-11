package com.cxsj.runhdu.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.view.View;

import java.io.File;

/**
 * Created by Sail on 2017/5/4 0004.
 * 用于截图并分享工具类
 */

public class ShareUtil {

    private static int delayTime = 0;

    public static void setDelay(int time) {
        delayTime = time;
    }

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
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = Bitmap.createBitmap(tBitmap);
        view.setDrawingCacheEnabled(false);
        if (tBitmap != null) {
            return tBitmap;
        } else {
            return null;
        }
    }

    public static void openShareDialog(Activity activity, String imagePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        File file = new File(imagePath);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType("image/jpeg");
        Intent chooser = Intent.createChooser(intent, "分享运动数据");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(chooser);
        }
    }

    public static void takeAndShare(Activity activity) {
        new Thread(() -> {
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            activity.runOnUiThread(() -> {
                String imagePath = ImageSaveUtil.saveToSDCard(activity,
                        ShareUtil.takeScreenShot(activity), "share_tmp");
                openShareDialog(activity, imagePath);
            });
        }).start();
    }
}
