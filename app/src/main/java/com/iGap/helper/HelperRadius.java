package com.iGap.helper;

import android.graphics.BitmapFactory;
import android.util.Log;
import com.iGap.Config;
import java.io.File;

public class HelperRadius {

    public static int computeRadius(String localPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(localPath).getAbsolutePath(), options);

        Log.e("ddd", options.outWidth + "    options.outWidth   ");

        return (int) (options.outWidth / Config.IMAGE_CORNER);
    }

}
