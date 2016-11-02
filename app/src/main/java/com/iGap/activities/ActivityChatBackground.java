package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterChatBackground;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.HelperCopyFile;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructAdapterBackground;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityChatBackground extends ActivityEnhanced {

    public static Uri uriIntent;
    public static String savePath;
    public MaterialDesignTextView txtSet;
    private MaterialDesignTextView txtBack;
    private int myResultCodeCamera = 1;
    private int myResultCodeGallery = 0;
    private File addFile;
    private int spanCount = 3;
    private RippleView rippleBack;
    private RecyclerView rcvContent;
    private AdapterChatBackground adapterChatBackgroundSetting;
    private List<StructAdapterBackground> items = new ArrayList<>();

    @Override protected void onResume() {
        super.onResume();

        G.currentActivity = this;
    }

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_background);

        txtBack = (MaterialDesignTextView) findViewById(R.id.stcb_txt_back);
        rippleBack = (RippleView) findViewById(R.id.stcb_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        int wdith = G.context.getResources().getDisplayMetrics().widthPixels;

        if (wdith <= 720) {
            spanCount = 3;
        } else if (wdith <= 1280) {
            spanCount = 4;
        } else {
            spanCount = 4;
        }

        rcvContent = (RecyclerView) findViewById(R.id.rcvContent);
        adapterChatBackgroundSetting = new AdapterChatBackground(items);
        rcvContent.setAdapter(adapterChatBackgroundSetting);
        rcvContent.setLayoutManager(new GridLayoutManager(ActivityChatBackground.this, spanCount));
        rcvContent.clearAnimation();
        setItem();
        adapterChatBackgroundSetting.notifyDataSetChanged();
    }

    public void setItem() {

        addFile = new File(G.DIR_CHAT_BACKGROUND);
        File file[] = addFile.listFiles();
        for (int i = 0; i < file.length; i++) {
            StructAdapterBackground item = new StructAdapterBackground();
            if (i == 0) {
                item.setId(i);
                items.add(item);
                Log.i("CCCC", "a0: " + i);
            } else {
                if (i == 1 && (G.chatBackground.exists())) {
                    item.setId(i);
                    item.setPathImage(G.chatBackground.toString());
                    items.add(item);
                    Log.i("CCCC", "a1: " + i);
                } else if (!file[i].getPath().equals(G.chatBackground.toString())) {
                    item.setId(i);
                    item.setPathImage(file[i].toString());
                    items.add(item);
                    Log.i("CCCC", "a2: " + i);

                }
            }
        }

        adapterChatBackgroundSetting.notifyDataSetChanged();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == myResultCodeCamera && resultCode == RESULT_OK) {// result for camera

            items.clear();
            AdapterChatBackground.imageLoader.clearDiskCache();
            AdapterChatBackground.imageLoader.clearMemoryCache();
            setItem();
        } else if (requestCode == myResultCodeGallery
            && resultCode == RESULT_OK) {// result for gallery

            String pathImageUser = getRealPathFromURI(data.getData());
            HelperCopyFile.copyFile(pathImageUser, G.chatBackground.toString());
            items.clear();
            AdapterChatBackground.imageLoader.clearDiskCache();
            AdapterChatBackground.imageLoader.clearMemoryCache();
            setItem();
            Log.i("CCCC", "a4: " + pathImageUser);
        }
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
}
