package com.iGap.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterExplorer;
import com.iGap.helper.HelperMimeType;
import com.iGap.module.StructExplorerItem;

import java.io.File;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by android3 on 9/7/2016.
 */
public class ActivityExplorer extends ActivityEnhanced {


    String nextnode;
    int rootcount = 0;
    ArrayList<StructExplorerItem> item;
    ArrayList<String> node;          //path of the hierychical directory
    ArrayList<Integer> mscroll;

    Button btnBack;

    StructExplorerItem x;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);


        recyclerView = (RecyclerView) findViewById(R.id.ae_recycler_view_explorer);
        recyclerView.setItemViewCacheSize(100);
        mLayoutManager = new LinearLayoutManager(ActivityExplorer.this);
        recyclerView.setLayoutManager(mLayoutManager);

        item = new ArrayList<StructExplorerItem>();
        node = new ArrayList<String>();
        mscroll = new ArrayList<Integer>();

        btnBack = (Button) findViewById(R.id.ae_btn_back);
        btnBack.setTypeface(G.fontawesome);
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        firstfill();
    }


    private void onItemClickInernal(int position) {

        if (node.size() == rootcount)
            nextnode = node.get(position);
        else
            nextnode = node.get(node.size() - 1) + "/" + item.get(position).name;

        fill(nextnode, position);
    }


    @Override
    public void onBackPressed() {
        int size = node.size();
        if (size == rootcount)
            super.onBackPressed();
        else if (size == rootcount + 1)
            firstfill();
        else {
            node.remove(node.size() - 1);
            fill(node.get(node.size() - 1), 0);
            node.remove(node.size() - 1);
            mscroll.remove(mscroll.size() - 1);
            recyclerView.scrollToPosition(mscroll.remove(mscroll.size() - 1));

        }
    }


    void firstfill() {

        item.clear();
        node.clear();
        rootcount = 0;
        mscroll.clear();
        mscroll.add(0);

        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath()).exists()) {
            x = new StructExplorerItem();
            x.name = "Device";
            x.image = R.mipmap.j_device;
            x.path = Environment.getExternalStorageDirectory().getAbsolutePath();
            item.add(x);
            node.add(Environment.getExternalStorageDirectory().getAbsolutePath());
            rootcount++;
        }

        if (new File("/storage/extSdCard/").exists()) {
            x = new StructExplorerItem();
            x.name = "SdCard";
            x.image = R.mipmap.j_sdcard;
            x.path = "/storage/extSdCard/";
            item.add(x);
            node.add("/storage/extSdCard/");
            rootcount++;
        }
        if (new File("/storage/sdcard1/").exists()) {
            x = new StructExplorerItem();
            x.name = "SdCard1";
            x.image = R.mipmap.j_sdcard;
            x.path = "/storage/sdcard1/";
            item.add(x);
            node.add("/storage/sdcard1/");
            rootcount++;
        }
        if (new File("/storage/usbcard1/").exists()) {
            x = new StructExplorerItem();
            x.name = "usbcard";
            x.image = R.mipmap.j_usb;
            x.path = "/storage/usbcard1/";
            item.add(x);
            node.add("/storage/usbcard1/");
            rootcount++;
        }

        recyclerView.setAdapter(new AdapterExplorer(item, new AdapterExplorer.OnItemClickListenerExplorer() {

            @Override
            public void onItemClick(View view, int position) {

                onItemClickInernal(position);
            }
        }));

    }


    void fill(String nextnod, int position) {

        try {

            File fileDir = new File(nextnod);

            if (fileDir.isDirectory()) {
                mscroll.add(position);

                String[] tmpname = fileDir.list();

                if (tmpname == null) {
                    return;
                }

                item.clear();
                for (int i = 0; i < tmpname.length; i++) {
                    if (tmpname[i].startsWith("."))
                        continue;
                    else {
                        File tmp = new File(fileDir.getAbsolutePath() + "/" + tmpname[i]);
                        if (tmp.canRead()) {
                            x = new StructExplorerItem();
                            x.name = tmpname[i];

                            if (tmp.isDirectory())
                                x.image = R.mipmap.actionbar_icon_myfiles;
                            else
                                x.image = HelperMimeType.getMimeResource(tmpname[i]);
                            x.path = tmp.getAbsolutePath();
                            item.add(x);
                        }
                    }
                }

                recyclerView.setAdapter(new AdapterExplorer(item, new AdapterExplorer.OnItemClickListenerExplorer() {

                    @Override
                    public void onItemClick(View view, int position) {

                        onItemClickInernal(position);
                    }
                }));

                node.add(nextnod);
            } else if (fileDir.isFile()) {
                Intent data = new Intent();
                data.setData(Uri.parse(fileDir.getAbsolutePath()));

                setResult(Activity.RESULT_OK, data);
                finish();

            }

        } catch (Exception e) {
            Toast.makeText(ActivityExplorer.this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }


//    Integer setPicture(File f) {
//
//        Integer x = null;
//        String extention;
//
//        if (f.getName().indexOf(".") == -1) {
//            extention = "";
//        }
//        else {
//            extention = f.getName().toLowerCase();//.substring(f.getName().length() - 3, f.getName().length());
//        }
//
//        if (extention.endsWith("jpg") || extention.endsWith("jpeg") || extention.endsWith("png") || extention.endsWith("bmp"))
//            x = R.mipmap.j_pic;
//        else if (extention.endsWith("apk"))
//            x = R.mipmap.j_apk;
//        else if (extention.endsWith("mp3") || extention.endsWith("ogg") || extention.endsWith("wma"))
//            x = R.mipmap.j_mp3;
//        else if (extention.endsWith("mp4") || extention.endsWith("3gp") || extention.endsWith("avi") || extention.endsWith("mpg") || extention.endsWith("flv") || extention.endsWith("wmv") || extention.endsWith("m4v"))
//            x = R.mipmap.j_video;
//        else if (extention.endsWith("m4a") || extention.endsWith("amr") || extention.endsWith("wav"))
//            x = R.mipmap.j_audio;
//        else if (extention.endsWith("html") || extention.endsWith("htm"))
//            x = R.mipmap.j_html;
//        else if (extention.endsWith("pdf"))
//            x = R.mipmap.j_pdf;
//        else if (extention.endsWith("ppt"))
//            x = R.mipmap.j_ppt;
//        else if (extention.endsWith("snb"))
//            x = R.mipmap.j_snb;
//        else if (extention.endsWith("txt"))
//            x = R.mipmap.j_txt;
//        else if (extention.endsWith("doc"))
//            x = R.mipmap.j_word;
//        else if (extention.endsWith("xls"))
//            x = R.mipmap.j_xls;
//        else if (f.isFile())
//            x = R.mipmap.j_ect;
//        else if (f.isDirectory())
//            x = R.mipmap.actionbar_icon_myfiles;
//        else
//            x = R.mipmap.j_ect;
//
//        return x;
//    }


}
