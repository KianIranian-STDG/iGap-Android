package net.iGap.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;


public class HelperThumbnail {

    /**
     * dont use it until write best code with cursor
     */
    private LruCache<String, Drawable> mThumbnailCacher;
    private int mThumbnailMode;
    private Map<String, AsyncTask> mTasks = new HashMap<>();
    private boolean isAbleToRemoveTask = true;

    public HelperThumbnail(int thumbMode) {
        mThumbnailMode = thumbMode;
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int maxSize = maxMemory / 4;
        mThumbnailCacher = new LruCache<>(maxSize);
    }

    public void clearCache() {
        //clear all ongoing tasks
        isAbleToRemoveTask = false;
        for (HashMap.Entry<String, AsyncTask> entry : mTasks.entrySet()) {
            entry.getValue().cancel(true);
        }
        mTasks.clear();
        mThumbnailCacher = null;
    }

    public void loadThumbnail(boolean isVideo, String key, String path, ImageView iv) {
        if (mThumbnailCacher != null && mThumbnailCacher.get(key) != null) {
            iv.setImageDrawable(mThumbnailCacher.get(key));
        } else {
            if (!mTasks.containsKey(key)) {
                ThumbLoader thumbLoader = new ThumbLoader(iv, path, key, isVideo);
                mTasks.put(key, thumbLoader);
                thumbLoader.execute();
            }
        }
    }

    private class ThumbLoader extends AsyncTask<String, Void, Drawable> {
        private ImageView imgView;
        private String path;
        private String key;
        boolean isVideo;

        public ThumbLoader(ImageView imageView, String path, String key, boolean isVideo) {
            this.imgView = imageView;
            this.path = path;
            this.key = key;
            this.isVideo = isVideo;
        }

        @Override
        protected Drawable doInBackground(String... params) {

            Drawable drawable = null;
            Bitmap bitmap = null;

            if (isVideo) {
                bitmap = ThumbnailUtils.createVideoThumbnail(path, mThumbnailMode);
                drawable = new BitmapDrawable(imgView.getResources(), bitmap);
            } else {
                bitmap = getMusicCover(path);
                drawable = new BitmapDrawable(imgView.getResources(), bitmap);
            }

            // save cache
            if (mThumbnailCacher != null && mThumbnailCacher.get(key) == null) {
                mThumbnailCacher.put(key, drawable);
            }

            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable image) {
            if (image != null && imgView != null && imgView.getTag().equals(key)) {
                imgView.setImageDrawable(image);
            }
            //remove task from list when ended
            if (isAbleToRemoveTask) mTasks.remove(key);
        }

        private Bitmap getMusicCover(String path) {
            if (path == null) return null;
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            byte[] data = mmr.getEmbeddedPicture();
            if (data != null) {
                return BitmapFactory.decodeByteArray(data, 0, data.length);
            }
            return null;
        }
    }
}
