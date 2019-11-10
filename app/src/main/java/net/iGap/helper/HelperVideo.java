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

    public void loadVideoThumbnail(String key ,String path, ImageView iv) {
        if (mThumbnailCacher != null && mThumbnailCacher.get(key) != null) {
            iv.setImageDrawable(mThumbnailCacher.get(key));
        } else {
            if (!mTasks.containsKey(key)) {
                VideoThumbLoader videoThumbLoader = new VideoThumbLoader(iv, path , key);
                mTasks.put(key, videoThumbLoader);
                videoThumbLoader.execute();
            }
        }
    }

    private class VideoThumbLoader extends AsyncTask<String, Void, Drawable> {
        private ImageView imgView;
        private String path;
        private String key ;

        public VideoThumbLoader(ImageView imageView, String path , String key) {
            this.imgView = imageView;
            this.path = path;
            this.key = key ;
        }

        @Override
        protected Drawable doInBackground(String... params) {

            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, mThumbnailMode);
            Drawable drawable = new BitmapDrawable(imgView.getResources(), bitmap);

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
    }
}
