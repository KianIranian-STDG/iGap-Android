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
import com.iGap.fragments.FragmentShowImage;
import com.iGap.module.MusicPlayer;
import com.iGap.module.OnComplete;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoomMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by android3 on 9/4/2016.
 */
public class ActivityShearedMedia extends ActivityEnhanced {

    private RecyclerView recyclerView;
    private AdapterShearedMedia mAdapter;
    private int spanItemCount = 3;
    private TextView txtSharedMedia;
    private TextView txtNumberOfSelected;
    private LinearLayout ll_AppBarSelected;
    private OnComplete complete;
    private long roomId = 0;
    private ArrayList<RealmRoomMessage> mList;

    private LinearLayout mediaLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheared_media);

        roomId = getIntent().getExtras().getLong("RoomID");


        mediaLayout = (LinearLayout) findViewById(R.id.asm_ll_music_layout);


        if (MusicPlayer.mp != null) {
            mediaLayout.setVisibility(View.VISIBLE);
            ((TextView) mediaLayout.findViewById(R.id.mls_txt_music_name)).setText(MusicPlayer.musicName);
            ((TextView) mediaLayout.findViewById(R.id.mls_txt_music_time)).setText(MusicPlayer.milliSecondsToTimer((long) MusicPlayer.mp.getDuration()));

            if (MusicPlayer.mp.isPlaying()) {
                ((Button) mediaLayout.findViewById(R.id.mls_btn_play_music)).setText(G.context.getString(R.string.md_pause_button));
            } else {
                ((Button) mediaLayout.findViewById(R.id.mls_btn_play_music)).setText(G.context.getString(R.string.md_play_arrow));
            }
        }



        initComponent();
    }

    @Override
    public void onBackPressed() {
        FragmentShowImage myFragment = (FragmentShowImage) getFragmentManager().findFragmentByTag("Show_Image_fragment_shared_media");
        if (myFragment != null && myFragment.isVisible()) {
            getFragmentManager().beginTransaction().remove(myFragment).commit();
        } else if (!mAdapter.resetSelected()) {
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

        mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList, txtSharedMedia.getText().toString(), complete, mediaLayout, roomId);
        final GridLayoutManager gLayoutManager = new GridLayoutManager(ActivityShearedMedia.this, spanItemCount);

        gLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (mList.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.TEXT.toString()))
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

        mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList, txtSharedMedia.getText().toString(), complete, mediaLayout, roomId);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityShearedMedia.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    private void showLink() {
        txtSharedMedia.setText(R.string.shared_links);
    }

    private void showMusic() {
        txtSharedMedia.setText(R.string.shared_music);
        fillListMusic();

        mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList, txtSharedMedia.getText().toString(), complete, mediaLayout, roomId);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityShearedMedia.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


    }

    //********************************************************************************************


    private void fillListImage() {

        mList = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", roomId).findAllSorted("id");

        String firstItmeTime = "";
        String secendItemTime = "";
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy/MM/dd");
        String type = "";

        for (RealmChatHistory chatHistory : chatHistories) {
            try {
                type = chatHistory.getRoomMessage().getMessageType();
            } catch (NullPointerException e) {
            }
            if (type.equals(ProtoGlobal.RoomMessageType.VIDEO.toString()) || type.equals(ProtoGlobal.RoomMessageType.VIDEO_TEXT.toString()) ||
                    type.equals(ProtoGlobal.RoomMessageType.IMAGE.toString()) || type.equals(ProtoGlobal.RoomMessageType.IMAGE_TEXT.toString())) {

                secendItemTime = month_date.format(chatHistory.getRoomMessage().getUpdateTime());

                if (secendItemTime.compareTo(firstItmeTime) > 0) {

                    RealmRoomMessage message = new RealmRoomMessage();
                    message.setMessage(secendItemTime);
                    message.setMessageType(ProtoGlobal.RoomMessageType.TEXT.toString());
                    mList.add(message);

                    firstItmeTime = secendItemTime;
                }

                mList.add(chatHistory.getRoomMessage());
            }
        }

        realm.close();

    }

    private void fillListFile() {

        mList = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", roomId).findAllSorted("id");

        String firstItmeTime = "";
        String secendItemTime = "";
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy/MM/dd");
        String type = "";

        for (RealmChatHistory chatHistory : chatHistories) {
            try {
                type = chatHistory.getRoomMessage().getMessageType();
            } catch (NullPointerException e) {
            }
            if (type.equals(ProtoGlobal.RoomMessageType.FILE.toString()) || type.equals(ProtoGlobal.RoomMessageType.FILE_TEXT.toString())) {

                secendItemTime = month_date.format(chatHistory.getRoomMessage().getUpdateTime());

                if (secendItemTime.compareTo(firstItmeTime) > 0) {

                    RealmRoomMessage message = new RealmRoomMessage();
                    message.setMessage(secendItemTime);
                    message.setMessageType(ProtoGlobal.RoomMessageType.TEXT.toString());
                    mList.add(message);

                    firstItmeTime = secendItemTime;
                }

                mList.add(chatHistory.getRoomMessage());
            }
        }

        realm.close();

    }

    private void fillListMusic() {

        mList = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", roomId).findAllSorted("id");

        String firstItmeTime = "";
        String secendItemTime = "";
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy/MM/dd");
        String type = "";

        for (RealmChatHistory chatHistory : chatHistories) {
            try {
                type = chatHistory.getRoomMessage().getMessageType();
            } catch (NullPointerException e) {
            }
            if (type.equals(ProtoGlobal.RoomMessageType.AUDIO.toString()) || type.equals(ProtoGlobal.RoomMessageType.AUDIO_TEXT.toString()) ||
                    type.equals(ProtoGlobal.RoomMessageType.VOICE.toString())) {

                secendItemTime = month_date.format(chatHistory.getRoomMessage().getUpdateTime());

                if (secendItemTime.compareTo(firstItmeTime) > 0) {

                    RealmRoomMessage message = new RealmRoomMessage();
                    message.setMessage(secendItemTime);
                    message.setMessageType(ProtoGlobal.RoomMessageType.TEXT.toString());
                    mList.add(message);

                    firstItmeTime = secendItemTime;
                }

                mList.add(chatHistory.getRoomMessage());
            }
        }

        realm.close();

    }

}
