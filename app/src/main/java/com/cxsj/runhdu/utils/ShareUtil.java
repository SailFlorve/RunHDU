package com.cxsj.runhdu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

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

    public static void shareImg(Activity activity, String imagePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        File file = new File(imagePath);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType("image/jpeg");
        Intent chooser = Intent.createChooser(intent, "分享运动数据");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(chooser);
        }
    }

    public static void shareText(Context context, String content) {
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(context, "文字为空。", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("text/plain");
        Intent chooser = Intent.createChooser(intent, content);
        context.startActivity(chooser);
    }
}
