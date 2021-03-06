/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import net.iGap.G;
import net.iGap.R;

public class HelperRadius {


    public static int computeRadius() {
        return (int) G.context.getResources().getDimension(R.dimen.messageBox_cornerRadius);
    }

    public static int computeRadius(String localPath) {
        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(localPath).getAbsolutePath(), options);*/
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

        /*if (options.outWidth > options.outHeight) {
            return (int) (options.outWidth / Config.IMAGE_CORNER);
        } else {
            return (int) (options.outHeight / Config.IMAGE_CORNER);
        }*/
        return (int) G.context.getResources().getDimension(R.dimen.messageBox_cornerRadius);
        //return Config.IMAGE_CORNER;
    }
}
