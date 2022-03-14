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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.Log;

import net.iGap.G;
import net.iGap.observers.interfaces.OnRotateImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageHelper {

    private static String TAG = "IMAGE_HELPER";

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, (float) pixels, (float) pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * if picture has a wrong rotate correct rotate and save it to file
     *
     * @param filepath picture file address
     * @return return correct rotate bitmap or return null if file path not exist
     */
    public void correctRotateImage(String filepath, boolean compress, OnRotateImage onRotateImage) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                onRotateImage.startProcess();
                if (!isRotateNeed(filepath)) {
                    onRotateImage.success(filepath);

                    return;
                }


                Bitmap bitmap = null;
                boolean saveChange = compress;

                try {

                    if (filepath.length() > 0) {
                        File file = new File(filepath);

                        try {
                            if (compress) {
                                bitmap = decodeFile(file);
                            } else {
                                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                    }

                    ExifInterface ei = new ExifInterface(filepath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            bitmap = rotateImage(bitmap, 90);
                            saveChange = true;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            bitmap = rotateImage(bitmap, 180);
                            saveChange = true;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            bitmap = rotateImage(bitmap, 270);
                            saveChange = true;
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (filepath.length() > 0 && saveChange) SaveBitmapToFile(filepath, bitmap);

                onRotateImage.success(filepath);
            }
        }).start();
    }

    public static Bitmap correctRotateImage(String filepath, boolean compress) {

        Bitmap bitmap = null;
        boolean saveChange = compress;

        try {

            if (filepath != null && filepath.length() > 0) {
                File file = new File(filepath);

                try {
                    if (compress) {
                        ImageHelper imageHelper = new ImageHelper();
                        bitmap = imageHelper.decodeFile(file);
                    } else {
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return null;
            }

            ExifInterface ei = new ExifInterface(filepath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    saveChange = true;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    saveChange = true;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    saveChange = true;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (filepath.length() > 0 && saveChange) SaveBitmapToFile(filepath, bitmap);

        return bitmap;
    }

    public static void SaveBitmapToFile(String filepath, Bitmap bitmap) {

        FileOutputStream out = null;
        try {
            File imgFile = new File(filepath);
            out = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    /**
     * decrease iamge size of file to request size
     *
     * @param f image file
     */
    public Bitmap decodeFile(File f) {
        try {
            //decode image size
            final int IMAGE_MAX_SIZE = 921600; // 400MP

            FileInputStream in = new FileInputStream(f);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();



            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + options.outWidth + ", orig-height: " + options.outHeight);

            Bitmap resultBitmap = null;
            in = new FileInputStream(f);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                // resize to desired dimensions
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();
                Log.d(TAG, "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
                resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(TAG, "bitmap size - width: " +resultBitmap.getWidth() + ", height: " +
                    resultBitmap.getHeight());
            return resultBitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNeedToCompress(File file) {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);
            return o.outWidth > 500 || o.outHeight > 500;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static Bitmap correctRotate(String filepath, Bitmap bitmap) {

        try {
            ExifInterface ei = new ExifInterface(filepath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            return bitmap;
        }

        return bitmap;
    }

    public static boolean isRotateNeed(String filepath) {
        try {
            ExifInterface exif = new ExifInterface(filepath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                case ExifInterface.ORIENTATION_ROTATE_180:
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
