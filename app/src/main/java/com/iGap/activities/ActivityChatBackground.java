package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import com.iGap.G;
import com.iGap.IntentRequests;
import com.iGap.R;
import com.iGap.adapter.AdapterChatBackground;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.HelperCopyFile;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructAdapterBackground;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.G.chatBackground;

public class ActivityChatBackground extends ActivityEnhanced {

    public static String savePath;
    public MaterialDesignTextView txtSet;
    private MaterialDesignTextView txtBack;
    private File addFile;
    private int spanCount = 3;
    private RippleView rippleBack;
    private RecyclerView rcvContent;
    private AdapterChatBackground adapterChatBackgroundSetting;
    private List<StructAdapterBackground> items = new ArrayList<>();
    private int spanItemCount = 3;

    @Override
    protected void onResume() {
        super.onResume();

        G.currentActivity = this;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_background);

        txtBack = (MaterialDesignTextView) findViewById(R.id.stcb_txt_back);
        rippleBack = (RippleView) findViewById(R.id.stcb_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        //        int wdith = G.context.getResources().getDisplayMetrics().widthPixels;

        try {
            copyFromAsset();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //        if (wdith <= 600) {
        //            spanCount = 3;
        //        } else if (wdith <= 720) {
        //            spanCount = 4;
        //        } else if (wdith <= 720){
        //            spanCount = 4;
        //        }

        final GridLayoutManager gLayoutManager = new GridLayoutManager(ActivityChatBackground.this, spanItemCount);

        rcvContent = (RecyclerView) findViewById(R.id.rcvContent);
        adapterChatBackgroundSetting = new AdapterChatBackground(items);
        rcvContent.setAdapter(adapterChatBackgroundSetting);
        rcvContent.setDrawingCacheEnabled(true);
        rcvContent.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rcvContent.setItemViewCacheSize(100);
        rcvContent.setLayoutManager(gLayoutManager);
        rcvContent.clearAnimation();
        setItem();
        adapterChatBackgroundSetting.notifyDataSetChanged();

        rcvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rcvContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int viewWidth = rcvContent.getMeasuredWidth();
                float cardViewWidth = getResources().getDimension(R.dimen.dp120);
                int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);

                if (newSpanCount < 3) newSpanCount = 3;

                spanItemCount = newSpanCount;
                gLayoutManager.setSpanCount(newSpanCount);
                gLayoutManager.requestLayout();
            }
        });
    }

    public void setItem() {

        addFile = new File(G.DIR_CHAT_BACKGROUND);
        File file[] = addFile.listFiles();
        for (int i = 0; i < file.length; i++) {
            StructAdapterBackground item = new StructAdapterBackground();
            if (!file[i].getPath().equals(chatBackground.toString())) {
                item.setId(i);
                item.setPathImage(file[i].toString());
                items.add(item);
            }
        }

        int intPlus = 1;
        if (chatBackground.exists()) intPlus = 2;
        for (int i = 0; i < intPlus; i++) {
            StructAdapterBackground item = new StructAdapterBackground();
            if (chatBackground.exists() && i == 0) {
                item.setId(i);
                item.setPathImage(chatBackground.toString());
            } else {
                item.setId(i);
            }

            items.add(0, item);
        }
        adapterChatBackgroundSetting.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentRequests.REQ_CAMERA && resultCode == RESULT_OK) {// result for camera

            if (data != null) {

                items.clear();
                AdapterChatBackground.imageLoader.clearDiskCache();
                AdapterChatBackground.imageLoader.clearMemoryCache();
                setItem();
            }
        } else if (requestCode == IntentRequests.REQ_GALLERY && resultCode == RESULT_OK) {// result for gallery

            String pathImageUser = getRealPathFromURI(data.getData());
            HelperCopyFile.copyFile(pathImageUser, chatBackground.toString());
            items.clear();
            if (AdapterChatBackground.imageLoader != null) {
                AdapterChatBackground.imageLoader.clearDiskCache();
                AdapterChatBackground.imageLoader.clearMemoryCache();
            }
            setItem();

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

    private void copyFromAsset() throws IOException {
        String[] files = null;
        files = getAssets().list("back");

        if (chatBackground.length() == 0) {
            for (String file : files) {

                InputStream inputStream = getAssets().open("back/" + file);
                String outFileName = chatBackground.toString() + file;
                OutputStream outputStream = new FileOutputStream(outFileName);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

            }
        }
    }
}
