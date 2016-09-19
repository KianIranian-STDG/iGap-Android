package com.iGap.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.CircleImageView;

/**
 * Created by android3 on 9/18/2016.
 */
public class ActivityGroupProfile extends ActivityEnhanced {


    CircleImageView imvGroupAvatar;
    TextView txtGroupNameTitle;
    TextView txtGroupName;
    TextView txtNumberOfSharedMedia;
    TextView txtMemberNumber;
    TextView txtMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        initComponent();

    }


    private void initComponent() {

        Button btnBack = (Button) findViewById(R.id.agp_btn_back);
        btnBack.setTypeface(G.fontawesome);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnMenu = (Button) findViewById(R.id.agp_btn_menu);
        btnMenu.setTypeface(G.fontawesome);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "menu clicked");
            }
        });


        imvGroupAvatar = (CircleImageView) findViewById(R.id.agp_imv_group_avatar);
        txtGroupNameTitle = (TextView) findViewById(R.id.agp_txt_group_name_title);
        txtGroupName = (TextView) findViewById(R.id.agp_txt_group_name);
        txtNumberOfSharedMedia = (TextView) findViewById(R.id.agp_txt_number_of_shared_media);
        txtMemberNumber = (TextView) findViewById(R.id.agp_txt_number_of_shared_media);


        txtMore = (TextView) findViewById(R.id.agp_txt_more);
        txtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "more clicked");
            }
        });


        CircleImageView imvAddMember = (CircleImageView) findViewById(R.id.agp_imv_add_member);
        imvAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "add member clicked");
            }
        });


        initRecycleView();

        TextView txtNotification = (TextView) findViewById(R.id.agp_txt_str_notification_and_sound);
        txtNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "Notification clicked");
            }
        });


        TextView txtDeleteGroup = (TextView) findViewById(R.id.agp_txt_str_delete_and_leave_group);
        txtDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "txtDeleteGroup");
            }
        });


    }


    private void initRecycleView() {


    }


}
