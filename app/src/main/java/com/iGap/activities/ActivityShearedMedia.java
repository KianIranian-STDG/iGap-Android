package com.iGap.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.iGap.request.RequestClientSearchRoomHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<StructSharedMedia> mList;

    private LinearLayout mediaLayout;
    private MusicPlayer musicPlayer;

    private AppBarLayout appBarLayout;

    boolean isSendRequestForLoading = false;
    boolean isThereAnyMoreItemToLoad = false;

    ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter mFilter;


    private int offset = 0;

    public class StructSharedMedia {

        public ProtoGlobal.RoomMessage item = null;

        public String time = "";

        public boolean isItemTime = false;

    }


    public enum SharedMediaType {

        image(1),
        video(2),
        audio(3),
        voice(4),
        gif(5),
        file(6),
        link(7);

        private int value;

        SharedMediaType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                Log.e("dddd", mAdapter.getItemCount() + "   " + ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition());

                if (isThereAnyMoreItemToLoad) {
                    if (!isSendRequestForLoading) {

                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                        if (mAdapter.getItemCount() <= lastVisiblePosition + 5) {

                            loadMoreData();
                        }
                    }
                }


            }
        });

        fillListImage();
        initAppbarSelected();
    }

    private void initAppbarSelected() {

        Button btnCloseAppBarSelected = (Button) findViewById(R.id.asm_btn_close_layout);

        RippleView rippleCloseAppBarSelected = (RippleView) findViewById(R.id.asm_ripple_close_layout);
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

            }
        });

        Button btnDeleteSelected = (Button) findViewById(R.id.asm_btn_delete_selected);
        RippleView rippleDeleteSelected = (RippleView) findViewById(R.id.asm_ripple_close_layout);
        rippleDeleteSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

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
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        offset = 0;

                        switch (which) {
                            case 0:
                                fillListImage();
                                break;
                            case 1:
                                fillListVideo();
                                break;
                            case 2:
                                fillListAudio();
                                break;
                            case 3:
                                fillListVoice();
                                break;
                            case 4:
                                fillListGif();
                                break;
                            case 5:
                                fillListFile();
                                break;
                            case 6:
                                fillListLink();
                                break;
                        }
                    }
                })
                .show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp220);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        dialog.getWindow().setAttributes(layoutParams);
    }


    //********************************************************************************************


    private void initLayoutRecycleviewForImage() {

        final GridLayoutManager gLayoutManager = new GridLayoutManager(ActivityShearedMedia.this, spanItemCount);

        gLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mList.get(position).isItemTime) {
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

    private void fillListImage() {

        txtSharedMedia.setText(R.string.shared_image);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.IMAGE;

        getDataFromServer(new OnFillList() {
            @Override
            public void getList(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {


                mList = addTimeToList(resultList);
                mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList, SharedMediaType.image, complete, musicPlayer, roomId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initLayoutRecycleviewForImage();
                    }
                });


                offset = mList.size();

            }
        }, mFilter);

    }

    private void fillListVideo() {


        txtSharedMedia.setText(R.string.shared_video);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VIDEO;
        getDataFromServer(new OnFillList() {
            @Override
            public void getList(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {


                mList = addTimeToList(resultList);
                mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList, SharedMediaType.video, complete, musicPlayer, roomId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initLayoutRecycleviewForImage();
                    }
                });

                offset = mList.size();

            }
        }, mFilter);

    }

    private void fillListAudio() {

        txtSharedMedia.setText(R.string.shared_audio);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO;
        getDataFromServer(new OnFillList() {
            @Override
            public void getList(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {


                mList = addTimeToList(resultList);

                mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList, SharedMediaType.audio, complete, musicPlayer, roomId);
                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityShearedMedia.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                    }
                });


                offset = mList.size();

            }
        }, mFilter);
    }

    private void fillListVoice() {
        txtSharedMedia.setText(R.string.shared_voice);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VOICE;

        getDataFromServer(new OnFillList() {
            @Override
            public void getList(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {


                mList = addTimeToList(resultList);
                mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList, SharedMediaType.voice, complete, musicPlayer, roomId);

                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityShearedMedia.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                    }
                });


                offset = mList.size();

            }
        }, mFilter);

    }

    private void fillListGif() {
        txtSharedMedia.setText(R.string.shared_gif);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.GIF;

        getDataFromServer(new OnFillList() {
            @Override
            public void getList(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {


                mList = addTimeToList(resultList);

                mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList, SharedMediaType.gif, complete, musicPlayer, roomId);
                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityShearedMedia.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                    }
                });


                offset = mList.size();

            }
        }, mFilter);

    }

    private void fillListFile() {
        txtSharedMedia.setText(R.string.shared_file);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.FILE;

        getDataFromServer(new OnFillList() {
            @Override
            public void getList(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                mList = addTimeToList(resultList);
                mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList, SharedMediaType.file, complete, musicPlayer, roomId);

                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityShearedMedia.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                    }
                });


                offset = mList.size();


            }
        }, mFilter);
    }

    private void fillListLink() {

        txtSharedMedia.setText(R.string.shared_links);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.URL;

        getDataFromServer(new OnFillList() {
            @Override
            public void getList(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                mList = addTimeToList(resultList);
                mAdapter = new AdapterShearedMedia(ActivityShearedMedia.this, mList, SharedMediaType.link, complete, musicPlayer, roomId);

                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityShearedMedia.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                    }
                });

                offset = mList.size();

            }
        }, mFilter);


    }


    //********************************************************************************************


    private void getDataFromServer(final OnFillList onFillList, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter) {

        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {
                isSendRequestForLoading = false;
                for (ProtoGlobal.RoomMessage message : resultList) {

                    Log.e("ddd", message + "      eeee");

                }

                if (totalCount > 0) {
                    onFillList.getList(totalCount, notDeletedCount, resultList);
                } else {
                    Toast.makeText(ActivityShearedMedia.this, "there is no Sheared media", Toast.LENGTH_LONG).show();
                }

                if (totalCount > mList.size()) {
                    isThereAnyMoreItemToLoad = true;
                } else {
                    isThereAnyMoreItemToLoad = false;
                }


                Log.e("dddd", "isThereAnyMoreItemToLoad   " + isThereAnyMoreItemToLoad);
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                isSendRequestForLoading = false;


                Log.e("ddd", "erore  onClientSearchRoomHistory    majorCode  " + majorCode + "   " + minorCode);

                switch (majorCode) {

                    case 617:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityShearedMedia.this, "there is no shared madia to show", Toast.LENGTH_SHORT).show();
                            }
                        });

                        break;
                    case 620:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityShearedMedia.this, "there is no shared madia to show", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }


            }

            @Override
            public void onTimeOut() {
                isSendRequestForLoading = false;
                Log.e("ddd", "timeOut  onClientSearchRoomHistory");
            }
        };

        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offset, filter);
        isSendRequestForLoading = true;


    }

    private void loadMoreData() {

        getDataFromServer(new OnFillList() {
            @Override
            public void getList(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                mList.addAll(addTimeToList(resultList));
                mAdapter.notifyDataSetChanged();


                offset = mList.size();

            }
        }, mFilter);


    }

    //********************************************************************************************

    public interface OnFillList {

        void getList(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList);

    }

    private ArrayList<StructSharedMedia> addTimeToList(List<ProtoGlobal.RoomMessage> uploadList) {

        ArrayList<StructSharedMedia> result = new ArrayList<>();

        String firstItmeTime = "";
        String secendItemTime = "";
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy/MM/dd");

        for (int i = uploadList.size(); i > 0; i--) {

            ProtoGlobal.RoomMessage message = uploadList.get(i - 1);

            secendItemTime = month_date.format(message.getUpdateTime() * DateUtils.SECOND_IN_MILLIS);

            Log.e("ddd", secendItemTime + "   " + firstItmeTime);

            if (secendItemTime.compareTo(firstItmeTime) > 0 || secendItemTime.compareTo(firstItmeTime) < 0) {

                StructSharedMedia timeItem = new StructSharedMedia();
                timeItem.time = secendItemTime;
                timeItem.isItemTime = true;

                result.add(timeItem);

                firstItmeTime = secendItemTime;
            }

            StructSharedMedia _item = new StructSharedMedia();
            _item.item = message;

            result.add(_item);
        }


        return result;
    }


}
