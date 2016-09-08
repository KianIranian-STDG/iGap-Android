package com.iGap.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.iGap.R;

import java.io.File;

/**
 * Created by android3 on 9/7/2016.
 */
public class HelperMimeType {


    /**
     * get a picture for this extension
     *
     * @param extention
     * @return
     */
    public static Integer getMimeResource(String extention) {


        Integer x = null;

        if (extention == null)
            return null;

        extention = extention.toLowerCase();


        if (extention.endsWith("jpg") || extention.endsWith("jpeg") || extention.endsWith("png") || extention.endsWith("bmp"))
            x = R.mipmap.j_pic;
        else if (extention.endsWith("apk"))
            x = R.mipmap.j_apk;
        else if (extention.endsWith("mp3") || extention.endsWith("ogg") || extention.endsWith("wma"))
            x = R.mipmap.j_mp3;
        else if (extention.endsWith("mp4") || extention.endsWith("3gp") || extention.endsWith("avi") || extention.endsWith("mpg") || extention.endsWith("flv") || extention.endsWith("wmv") || extention.endsWith("m4v"))
            x = R.mipmap.j_video;
        else if (extention.endsWith("m4a") || extention.endsWith("amr") || extention.endsWith("wav"))
            x = R.mipmap.j_audio;
        else if (extention.endsWith("html") || extention.endsWith("htm"))
            x = R.mipmap.j_html;
        else if (extention.endsWith("pdf"))
            x = R.mipmap.j_pdf;
        else if (extention.endsWith("ppt"))
            x = R.mipmap.j_ppt;
        else if (extention.endsWith("snb"))
            x = R.mipmap.j_snb;
        else if (extention.endsWith("txt"))
            x = R.mipmap.j_txt;
        else if (extention.endsWith("doc"))
            x = R.mipmap.j_word;
        else if (extention.endsWith("xls"))
            x = R.mipmap.j_xls;
        else
            x = R.mipmap.j_ect;

        return x;
    }


    public static Bitmap getMimePic(Context context, Integer src) {

        Bitmap bitmap = null;

        if (src == null)
            return null;

        bitmap = BitmapFactory.decodeResource(context.getResources(), src);

        return bitmap;
    }


    /**
     * return Thumbnail bitmap from file path image
     */

    public void LoadImageTumpnail(ImageView imageView, String path) {

        new LoadImageToImageView(imageView, path).execute();

    }

    public void LoadVideoTumpnail(ImageView imageView, String path) {
        new getVideoThumbnail(imageView, path).execute();
    }


    class LoadImageToImageView extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        private String path;


        public LoadImageToImageView(ImageView imageView, String path) {
            imv = imageView;
            this.path = path;
        }


        @Override
        protected Bitmap doInBackground(Object... params) {

            Bitmap bitmap = null;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 8;
            File file = new File(path);

            if (file.exists())
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            Log.e("ddd", "LoadImageToImageView");
            if (result != null && imv != null) {
                imv.setImageBitmap(result);
            }
        }

    }


    /**
     * return Thumbnail bitmap from file path video
     */

    class getVideoThumbnail extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        private String path;


        public getVideoThumbnail(ImageView imageView, String path) {
            imv = imageView;
            this.path = path;
        }


        @Override
        protected Bitmap doInBackground(Object... params) {

            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
            return bMap;
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && imv != null) {
                imv.setImageBitmap(result);
            }
        }
    }

}
