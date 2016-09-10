package com.iGap.activitys;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterShearedMedia;
import com.iGap.module.OnComplete;
import com.iGap.module.StructSharedMedia;
import com.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

/**
 * Created by android3 on 9/4/2016.
 */
public class ActivityShearedMedia extends ActivityEnhanced {

    private RecyclerView recyclerView;
    private ArrayList<StructSharedMedia> list;
    private AdapterShearedMedia mAdapter;
    private int spanItemCount = 3;
    private TextView txtSharedMedia;
    private TextView txtNumberOfSelected;
    private LinearLayout ll_AppBarSelected;
    private OnComplete complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheared_media);

        initComponent();
    }

    @Override
    public void onBackPressed() {

        if (!mAdapter.resetSelected()) {
            super.onBackPressed();
        }
    }


    private void initComponent() {

        Button btnBack = (Button) findViewById(R.id.asm_btn_back);
        btnBack.setTypeface(G.fontawesome);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Button btnMenu = (Button) findViewById(R.id.asm_btn_menu);
        btnMenu.setTypeface(G.fontawesome);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpMenuSharedMedai();
            }
        });


        txtSharedMedia = (TextView) findViewById(R.id.asm_txt_sheared_media);


        complete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                int whatAction = 0;
                String number = "0";

                if (messageOne != null)
                    if (messageOne.length() > 0)
                        whatAction = Integer.parseInt(messageOne);

                if (MessageTow != null)
                    if (MessageTow.length() > 0)
                        number = MessageTow;

                callBack(result, whatAction, number);
            }
        };

        recyclerView = (RecyclerView) findViewById(R.id.asm_recycler_view_sheared_media);

        showMedia();

        initAppbarSelected();

    }

    private void initAppbarSelected() {

        Button btnCloseAppBarSelected = (Button) findViewById(R.id.asm_btn_close_layout);
        btnCloseAppBarSelected.setTypeface(G.fontawesome);
        btnCloseAppBarSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.resetSelected();
            }
        });


        Button btnForwardSelected = (Button) findViewById(R.id.asm_btn_forward_selected);
        btnForwardSelected.setTypeface(G.fontawesome);
        btnForwardSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnForwardSelected");
            }
        });

        Button btnDeleteSelected = (Button) findViewById(R.id.asm_btn_delete_selected);
        btnDeleteSelected.setTypeface(G.fontawesome);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnDeleteSelected");

            }
        });

        txtNumberOfSelected = (TextView) findViewById(R.id.asm_txt_number_of_selected);
        txtNumberOfSelected.setTypeface(G.fontawesome);


        ll_AppBarSelected = (LinearLayout) findViewById(R.id.asm_ll_appbar_selelected);

    }


    private void callBack(boolean result, int whatAction, String number) {

        switch (whatAction) {

            case 1://for show or gone layout appBar selected
                if (result) {
                    ll_AppBarSelected.setVisibility(View.VISIBLE);
                    txtNumberOfSelected.setText(number);
                } else {
                    ll_AppBarSelected.setVisibility(View.GONE);
                }
                break;
        }
    }


    //********************************************************************************************

    public void popUpMenuSharedMedai() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.pop_up_shared_media)
                .contentColor(Color.BLACK)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {
                            case 0:
                                showMedia();
                                break;
                            case 1:
                                showFile();
                                break;
                            case 2:
                                showLink();
                                break;
                            case 3:
                                showMusic();
                                break;
                        }


                    }
                }).show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp180);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        dialog.getWindow().setAttributes(layoutParams);
    }

    private void showMedia() {
        fillListImage();

        txtSharedMedia.setText(getString(R.string.shared_media));

        mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, list, txtSharedMedia.getText().toString(), complete);
        final GridLayoutManager gLayoutManager = new GridLayoutManager(ActivityShearedMedia.this, spanItemCount);

        gLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (list.get(position).messgeType == ProtoGlobal.RoomMessageType.TEXT)
                    return spanItemCount;
                else
                    return 1;

            }
        });

        recyclerView.setLayoutManager(gLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int viewWidth = recyclerView.getMeasuredWidth();
                float cardViewWidth = getResources().getDimension(R.dimen.dp120);
                int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);

                if (newSpanCount < 3)
                    newSpanCount = 3;

                spanItemCount = newSpanCount;
                gLayoutManager.setSpanCount(newSpanCount);
                gLayoutManager.requestLayout();
            }
        });


    }

    private void showFile() {

        txtSharedMedia.setText(getString(R.string.shared_files));

        fillListFile();

        mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, list, txtSharedMedia.getText().toString(), complete);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityShearedMedia.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    private void showLink() {
        txtSharedMedia.setText("Shared Link");
    }

    private void showMusic() {
        txtSharedMedia.setText("Shared Music");
    }

    //********************************************************************************************

    private void fillListImage() {

        list = new ArrayList<>();

        StructSharedMedia item1 = new StructSharedMedia();
        item1.fileTime = "May 2016";
        item1.messgeType = ProtoGlobal.RoomMessageType.TEXT;
        list.add(item1);

        StructSharedMedia item2 = new StructSharedMedia();
        item2.messgeType = ProtoGlobal.RoomMessageType.IMAGE;
        item2.filePath = R.mipmap.b + "";
        item2.fileName = "image 1";
        item2.fileTime = " 2016/3/29  11:29";
        list.add(item2);

        StructSharedMedia item3 = new StructSharedMedia();
        item3.messgeType = ProtoGlobal.RoomMessageType.IMAGE;
        item3.filePath = R.mipmap.c + "";
        item3.fileName = "image 2";
        item3.fileTime = " 2016/1/2  11:29";
        list.add(item3);

        StructSharedMedia item4 = new StructSharedMedia();
        item4.messgeType = ProtoGlobal.RoomMessageType.VIDEO;
        item4.fileInfo = "3:12";
        item4.filePath = R.mipmap.d + "";
        list.add(item4);


        StructSharedMedia item5 = new StructSharedMedia();
        item5.messgeType = ProtoGlobal.RoomMessageType.VIDEO;
        item5.fileInfo = "6:56";
        item5.filePath = R.mipmap.e + "";
        list.add(item5);

        StructSharedMedia item6 = new StructSharedMedia();
        item6.messgeType = ProtoGlobal.RoomMessageType.IMAGE;
        item6.filePath = R.mipmap.f + "";
        list.add(item6);

        list.add(item1);

        StructSharedMedia item7 = new StructSharedMedia();
        item7.filePath = R.mipmap.g + "";
        item7.messgeType = ProtoGlobal.RoomMessageType.IMAGE;
        list.add(item7);

        StructSharedMedia item8 = new StructSharedMedia();
        item8.filePath = R.mipmap.h + "";
        item8.messgeType = ProtoGlobal.RoomMessageType.IMAGE;
        list.add(item8);


    }

    private void fillListFile() {

        list = new ArrayList<>();

        StructSharedMedia item1 = new StructSharedMedia();
        item1.fileTime = "May 2016";
        item1.messgeType = ProtoGlobal.RoomMessageType.TEXT;
        list.add(item1);

        StructSharedMedia item2 = new StructSharedMedia();
        item2.messgeType = ProtoGlobal.RoomMessageType.FILE;
        item2.filePath = "lkjdf.png";
        item2.fileName = "image";
        item2.fileInfo = " 2016/3/29  11:29";
        list.add(item2);

        StructSharedMedia item3 = new StructSharedMedia();
        item3.messgeType = ProtoGlobal.RoomMessageType.FILE;
        item3.filePath = "lkjdf.mp3";
        item3.fileName = "image 2";
        item3.fileInfo = " 2016/1/2  11:29";
        list.add(item3);

        StructSharedMedia item4 = new StructSharedMedia();
        item4.messgeType = ProtoGlobal.RoomMessageType.FILE;
        item4.fileInfo = "3:12";
        item4.filePath = "lkjdf.mp4";
        list.add(item4);


        StructSharedMedia item5 = new StructSharedMedia();
        item5.messgeType = ProtoGlobal.RoomMessageType.FILE;
        item5.fileInfo = "6:56";
        item5.filePath = "lkjdf.apk";
        list.add(item5);

        StructSharedMedia item6 = new StructSharedMedia();
        item6.messgeType = ProtoGlobal.RoomMessageType.FILE;
        item6.filePath = "lkjdf.html";
        list.add(item6);

        list.add(item1);

        StructSharedMedia item7 = new StructSharedMedia();
        item7.filePath = "lkjdf.pdf";
        item7.messgeType = ProtoGlobal.RoomMessageType.FILE;
        list.add(item7);

        StructSharedMedia item8 = new StructSharedMedia();
        item8.filePath = "lkjdf.xls";
        item8.messgeType = ProtoGlobal.RoomMessageType.FILE;
        list.add(item8);

    }

}
