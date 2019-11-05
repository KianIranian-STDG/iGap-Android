package net.iGap.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import net.iGap.R;
import net.iGap.model.GalleryAlbumModel;
import net.iGap.model.GalleryItemModel;

import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static List<GalleryAlbumModel> getDevicePhotoFolders(Context context) {
        List<GalleryAlbumModel> albums = new ArrayList<>();
        if (context == null) return albums;

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        ArrayList<String> ids = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    GalleryAlbumModel album = new GalleryAlbumModel();
                    album.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)));
                    if (!ids.contains(album.getId())) {
                        album.setCaption(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
                        album.setCover(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));
                        if (!album.getCover().contains(".gif")) {
                            //check and add ALL for first item
                            if (albums.size() == 0) {
                                albums.add(new GalleryAlbumModel("-1", context.getString(R.string.all), album.getCover()));
                            }
                            albums.add(album);
                            ids.add(album.getId());
                        }
                    }//else could be counter
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return albums;
    }

    public static List<GalleryItemModel> getFolderPhotosById(Context context , String folderId) {
        List<GalleryItemModel> photos = new ArrayList<>();
        if (context == null) return photos;

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DATE_TAKEN
        };

        boolean isAllPhoto = folderId.equals("-1");

        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                isAllPhoto ? null : MediaStore.Images.Media.BUCKET_ID + " = ?",
                isAllPhoto ? null : new String[]{folderId},
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    GalleryItemModel photo = new GalleryItemModel();
                    photo.setId(photos.size());
                    photo.setAddress(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));
                    if (photo.getAddress() != null && !photo.getAddress().contains(".gif")) {
                        photos.add(photo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return photos;
    }

}
