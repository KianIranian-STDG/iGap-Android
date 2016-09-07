package com.iGap.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.CircleImageView;

public class ActivitySettingNotification extends AppCompatActivity {

    private TextView txtBack;
    private CircleImageView imgLedMessage;

    private ViewGroup ltAlert ,ltMessagePreview , ltMedColorMessage,ltAlert_group,ltMessagePreview_group
            ,ltLedColor_group,ltApp_sound,ltVibrate,ltApp_preview,ltChat_sound,stns_layout_Contact_joined
            ,stns_layout_pinned_message,stns_layout_keep_alive_service,stns_layout_background_connection,stns_layout_badge_countent;
    private ToggleButton tgAlert  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification);

        imgLedMessage = (CircleImageView) findViewById(R.id.stns_img_ledColorMessage);
        txtBack = (TextView) findViewById(R.id.stns_txt_back);
        txtBack.setTypeface(G.fontawesome);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivitySettingNotification.this, ActivitySetting.class);
                startActivity(intent);
                finish();

            }
        });

    }
}
