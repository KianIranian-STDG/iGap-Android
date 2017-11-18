/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.AndroidUtils;

public class HelperSaveFile {

    public static Boolean savePicToDownLoadFolder(Bitmap bitmap) {

        try {
            if (bitmap == null) return false;

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

    public enum FolderType {
        download, music, gif, video, image
    }

    public static Boolean saveFileToDownLoadFolder(String filePath, String fileName, FolderType folderType, int sucsesMessageSRC) {

        try {
            if (filePath == null || fileName == null) {
                return false;
            }

            File src = new File(filePath);

            if (!src.exists()) {
                return false;
            }

            Boolean shudCopy = true;

            String destinationPath = " ";

            switch (folderType) {
                case download:

                    if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).exists()) {
                        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;
                    } else {
                        destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/" + fileName;
                    }

                    break;
                case music:
                    if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).exists()) {
                        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + fileName;
                    }

                    break;

                case gif:

                    if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).exists()) {
                        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + fileName;
                    }

                    break;
                case video:

                    if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).exists()) {
                        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/" + fileName;
                    } else {
                        HelperSaveFile.saveVideoToGallary(filePath, true);
                        shudCopy = false;
                    }

                    break;

                case image:

                    savePicToGallary(filePath, true);
                    shudCopy = false;
                    break;
            }

            if (shudCopy) {
                AndroidUtils.copyFile(src, new File(destinationPath));
                Toast.makeText(G.currentActivity, sucsesMessageSRC, Toast.LENGTH_SHORT).show();
            }

            return true;
        } catch (Exception e) {

            Toast.makeText(G.currentActivity, R.string.file_can_not_save_to_selected_folder, Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    public static void savePicToGallary(Bitmap bitmap, String name) {

        MediaStore.Images.Media.insertImage(G.context.getContentResolver(), bitmap, name, "yourDescription");
    }

    public static void savePicToGallary(String filePath, boolean showToast) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        try {

            G.context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (showToast) {
                Toast.makeText(G.context, R.string.picture_save_to_galary, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            String name = filePath.substring(filePath.lastIndexOf("/"));

            savePicToGallary(bitmap, name);

            if (showToast) {
                Toast.makeText(G.context, R.string.picture_save_to_galary, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public static void saveVideoToGallary(String videoFilePath, boolean showToast) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, "My video title");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, videoFilePath);
        G.context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        if (showToast) {
            Toast.makeText(G.context, R.string.file_save_to_galary_folder, Toast.LENGTH_SHORT).show();
        }
    }

    public static void saveToMusicFolder(String path, String name) {
        if (path == null) return;
        File mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File file = new File(mPath, name);
        try {
            // Make sure the Pictures directory exists.
            mPath.mkdirs();

            InputStream is = new FileInputStream(path);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(G.context, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String mPath, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + mPath + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });

            Toast.makeText(G.context, G.context.getResources().getString(R.string.save_to_music_folder), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }
}
