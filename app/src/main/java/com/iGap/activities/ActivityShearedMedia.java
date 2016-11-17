package com.iGap.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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
import com.iGap.interfaces.OnClientSearchRoomHistory;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.MusicPlayer;
import com.iGap.module.OnComplete;
import com.iGap.proto.ProtoClientSearchRoomHistory;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.request.RequestClientSearchRoomHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
    private MusicPlayer musicPlayer;

    private AppBarLayout appBarLayout;

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicPlayer.mp != null) {
            MusicPlayer.initLayoutTripMusic(mediaLayout);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheared_media);

        mediaLayout = (LinearLayout) findViewById(R.id.asm_ll_music_layout);
        musicPlayer = new MusicPlayer(mediaLayout);

        roomId = getIntent().getExtras().getLong("RoomID");

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

        appBarLayout = (AppBarLayout) findViewById(R.id.asm_appbar_shared_media);

        FragmentShowImage.appBarLayout = appBarLayout;

        MaterialDesignTextView btnBack = (MaterialDesignTextView) findViewById(R.id.asm_btn_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.asm_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        MaterialDesignTextView btnMenu = (MaterialDesignTextView) findViewById(R.id.asm_btn_menu);
        RippleView rippleMenu = (RippleView) findViewById(R.id.asm_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popUpMenuSharedMedai();
            }
        });

        txtSharedMedia = (TextView) findViewById(R.id.asm_txt_sheared_media);

        complete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                int whatAction = 0;
                String number = "0";

                if (messageOne != null) {
                    if (messageOne.length() > 0) whatAction = Integer.parseInt(messageOne);
                }

                if (MessageTow != null) if (MessageTow.length() > 0) number = MessageTow;

                callBack(result, whatAction, number);
            }
        };

        recyclerView = (RecyclerView) findViewById(R.id.asm_recycler_view_sheared_media);

        showMedia();

        initAppbarSelected();
    }

    private void initAppbarSelected() {

        Button btnCloseAppBarSelected = (Button) findViewById(R.id.asm_btn_close_layout);

        RippleView rippleCloseAppBarSelected =
                (RippleView) findViewById(R.id.asm_ripple_close_layout);
        rippleCloseAppBarSelected.setOnClickListener(new View.OnClickListener() {
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
        RippleView rippleDeleteSelected = (RippleView) findViewById(R.id.asm_ripple_close_layout);
        rippleDeleteSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
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

        MaterialDialog dialog = new MaterialDialog.Builder(this).items(R.array.pop_up_shared_media)
                .contentColor(Color.BLACK)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which,
                                            CharSequence text) {

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
                })
                .show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp180);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        dialog.getWindow().setAttributes(layoutParams);
    }

    private void showMedia() {
        fillListImage();

        txtSharedMedia.setText(getString(R.string.shared_media));

        mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList,
                txtSharedMedia.getText().toString(), complete, musicPlayer, roomId);
        final GridLayoutManager gLayoutManager =
                new GridLayoutManager(ActivityShearedMedia.this, spanItemCount);

        gLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (mList.get(position)
                        .getMessageType()
                        .equals(ProtoGlobal.RoomMessageType.TEXT)) {
                    return spanItemCount;
                } else {
                    return 1;
                }
            }
        });

        recyclerView.setLayoutManager(gLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        int viewWidth = recyclerView.getMeasuredWidth();
                        float cardViewWidth = getResources().getDimension(R.dimen.dp120);
                        int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);

                        if (newSpanCount < 3) newSpanCount = 3;

                        spanItemCount = newSpanCount;
                        gLayoutManager.setSpanCount(newSpanCount);
                        gLayoutManager.requestLayout();
                    }
                });
    }

    private void showFile() {

        txtSharedMedia.setText(getString(R.string.shared_files));

        fillListFile();

        mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList,
                txtSharedMedia.getText().toString(), complete, musicPlayer, roomId);
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

        mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList,
                txtSharedMedia.getText().toString(), complete, musicPlayer, roomId);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityShearedMedia.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    //********************************************************************************************

    private void fillListImage() {

        getImageListFromServer();

        mList = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class)
                .equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
                .findAllSorted(RealmRoomMessageFields.MESSAGE_ID);

        String firstItmeTime = "";
        String secendItemTime = "";
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy/MM/dd");
        ProtoGlobal.RoomMessageType type = ProtoGlobal.RoomMessageType.UNRECOGNIZED;

        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
            try {
                type = realmRoomMessage.getMessageType();
            } catch (NullPointerException e) {
            }
            if (type.equals(ProtoGlobal.RoomMessageType.VIDEO) || type.equals(
                    ProtoGlobal.RoomMessageType.VIDEO_TEXT) ||
                    type.equals(ProtoGlobal.RoomMessageType.IMAGE) || type.equals(
                    ProtoGlobal.RoomMessageType.IMAGE_TEXT)) {

                secendItemTime = month_date.format(realmRoomMessage.getUpdateTime());

                if (secendItemTime.compareTo(firstItmeTime) > 0) {

                    RealmRoomMessage message = new RealmRoomMessage();
                    message.setMessage(secendItemTime);
                    message.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                    mList.add(message);

                    firstItmeTime = secendItemTime;
                }

                mList.add(realmRoomMessage);
            }
        }

        realm.close();
    }

    private void fillListFile() {

        mList = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class)
                .equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
                .findAllSorted(RealmRoomMessageFields.MESSAGE_ID);

        String firstItmeTime = "";
        String secendItemTime = "";
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy/MM/dd");
        ProtoGlobal.RoomMessageType type = ProtoGlobal.RoomMessageType.UNRECOGNIZED;

        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
            try {
                type = realmRoomMessage.getMessageType();
            } catch (NullPointerException e) {
            }
            if (type.equals(ProtoGlobal.RoomMessageType.FILE.toString()) || type.equals(
                    ProtoGlobal.RoomMessageType.FILE_TEXT.toString())) {

                secendItemTime = month_date.format(realmRoomMessage.getUpdateTime());

                if (secendItemTime.compareTo(firstItmeTime) > 0) {

                    RealmRoomMessage message = new RealmRoomMessage();
                    message.setMessage(secendItemTime);
                    message.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                    mList.add(message);

                    firstItmeTime = secendItemTime;
                }

                mList.add(realmRoomMessage);
            }
        }

        realm.close();
    }

    private void fillListMusic() {

        mList = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class)
                .equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
                .findAllSorted(RealmRoomMessageFields.MESSAGE_ID);

        String firstItmeTime = "";
        String secendItemTime = "";
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy/MM/dd");
        ProtoGlobal.RoomMessageType type = ProtoGlobal.RoomMessageType.UNRECOGNIZED;

        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
            try {
                type = realmRoomMessage.getMessageType();
            } catch (NullPointerException e) {
            }
            if (type.equals(ProtoGlobal.RoomMessageType.AUDIO.toString()) || type.equals(
                    ProtoGlobal.RoomMessageType.AUDIO_TEXT.toString()) ||
                    type.equals(ProtoGlobal.RoomMessageType.VOICE)) {

                secendItemTime = month_date.format(realmRoomMessage.getUpdateTime());

                if (secendItemTime.compareTo(firstItmeTime) > 0) {

                    RealmRoomMessage message = new RealmRoomMessage();
                    message.setMessage(secendItemTime);
                    message.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                    mList.add(message);

                    firstItmeTime = secendItemTime;
                }

                mList.add(realmRoomMessage);
            }
        }

        realm.close();
    }


    //********************************************************************************************

    private int offsetIMAGE = 0;
    private int offsetVIDEO = 0;
    private int offsetAUDIO = 0;
    private int offsetVOICE = 0;
    private int offsetGIF = 0;
    private int offsetFILE = 0;
    private int offsetURL = 0;


    private void getImageListFromServer() {


        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                for (ProtoGlobal.RoomMessage message : resultList) {

                    Log.e("ddd", message + "      eeee");

                }

            }

            @Override
            public void onError(int majorCode, int minorCode) {
                Log.e("ddd", "erore  onClientSearchRoomHistory    majorCode" + majorCode + "   " + minorCode);
            }

            @Override
            public void onTimeOut() {
                Log.e("ddd", "timeOut  onClientSearchRoomHistory");
            }
        };

        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offsetIMAGE, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.IMAGE);

    }

    private void getVIDEOListFromServer() {

        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                for (ProtoGlobal.RoomMessage message : resultList) {

                    Log.e("ddd", message + "      eeee");

                }


            }

            @Override
            public void onError(int majorCode, int minorCode) {
                Log.e("ddd", "erore  onClientSearchRoomHistory    majorCode" + majorCode + "   " + minorCode);
            }

            @Override
            public void onTimeOut() {
                Log.e("ddd", "timeOut  onClientSearchRoomHistory");
            }
        };

        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offsetVIDEO, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VIDEO);

    }

    private void getAUDIOListFromServer() {


        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                for (ProtoGlobal.RoomMessage message : resultList) {

                    Log.e("ddd", message + "      eeee");

                }


            }

            @Override
            public void onError(int majorCode, int minorCode) {
                Log.e("ddd", "erore  onClientSearchRoomHistory    majorCode" + majorCode + "   " + minorCode);
            }

            @Override
            public void onTimeOut() {
                Log.e("ddd", "timeOut  onClientSearchRoomHistory");
            }
        };

        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offsetAUDIO, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO);

    }

    private void getVOICEListFromServer() {


        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                for (ProtoGlobal.RoomMessage message : resultList) {

                    Log.e("ddd", message + "      eeee");

                }


            }

            @Override
            public void onError(int majorCode, int minorCode) {
                Log.e("ddd", "erore  onClientSearchRoomHistory    majorCode" + majorCode + "   " + minorCode);
            }

            @Override
            public void onTimeOut() {
                Log.e("ddd", "timeOut  onClientSearchRoomHistory");
            }
        };

        ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter voiceFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VOICE;
        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offsetVOICE, voiceFilter);

    }

    private void getGIFListFromServer() {


        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                for (ProtoGlobal.RoomMessage message : resultList) {

                    Log.e("ddd", message + "      eeee");

                }


            }

            @Override
            public void onError(int majorCode, int minorCode) {
                Log.e("ddd", "erore  onClientSearchRoomHistory    majorCode" + majorCode + "   " + minorCode);
            }

            @Override
            public void onTimeOut() {
                Log.e("ddd", "timeOut  onClientSearchRoomHistory");
            }
        };

        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offsetGIF, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.GIF);

    }

    private void getFILEListFromServer() {


        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                for (ProtoGlobal.RoomMessage message : resultList) {

                    Log.e("ddd", message + "      eeee");

                }


            }

            @Override
            public void onError(int majorCode, int minorCode) {
                Log.e("ddd", "erore  onClientSearchRoomHistory    majorCode" + majorCode + "   " + minorCode);
            }

            @Override
            public void onTimeOut() {
                Log.e("ddd", "timeOut  onClientSearchRoomHistory");
            }
        };

        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offsetFILE, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.FILE);

    }

    private void getURLListFromServer() {


        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                for (ProtoGlobal.RoomMessage message : resultList) {

                    Log.e("ddd", message + "      eeee");

                }


            }

            @Override
            public void onError(int majorCode, int minorCode) {
                Log.e("ddd", "erore  onClientSearchRoomHistory    majorCode" + majorCode + "   " + minorCode);
            }

            @Override
            public void onTimeOut() {
                Log.e("ddd", "timeOut  onClientSearchRoomHistory");
            }
        };

        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offsetURL, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.URL);

    }


}
