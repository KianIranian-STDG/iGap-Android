package net.iGap.helper;

import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HelperScreenShot {

    public static Bitmap takeScreenshot(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap takeScreenshotOfRootView(View v) {
        return takeScreenshot(v.getRootView());
    }

    public static boolean takeScreenshotAndSaveIi(View v, String filename) {
        return storeScreenshot(takeScreenshot(v.getRootView()), filename);
    }

    public static boolean storeScreenshot(Bitmap bitmap, String filename) {

        if (!isExternalStorageReadable()) {
            return false;
        }
        if (!isExternalStorageWritable()) {
            return false;
        }

        OutputStream out = null;
        File dir = getDownloadStorageDir("Payment_Receipt");
        File imageFile = new File(dir, filename);

        try {
            out = new FileOutputStream(imageFile);
            // choose JPEG format
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
        } catch (FileNotFoundException e) {
            // manage exception ...
            return false;
        } catch (IOException e) {
            // manage exception ...
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                return true;
            } catch (Exception exc) {
                return false;
            }
        }
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static File getDownloadStorageDir(String fileName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), fileName);
        if (file.isDirectory())
            return file;

        if (!file.mkdirs()) {
            return null;
        }
        return file;
    }
}
