package net.iGap.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import net.iGap.R;
import net.iGap.model.GalleryAlbumModel;
import net.iGap.model.GalleryItemModel;
import net.iGap.model.GalleryMusicModel;
import net.iGap.model.GalleryVideoModel;
import net.iGap.module.structs.StructFileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileManager {

    public static void getDevicePhotoFolders(Context context, FetchListener<List<GalleryAlbumModel>> callback) {

        new Thread(() -> {

            List<GalleryAlbumModel> albums = new ArrayList<>();
            if (context == null) {
                callback.onFetch(albums);
                return;
            }

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
                    MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC");

            ArrayList<String> ids = new ArrayList<>();
            if (cursor != null) {

                final int COLUMN_ID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                final int COLUMN_BUCKET_NAME = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                final int COLUMN_DATA = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);

                while (cursor.moveToNext()) {
                    try {
                        GalleryAlbumModel album = new GalleryAlbumModel();
                        album.setId(cursor.getString(COLUMN_ID));
                        if (!ids.contains(album.getId())) {
                            album.setCaption(cursor.getString(COLUMN_BUCKET_NAME));
                            album.setCover(cursor.getString(COLUMN_DATA));
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
            callback.onFetch(albums);

        }).start();

    }

    public static void getFolderPhotosById(Context context, String folderId, FetchListener<List<GalleryItemModel>> callback) {

        new Thread(() -> {

            List<GalleryItemModel> photos = new ArrayList<>();
            if (context == null) {
                callback.onFetch(photos);
                return;
            }

            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.MediaColumns.DATA
            };

            boolean isAllPhoto = folderId.equals("-1");

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    isAllPhoto ? null : MediaStore.Images.Media.BUCKET_ID + " = ?",
                    isAllPhoto ? null : new String[]{folderId},
                    MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC"
            );

            if (cursor != null) {

                final int COLUMN_DATA = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);

                while (cursor.moveToNext()) {
                    try {
                        GalleryItemModel photo = new GalleryItemModel();
                        photo.setId(photos.size());
                        photo.setAddress(cursor.getString(COLUMN_DATA));
                        if (photo.getAddress() != null && !photo.getAddress().contains(".gif")) {
                            photos.add(photo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            callback.onFetch(photos);

        }).start();
    }

    public static void getAllMedia(Context context, String folderId, FetchListener<List<GalleryItemModel>> callback) {

        new Thread(() -> {

            List<GalleryItemModel> videos = new ArrayList<>();
            if (context == null) {
                callback.onFetch(videos);
                return;
            }

            Uri uri = MediaStore.Files.getContentUri("external");
            ;
            String[] projection = {
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MIME_TYPE,
                    MediaStore.Files.FileColumns.TITLE
            };

// Return only video and image metadata.
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            boolean isAllPhoto = folderId.equals("-1");

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    selection,
                   null,
                    MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"
            );

            if (cursor != null) {

                final int COLUMN_DATA = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                final int COLUMN_MEDIA_TYPE = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
                while (cursor.moveToNext()) {
                    try {
                        GalleryItemModel video = new GalleryItemModel();
                        video.setId(videos.size());
                        video.setAddress(cursor.getString(COLUMN_DATA));
                        video.setMediaType(cursor.getString(COLUMN_MEDIA_TYPE));
                        videos.add(video);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            callback.onFetch(videos);

        }).start();

    }

    public static void getFolderVideosById(Context context, String folderId, FetchListener<List<GalleryVideoModel>> callback) {

        new Thread(() -> {

            List<GalleryVideoModel> videos = new ArrayList<>();
            if (context == null) {
                callback.onFetch(videos);
                return;
            }

            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Video.Media.DATA
            };

            boolean isAllPhoto = folderId.equals("-1");

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    isAllPhoto ? null : MediaStore.Video.Media.BUCKET_ID + " = ?",
                    isAllPhoto ? null : new String[]{folderId},
                    MediaStore.Video.Media.DATE_MODIFIED + " DESC"
            );

            if (cursor != null) {

                final int COLUMN_DATA = cursor.getColumnIndex(MediaStore.Video.Media.DATA);

                while (cursor.moveToNext()) {
                    try {
                        GalleryVideoModel video = new GalleryVideoModel();
                        video.setId(videos.size() + "");
                        video.setPath(cursor.getString(COLUMN_DATA));
                        videos.add(video);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            callback.onFetch(videos);

        }).start();

    }

    public static void getDeviceVideoFolders(Context context, FetchListener<List<GalleryVideoModel>> callback) {

        new Thread(() -> {

            List<GalleryVideoModel> albums = new ArrayList<>();
            if (context == null) {
                callback.onFetch(albums);
                return;
            }

            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Video.Media.BUCKET_ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME
            };

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    MediaStore.Video.Media.DATE_MODIFIED + " DESC");

            ArrayList<String> ids = new ArrayList<>();
            if (cursor != null) {

                final int COLUMN_ID = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID);
                final int COLUMN_BUCKET_NAME = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                final int COLUMN_DATA = cursor.getColumnIndex(MediaStore.Video.Media.DATA);

                while (cursor.moveToNext()) {
                    try {
                        GalleryVideoModel album = new GalleryVideoModel();
                        album.setId(cursor.getString(COLUMN_ID));
                        if (!ids.contains(album.getId())) {
                            album.setCaption(cursor.getString(COLUMN_BUCKET_NAME));
                            album.setPath(cursor.getString(COLUMN_DATA));

                            //check and add ALL for first item
                            if (albums.size() == 0) {
                                albums.add(new GalleryVideoModel("-1", context.getString(R.string.all), album.getPath()));
                            }
                            albums.add(album);
                            ids.add(album.getId());

                        }//else could be counter
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            callback.onFetch(albums);

        }).start();
    }

    public static void getDeviceMusics(Context context, boolean sortByDate, FetchListener<List<GalleryMusicModel>> callback) {

        new Thread(() -> {

            List<GalleryMusicModel> musics = new ArrayList<>();
            if (context == null) {
                callback.onFetch(musics);
                return;
            }

            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE
            };

            String sortType;
            if (sortByDate) sortType = MediaStore.Audio.Media.DATE_ADDED + " DESC";
            else sortType = MediaStore.Audio.Media.TITLE + " ASC";

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    sortType
            );

            if (cursor != null) {

                final int COLUMN_DATA = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                final int COLUMN_ARTIST = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                final int COLUMN_TITLE = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

                while (cursor.moveToNext()) {
                    try {
                        GalleryMusicModel music = new GalleryMusicModel();
                        music.setId(musics.size());
                        music.setPath(cursor.getString(COLUMN_DATA));
                        music.setTitle(cursor.getString(COLUMN_TITLE));
                        String artist = cursor.getString(COLUMN_ARTIST);
                        if (artist.contains("unknown"))
                            artist = context.getString(R.string.unknown);
                        music.setArtist(artist);
                        musics.add(music);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            callback.onFetch(musics);

        }).start();

    }

/*
    private static String getMusicCover(Context context ,long id) {
        if (context == null) return null;
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID+ "=?",
                new String[] {String.valueOf(id)},
                null);

        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                }
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
*/

    public interface FetchListener<T> {
        void onFetch(T result);
    }

    /**
     * sorts based on the files name
     */
    public static class SortFileName implements Comparator<StructFileManager> {
        @Override
        public int compare(StructFileManager f1, StructFileManager f2) {
            if (f1.nameStr == null)
                return 1;
            else
                return f1.nameStr.compareTo(f2.nameStr);
        }
    }

    /**
     * sorts based on the file date modify
     */
    public static class SortFileDate implements Comparator<StructFileManager> {
        @Override
        public int compare(StructFileManager obj1, StructFileManager obj2) {
            File f1 = new File(obj1.path);
            File f2 = new File(obj2.path);
            if ((f1.lastModified() == f2.lastModified()))
                return 0;
            else if (f1.lastModified() > f2.lastModified())
                return -1;
            else
                return 1;
        }
    }

    /**
     * sorts based on a file or folder. folders will be listed first
     */
    public static class SortFolder implements Comparator<StructFileManager> {
        @Override
        public int compare(StructFileManager obj1, StructFileManager obj2) {
            File f1 = new File(obj1.path);
            File f2 = new File(obj2.path);
            if (f1.isDirectory() == f2.isDirectory())
                return 0;
            else if (f1.isDirectory() && !f2.isDirectory())
                return -1;
            else
                return 1;
        }
    }
}
