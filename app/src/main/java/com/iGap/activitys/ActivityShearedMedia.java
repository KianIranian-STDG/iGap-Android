package com.iGap.activitys;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterShearedMedia;
import com.iGap.module.StructSharedMedia;
import com.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

/**
 * Created by android3 on 9/4/2016.
 */
public class ActivityShearedMedia extends ActivityEnhanced {

    ArrayList<StructSharedMedia> list;
    AdapterShearedMedia mAdapter;
    int spanItemCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheared_media);

        initComponent();
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

        initRecycleView();

    }


    private void initRecycleView() {

        fillList();


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.asm_recycler_view_sheared_media);
        mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, list);
        final GridLayoutManager gLayoutManager = new GridLayoutManager(ActivityShearedMedia.this, spanItemCount);

        gLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                switch (mAdapter.getItemViewType(position)) {

                    case AdapterShearedMedia.Type_Header:
                        return spanItemCount;
                    case AdapterShearedMedia.Type_Item:
                        return 1;
                    default:
                        return -1;
                }
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


    public void popUpMenuSharedMedai() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.pop_up_shared_media)
                .contentColor(Color.BLACK)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        Log.e("ddd", which + ": " + text);
                    }
                }).show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp160);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        dialog.getWindow().setAttributes(layoutParams);
    }


    private void fillList() {

        list = new ArrayList<>();

        StructSharedMedia item1 = new StructSharedMedia();
        item1.fileTime = "May 2016";
        item1.messgeType = ProtoGlobal.RoomMessageType.TEXT;
        list.add(item1);

        StructSharedMedia item2 = new StructSharedMedia();
        item2.filePath = R.mipmap.b + "";
        item2.fileName = "image 1";
        item2.fileTime = " 2016/3/29  11:29";
        list.add(item2);

        StructSharedMedia item3 = new StructSharedMedia();
        item3.filePath = R.mipmap.c + "";
        item3.fileName = "image 2";
        item3.fileTime = " 2016/1/2  11:29";
        list.add(item3);

        StructSharedMedia item4 = new StructSharedMedia();
        item4.filePath = R.mipmap.d + "";
        list.add(item4);


        StructSharedMedia item5 = new StructSharedMedia();
        item5.filePath = R.mipmap.e + "";
        list.add(item5);

        StructSharedMedia item6 = new StructSharedMedia();
        item6.filePath = R.mipmap.f + "";
        list.add(item6);

        list.add(item1);

        StructSharedMedia item7 = new StructSharedMedia();
        item7.filePath = R.mipmap.g + "";
        list.add(item7);

        StructSharedMedia item8 = new StructSharedMedia();
        item8.filePath = R.mipmap.h + "";
        list.add(item8);


    }


}
