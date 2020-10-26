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

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.yalantis.ucrop.util.FileUtils;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.AndroidUtils;
import net.iGap.observers.interfaces.OnGetPermission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HelperSaveFile {

    public static void saveFileToDownLoadFolder(final String filePath, final String fileName, final FolderType folderType, final int successMessage) {

        if (!HelperPermission.grantedUseStorage()) {
            try {
                HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        saveFileToDownLoadFolder(filePath, fileName, folderType, successMessage);
                    }

                    @Override
                    public void deny() {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (filePath == null || fileName == null) {
                    return;
                }

                File src = new File(filePath);

                if (!src.exists()) {
                    return;
                }

                Boolean shouldCopy = true;

                String destinationPath = " ";

                switch (folderType) {
                    case download:
                        if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).exists()) {
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
                        }
                        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;
                        break;
                    case music:

                        if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).exists()) {
                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath()).mkdirs();
                        }
                        if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).exists()) {
                            destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + fileName;
                        }

                        break;

                    case gif:

                        if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).exists()) {
                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()).mkdirs();
                        }
                        if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).exists()) {
                            destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + fileName;
                        }

                        break;
                    case video:

                        if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).exists()) {
                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath()).mkdirs();
                        }

                        if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).exists()) {
                            destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/" + fileName;
                        } else {
                            saveVideoToGallery(filePath);
                            shouldCopy = false;
                        }

                        break;

                    case image:

                        File file = getAlbumDir();
                        if (file != null) {
                            destinationPath = getAlbumDir().getAbsolutePath() + "/" + fileName;
                        } else {
                            if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).exists()) {
                                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()).mkdirs();
                            }
                            if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).exists()) {
                                destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + fileName;
                            }
                        }

                        //savePicToGallery(filePath, false);
                        shouldCopy = true;
                        break;
                }

                if (shouldCopy) {
                    File file = new File(destinationPath);
                    AndroidUtils.copyFile(src, file);
                    MediaScannerConnection.scanFile(G.context, new String[]{file.getAbsolutePath()}, null, null);
                    Toast.makeText(G.currentActivity, successMessage, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(G.currentActivity, R.string.file_can_not_save_to_selected_folder, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void savePicToGallery(final String filePath, final boolean showToast) {
        try {
            String extension = filePath.substring(filePath.lastIndexOf("."));
            saveFileToDownLoadFolder(filePath, "IMAGE_" + System.currentTimeMillis() + extension, FolderType.image, R.string.file_save_to_picture_folder);
        } catch (Exception e) {
            saveFileToDownLoadFolder(filePath, "IMAGE_" + System.currentTimeMillis() + ".png", FolderType.image, R.string.file_save_to_picture_folder);
            FileLog.e(e);
        }
    }

    private static File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "iGap");
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    return null;
                }
            }
        }
        return storageDir;
    }

    private static void saveVideoToGallery(String videoFilePath) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, "My video title");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, videoFilePath);
        G.context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        Toast.makeText(G.context, R.string.file_save_to_galary_folder, Toast.LENGTH_SHORT).show();
    }

//    public static void savePicToGallery(final String filePath, final boolean showToast) {
//
//        if (!HelperPermision.grantedUseStorage()) {
//            try {
//                HelperPermision.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
//                    @Override
//                    public void Allow() throws IOException {
//                        savePicToGallery(filePath, showToast);
//                    }
//
//                    @Override
//                    public void deny() {
//
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//            values.put(MediaStore.MediaColumns.DATA, filePath);
//
//            try {
//                G.context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                if (showToast) {
//                    Toast.makeText(G.context, R.string.picture_save_to_galary, Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//
//                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//                String name = filePath.substring(filePath.lastIndexOf("/"));
//
//                MediaStore.Images.Media.insertImage(G.context.getContentResolver(), bitmap, name, "yourDescription");
//
//                if (showToast) {
//                    Toast.makeText(G.context, R.string.picture_save_to_galary, Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

    public static void saveToMusicFolder(final String path, final String name) {

        if (!HelperPermission.grantedUseStorage()) {
            try {
                HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        saveToMusicFolder(path, name);
                    }

                    @Override
                    public void deny() {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
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
                    }
                });

                Toast.makeText(G.context, G.context.getResources().getString(R.string.save_to_music_folder), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // Unable to create file, likely because external storage is
                // not currently mounted.
                e.printStackTrace();
            }
        }
    }

    public static String saveInPrivateDirectory(File fileWithinMyDir, String filePath) throws IOException {
        FileUtils.copyFile(filePath, fileWithinMyDir.getPath());
        return fileWithinMyDir.getPath();
    }

    public static void removeFromPrivateDirectory(File fileWithinMyDir) {
        if (fileWithinMyDir.exists()) {
            fileWithinMyDir.delete();
        }
    }

    public static File getPrivateDirectory(Activity activity) {
        return new File(new ContextWrapper(activity).getDir("ChatBackground", Context.MODE_PRIVATE), "kb24");
    }

    public enum FolderType {
        download, music, gif, video, image
    }

//    public static Boolean savePicToDownLoadFolder(Bitmap bitmap) {
//        try {
//            if (bitmap == null) return false;
//
//            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//
//            Calendar calendar = Calendar.getInstance();
//            java.util.Date now = calendar.getTime();
//            Timestamp tsTemp = new Timestamp(now.getTime());
//            String ts = tsTemp.toString();
//
//            File file = new File(path, ts + ".jpg");
//
//            OutputStream fOut = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//
//            return true;
//        } catch (FileNotFoundException e) {
//            return false;
//        }
//    }
}
