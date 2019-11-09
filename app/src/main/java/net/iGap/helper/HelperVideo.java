package net.iGap.helper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;


public class HelperVideo {

    private LruCache<String, Drawable> mThumbnailCacher;
    private int mThumbnailMode;
    private Map<String, AsyncTask> mTasks = new HashMap<>();
    private boolean isAbleToRemoveTask = true;

    public HelperVideo(int thumbMode) {
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

    public void loadVideoThumbnail(String path, ImageView iv) {
        if (mThumbnailCacher != null && mThumbnailCacher.get(path) != null) {
            iv.setImageDrawable(mThumbnailCacher.get(path));
        } else {
            if (!mTasks.containsKey(path)) {
                VideoThumbLoader videoThumbLoader = new VideoThumbLoader(iv, path);
                mTasks.put(path, videoThumbLoader);
                videoThumbLoader.execute();
            }
        }
    }

    private class VideoThumbLoader extends AsyncTask<String, Void, Drawable> {
        private ImageView imgView;
        private String path;

        public VideoThumbLoader(ImageView imageView, String path) {
            this.imgView = imageView;
            this.path = path;
        }

        @Override
        protected Drawable doInBackground(String... params) {

            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, mThumbnailMode);
            Drawable drawable = new BitmapDrawable(imgView.getResources(), bitmap);

            // save cache
            if (mThumbnailCacher != null && mThumbnailCacher.get(path) == null) {
                mThumbnailCacher.put(path, drawable);
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable image) {
            if (image != null && imgView != null && imgView.getTag().equals(path)) {
                imgView.setImageDrawable(image);
            }
            //remove task from list when ended
            if (isAbleToRemoveTask) mTasks.remove(path);
        }
    }
}
