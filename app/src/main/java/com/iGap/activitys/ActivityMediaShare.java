package com.iGap.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterMediaShare;
import com.iGap.module.StructMediaShare;

import java.util.ArrayList;
import java.util.List;

public class ActivityMediaShare extends AppCompatActivity {

    private TextView txtTitleToolbar, txtBack;
    private ImageView imgMenu;

    private RecyclerView rcvContent;
    private List<StructMediaShare> items = new ArrayList<>();
    private AdapterMediaShare adapterMediaShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_share);

        txtTitleToolbar = (TextView) findViewById(R.id.mh_txt_titleToolbar);


        rcvContent = (RecyclerView) findViewById(R.id.mh_rcvContent);
        adapterMediaShare = new AdapterMediaShare(items);
        rcvContent.setAdapter(adapterMediaShare);
        rcvContent.setLayoutManager(new GridLayoutManager(G.context, 4));
        setItem();

        txtBack = (TextView) findViewById(R.id.mh_txt_back);
        txtBack.setTypeface(G.fontawesome);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// button back

                startActivity(new Intent(ActivityMediaShare.this, ActivityChanelInfo.class));
                finish();
            }
        });

        imgMenu = (ImageView) findViewById(R.id.mh_img_menuPopup);
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(ActivityMediaShare.this, v, Gravity.BOTTOM);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.mh_mediaShared:

                                txtTitleToolbar.setText(getResources().getString(R.string.mh_popup_media));

                                Toast.makeText(ActivityMediaShare.this, "1", Toast.LENGTH_SHORT).show();
                                return true;

                            case R.id.mh_mediaFile:

                                txtTitleToolbar.setText(getResources().getString(R.string.mh_popup_file));
                                Toast.makeText(ActivityMediaShare.this, "2", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.mh_mediaLinks:

                                txtTitleToolbar.setText(getResources().getString(R.string.mh_popup_link));
                                Toast.makeText(ActivityMediaShare.this, "3", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.mh_mediaAudio:

                                txtTitleToolbar.setText(getResources().getString(R.string.mh_popup_audio));
                                Toast.makeText(ActivityMediaShare.this, "3", Toast.LENGTH_SHORT).show();
                                return true;
                        }

                        return true;
                    }
                });

                popupMenu.inflate(R.menu.mh_popup_menu);
                popupMenu.show();
            }
        });
    }

    private void setItem() {

        for (int i = 0; i < 20; i++) {

            StructMediaShare item = new StructMediaShare();

            item.setImage("" + G.imageFile);
            items.add(item);
        }

        adapterMediaShare.notifyDataSetChanged();

    }

}
