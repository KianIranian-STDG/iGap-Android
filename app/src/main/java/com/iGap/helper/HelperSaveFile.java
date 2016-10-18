package com.iGap.helper;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by android3 on 9/27/2016.
 */
public class HelperSaveFile {


    public static Boolean savePicToDownLoadFolder(Bitmap bitmap) {

        try {
            if (bitmap == null)
                return false;

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();
            Timestamp tsTemp = new Timestamp(now.getTime());
            String ts = tsTemp.toString();

            File file = new File(path, ts + ".jpg");

            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            return true;
        } catch (FileNotFoundException e) {
            return false;
        }

    }


    public static void savePicToGallary(Bitmap bitmap, String name) {

        MediaStore.Images.Media.insertImage(G.context.getContentResolver(), bitmap, name, "yourDescription");

    }


    public static void savePicToGallary(String filePath) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        G.context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    public static void saveToMusicFolder(String path, String name) {
        try {
            if (path == null)
                return;

            InputStream is = new FileInputStream(path);
            File mSavePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            File file = new File(mSavePath + "/" + name);
            OutputStream os = new FileOutputStream(file);

            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) > 0) {
                os.write(buff, 0, len);
            }
            is.close();
            os.close();

            Toast.makeText(G.context, R.string.save_to_music_folder, Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }


    }


}
