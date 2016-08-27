package com.iGap.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterChat;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructChatInfo;

import java.util.ArrayList;

/**
 * Created by android3 on 8/5/2016.
 */
public class ActivityChat extends ActivityEnhanced {

    private EditText edtChat;
    private Button btnSend;
    private Button btnAttachFile;
    private Button btnMic;

    private Button btnCloseAppBarSelected;
    private Button btnReplaySelected;
    private Button btnCopySelected;
    private Button btnForwardSelected;
    private Button btnDeleteSelected;
    private TextView txtNumberOfSelected;
    private LinearLayout ll_AppBarSelected;

    private TextView txtName;
    private TextView txtLastSeen;
    private TextView txt_mute;
    private ImageView imvUserPicture;
    private RecyclerView recyclerView;

    AdapterChat mAdapter;
    private MyType.ChatType chatType;
    private String contactId;
    private boolean isMute = false;
    private MyType.OwnerShip ownerShip;

    private Button btnUp;
    private Button btnDown;
    private TextView txtChannelMute;

    private AppBarLayout appBarLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();  // get chat type and contact id and  finish activity if value equal null
        if (bundle != null) {
            chatType = (MyType.ChatType) bundle.getSerializable("ChatType");
            contactId = bundle.getString("ContactID");
            isMute = bundle.getBoolean("IsMute");
            ownerShip = (MyType.OwnerShip) bundle.getSerializable("OwnerShip");
        }
        if (chatType == null || contactId == null || ownerShip == null) {
            finish();
        }


        setContentView(R.layout.activity_chat);

        initComponent();
        initAppbarSelected();

