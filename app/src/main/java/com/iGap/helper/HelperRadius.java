package com.iGap.helper;

import android.graphics.BitmapFactory;

import com.iGap.Config;

import java.io.File;

public class HelperRadius {

    public static int computeRadius(String localPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(localPath).getAbsolutePath(), options);
        //return (int) (options.outWidth / Config.IMAGE_CORNER);

        /*DisplayMetrics metrics = new DisplayMetrics();
        G.currentActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;*/

        /*if (options.outWidth > options.outHeight) {
            return (int) (options.outWidth / Config.IMAGE_CORNER);
        } else {
            return (int) (options.outHeight / Config.IMAGE_CORNER);
        }*/

        if (options.outWidth > options.outHeight) {
            return (int) (options.outWidth / Config.IMAGE_CORNER);
        } else {
            return (int) (options.outHeight / Config.IMAGE_CORNER);
        }
    }

}
