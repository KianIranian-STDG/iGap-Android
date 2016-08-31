package com.iGap.activitys;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterComment;
import com.iGap.module.StructCommentInfo;

import java.util.ArrayList;

/**
 * Created by android3 on 8/31/2016.
 */
public class ActivityComment extends ActivityEnhanced {


    private int numberOfComment = 0;
    ArrayList<StructCommentInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_show);

        String messageID = null;
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            messageID = bundle.getString("MessageID");
            if (messageID == null)
                finish();
        }

        getCommentList(messageID);

        initComponent();

        initRecycleView();


    }


    private void getCommentList(String messageID) {


        list = new ArrayList<>();

        StructCommentInfo info = new StructCommentInfo();
        info.date = "agust 24";
        info.message = "this is a sample comment andf lsdkfj ldkjfldkjf ldkjf ldkfj ldfkhsjkfodfjkdlf ";
        info.senderName = "ali";
        info.senderID = " ali@kjfkd.com";
        info.time = "10:25";
        info.senderPicturePath = R.mipmap.a + "";
        list.add(info);

        list.add(info);
        list.add(info);


        numberOfComment = list.size();

    }

    private void initComponent() {

        Button btnBack = (Button) findViewById(R.id.acs_btn_back);
        btnBack.setTypeface(G.fontawesome);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        Button btnMenu = (Button) findViewById(R.id.acs_btn_menu);
        btnMenu.setTypeface(G.fontawesome);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnMenu  ");
            }
        });


        TextView txtNumberOfComment = (TextView) findViewById(R.id.acs_txt_number_of_comment);
        if (numberOfComment > 0)
            txtNumberOfComment.setText(numberOfComment + "  Comment");
        else
            txtNumberOfComment.setText(numberOfComment + "NO Comment");

    }

    private void initRecycleView() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.acs_recycler_view_comment);
        AdapterComment mAdapter = new AdapterComment(ActivityComment.this, list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityComment.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

}