        if (chatType == MyType.ChatType.channel && ownerShip == MyType.OwnerShip.member)
            initLayotChannelFooter();

    }

    private void initComponent() {

        appBarLayout = (AppBarLayout) findViewById(R.id.chl_appbar_main);

        Button btnBack = (Button) findViewById(R.id.chl_btn_back);
        btnBack.setTypeface(G.fontawesome);

        txtName = (TextView) findViewById(R.id.chl_txt_name);
        txtLastSeen = (TextView) findViewById(R.id.chl_txt_last_seen);
        txt_mute = (TextView) findViewById(R.id.chl_txt_mute);
        txt_mute.setTypeface(G.fontawesome);

        if (isMute) {
            txt_mute.setVisibility(View.VISIBLE);
        }


        imvUserPicture = (ImageView) findViewById(R.id.chl_imv_user_picture);

        Button btnMenu = (Button) findViewById(R.id.chl_btn_menu);
        btnMenu.setTypeface(G.fontawesome);

        Button btnSmile = (Button) findViewById(R.id.chl_btn_smile);
        btnSmile.setTypeface(G.fontawesome);

        edtChat = (EditText) findViewById(R.id.chl_edt_chat);


        btnSend = (Button) findViewById(R.id.chl_btn_send);
        btnSend.setTypeface(G.fontawesome);

        btnAttachFile = (Button) findViewById(R.id.chl_btn_attach);
        btnAttachFile.setTypeface(G.fontawesome);

        btnMic = (Button) findViewById(R.id.chl_btn_mic);
        btnMic.setTypeface(G.fontawesome);


        OnComplete complete = new OnComplete() {
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


        recyclerView = (RecyclerView) findViewById(R.id.chl_recycler_view_chat);
        mAdapter = new AdapterChat(ActivityChat.this, chatType, getChatList(), complete);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ActivityChat.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        imvUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", " imvUserPicture ");
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnMenu  ");
            }
        });

        btnSmile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", " btnSmile ");
            }
        });

        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {

                if (text.length() > 0) {
                    if (btnSend.getVisibility() == View.GONE) {
                        btnSend.setVisibility(View.VISIBLE);
                        btnMic.setVisibility(View.GONE);
                        btnAttachFile.setVisibility(View.GONE);
                    }
                } else {
                    btnSend.setVisibility(View.GONE);
                    btnMic.setVisibility(View.VISIBLE);
                    btnAttachFile.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", " btnSend ");
            }
        });

        btnAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", " btnAttachFile ");
            }
        });

        btnMic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.e("ddd", " btnMic  ");
                return false;
            }
        });

    }

    private void initAppbarSelected() {

        btnCloseAppBarSelected = (Button) findViewById(R.id.chl_btn_close_layout);
        btnCloseAppBarSelected.setTypeface(G.fontawesome);
        btnCloseAppBarSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.resetSelected();
            }
        });

        btnReplaySelected = (Button) findViewById(R.id.chl_btn_replay_selected);
        btnReplaySelected.setTypeface(G.fontawesome);
        btnReplaySelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnReplaySelected");
            }
        });

        btnCopySelected = (Button) findViewById(R.id.chl_btn_copy_selected);
        btnCopySelected.setTypeface(G.fontawesome);
        btnCopySelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnCopySelected");
            }
        });


        btnForwardSelected = (Button) findViewById(R.id.chl_btn_forward_selected);
        btnForwardSelected.setTypeface(G.fontawesome);
        btnForwardSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnForwardSelected");
            }
        });

        btnDeleteSelected = (Button) findViewById(R.id.chl_btn_delete_selected);
        btnDeleteSelected.setTypeface(G.fontawesome);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnDeleteSelected");
            }
        });

        txtNumberOfSelected = (TextView) findViewById(R.id.chl_txt_number_of_selected);
        txtNumberOfSelected.setTypeface(G.fontawesome);


        ll_AppBarSelected = (LinearLayout) findViewById(R.id.chl_ll_appbar_selelected);

    }

    private void initLayotChannelFooter() {


        LinearLayout layoutAttach = (LinearLayout) findViewById(R.id.chl_ll_attach);
        LinearLayout layoutChannelFooter = (LinearLayout) findViewById(R.id.chl_ll_channel_footer);

        layoutAttach.setVisibility(View.GONE);
        layoutChannelFooter.setVisibility(View.VISIBLE);

        btnUp = (Button) findViewById(R.id.chl_btn_up);
        btnUp.setTypeface(G.fontawesome);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "up click");
                int position = recyclerView.getAdapter().getItemCount();
                if (position > 0)
                    recyclerView.scrollToPosition(0);

                appBarLayout.setExpanded(true, true);
            }
        });


        btnDown = (Button) findViewById(R.id.chl_btn_down);
        btnDown.setTypeface(G.fontawesome);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnDown");
                int position = recyclerView.getAdapter().getItemCount();
                if (position > 0)
                    recyclerView.scrollToPosition(position - 1);
            }
        });


        txtChannelMute = (TextView) findViewById(R.id.chl_txt_mute_channel);
        txtChannelMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;
                if (isMute) {
                    txtChannelMute.setText("UnMute");
                    txt_mute.setVisibility(View.VISIBLE);

                } else {
                    txtChannelMute.setText("Mute");
                    txt_mute.setVisibility(View.GONE);
                }
            }
        });

        if (isMute) {
            txtChannelMute.setText("UnMute");
        } else {
            txtChannelMute.setText("Mute");
        }

    }

    private void callBack(boolean result, int whatAction, String number) {

        switch (whatAction) {

            case 1://for show or gone layout appBar selected
                if (result) {
                    ll_AppBarSelected.setVisibility(View.VISIBLE);
                    txtNumberOfSelected.setText(number);

                    int x = Integer.parseInt(number);
                    if (x > 1)
                        btnReplaySelected.setVisibility(View.INVISIBLE);
                    else
                        btnReplaySelected.setVisibility(View.VISIBLE);
                } else {
                    ll_AppBarSelected.setVisibility(View.GONE);
                    appBarLayout.setExpanded(true, true);
                }
                break;

        }


    }


    @Override
    public void onBackPressed() {

        if (!mAdapter.resetSelected()) {
            super.onBackPressed();
        }
    }

    private ArrayList<StructChatInfo> getChatList() {

        ArrayList<StructChatInfo> list = new ArrayList<>();

        StructChatInfo c = new StructChatInfo();
        c.messageType = MyType.MessageType.image;
        c.sendType = MyType.SendType.send;
        c.senderAvatar = R.mipmap.a + "";
        c.filePath = R.mipmap.a + "";
        c.channelLink = "@igap";
        c.seen = "122k";
        list.add(c);


        StructChatInfo c9 = new StructChatInfo();
        c9.messageType = MyType.MessageType.files;
        c9.forwardMessageFrom = "ali";
        c9.replayFrom = "ali";
        c9.replayMessage = "where are you djkdj kjf kdj";
        c9.sendType = MyType.SendType.send;
        c9.senderAvatar = R.mipmap.a + "";
        list.add(c9);

        StructChatInfo c10 = new StructChatInfo();
        c10.messageType = MyType.MessageType.files;
        c10.forwardMessageFrom = "hasan";
        c10.replayFrom = "mehdi";
        c10.replayMessage = "i am fine";
        c10.replayPicturePath = " fd";
        c10.sendType = MyType.SendType.recvive;
        c10.senderAvatar = R.mipmap.a + "";
        list.add(c10);

        StructChatInfo c12 = new StructChatInfo();
        c12.messageType = MyType.MessageType.gif;
        c12.sendType = MyType.SendType.send;
        list.add(c12);

        StructChatInfo c16 = new StructChatInfo();
        c16.sendType = MyType.SendType.timeLayout;
        list.add(c16);

        StructChatInfo c13 = new StructChatInfo();
        c13.messageType = MyType.MessageType.audio;
        c13.sendType = MyType.SendType.send;
        list.add(c13);

        StructChatInfo c14 = new StructChatInfo();
        c14.messageType = MyType.MessageType.audio;
        c14.sendType = MyType.SendType.recvive;
        c14.messag = "بهترین موسیقی منتخب" +
                "\n" + " how are you";
        c14.senderAvatar = R.mipmap.a + "";
        list.add(c14);

        StructChatInfo c15 = new StructChatInfo();
        c15.messageType = MyType.MessageType.sticker;
        c15.sendType = MyType.SendType.recvive;
        c15.filePath = R.mipmap.sd + "";
        list.add(c15);

        StructChatInfo c11 = new StructChatInfo();
        c11.messageType = MyType.MessageType.files;
        c11.sendType = MyType.SendType.recvive;
        c11.senderAvatar = R.mipmap.a + "";
        list.add(c11);

        StructChatInfo c1 = new StructChatInfo();
        c1.messageType = MyType.MessageType.message;
        c1.messag = "how";

        c1.sendType = MyType.SendType.send;
        c1.senderAvatar = R.mipmap.b + "";
        list.add(c1);

        StructChatInfo c2 = new StructChatInfo();
        c2.messageType = MyType.MessageType.message;
        c2.senderAvatar = R.mipmap.c + "";
        c2.messag = "i am fine";
        c2.forwardMessageFrom = "Android cade";
        c2.sendType = MyType.SendType.recvive;
        list.add(c2);

        StructChatInfo c3 = new StructChatInfo();
        c3.messageType = MyType.MessageType.message;
        c3.messag = "where are you  going";
        c3.sendType = MyType.SendType.send;
        list.add(c3);

        StructChatInfo c4 = new StructChatInfo();
        c4.messageType = MyType.MessageType.image;
        c4.forwardMessageFrom = "ali";
        c4.messag = "the good picture for all the word";
        c4.sendType = MyType.SendType.recvive;
        c4.senderAvatar = R.mipmap.d + "";
        c4.filePath = R.mipmap.d + "";
        list.add(c4);

        StructChatInfo c5 = new StructChatInfo();
        c5.messageType = MyType.MessageType.image;
        c5.forwardMessageFrom = "ali";
        c5.sendType = MyType.SendType.send;
        c5.senderAvatar = R.mipmap.e + "";
        c5.filePath = R.mipmap.e + "";
        list.add(c5);

        StructChatInfo c6 = new StructChatInfo();
        c6.messageType = MyType.MessageType.image;
        c6.sendType = MyType.SendType.recvive;
        c6.senderAvatar = R.mipmap.f + "";
        c6.filePath = R.mipmap.f + "";
        list.add(c6);

        StructChatInfo c7 = new StructChatInfo();
        c7.messageType = MyType.MessageType.image;
        c7.sendType = MyType.SendType.recvive;
        c7.senderAvatar = R.mipmap.g + "";
        c7.filePath = R.mipmap.g + "";
        list.add(c7);

        StructChatInfo c8 = new StructChatInfo();
        c8.messageType = MyType.MessageType.image;
        c8.sendType = MyType.SendType.send;
        c8.filePath = R.mipmap.h + "";
        list.add(c8);


        for (int i = 0; i < 100; i++) {

            list.add(c8);
        }

        return list;
    }

}
