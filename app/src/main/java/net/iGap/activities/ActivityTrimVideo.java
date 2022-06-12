/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.iGap.G;
import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.MaterialDesignTextView;
import net.igap.video.trim.K4LVideoTrimmer;
import net.igap.video.trim.interfaces.OnK4LVideoListener;
import net.igap.video.trim.interfaces.OnTrimVideoListener;

import java.io.File;


public class ActivityTrimVideo extends ActivityEnhanced implements OnTrimVideoListener, OnK4LVideoListener {

    int videoWidth = 641;
    int videoHeight = 481;
    private String path;
    private int duration;
    private TextView txtDetail;
    private TextView txtTime;
    private TextView txtSize;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_video_trime);

        MaterialDesignTextView pu_txt_agreeImage = findViewById(R.id.pu_txt_agreeImage);
        pu_txt_agreeImage.setTextColor(Theme.getColor(Theme.key_white));
        txtDetail = findViewById(R.id.stfaq_txt_detail);
        txtTime = findViewById(R.id.stfaq_txt_time);
        txtSize = findViewById(R.id.stfaq_txt_size);
        View btnBack = findViewById(R.id.pu_ripple_back);
        btnBack.setOnClickListener(v -> onBackPressed());
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            path = bundle.getString("PATH");

            if (path == null) {
                finish();
                return;
            }
        }

        setInfo(path);

        K4LVideoTrimmer videoTrimmer = findViewById(R.id.timeLine);
        progressBar = findViewById(R.id.fvt_progress);
        if (videoTrimmer != null) {

            Uri uri = Uri.parse(path);
            if (uri == null) {
                onBackPressed();
                return;
            }

            videoTrimmer.setVideoURI(Uri.parse(path));
            videoTrimmer.setMaxDuration(duration);
            videoTrimmer.setOnTrimVideoListener(this);
            videoTrimmer.setDestinationPath(G.DIR_VIDEOS + "/");
            videoTrimmer.setVideoInformationVisibility(true);
        }
    }

    private void setInfo(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);

            File file = new File(path);
            if (file.exists()) {
                txtSize.setText("," + net.iGap.module.FileUtils.formatFileSize(file.length()));
            }

            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (time != null && time.length() > 0) {
                duration = ((Integer.parseInt(time)) / 1000) + 1;
                int minutes = duration / 60;
                int seconds = duration % 60;
                txtTime.setText("," + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            Bitmap bmp = retriever.getFrameAtTime();

            if (bmp != null) {
                videoHeight = bmp.getHeight();
                videoWidth = bmp.getWidth();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        txtDetail.setText(videoWidth + "X" + videoHeight);
    }

    @Override
    public void onTrimStarted() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void getResult(final Uri uri) {

        File file = new File(uri.getPath());
        //when file was not correct length is between digit 1082
        if (file.length() <= 1082) {
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this.getApplicationContext(), R.string.trim_error, Toast.LENGTH_SHORT).show();
            });
        } else {
            Intent data = new Intent();
            data.setData(uri);
            setResult(Activity.RESULT_OK, data);
            finish();
        }

    }

    @Override
    public void cancelAction() {

        Uri uriCancel = Uri.parse(path);
        Intent data = new Intent();
        data.setData(uriCancel);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void onError(String message) {
        Log.e("nazariii", "onError: " + message);
        G.handler.post(this::onBackPressed);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onVideoPrepared() {

    }


}
