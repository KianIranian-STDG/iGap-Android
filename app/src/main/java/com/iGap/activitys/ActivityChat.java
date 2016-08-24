package com.iGap.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterChat;
import com.iGap.module.MyType;
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
    private TextView txtName;
    private TextView txtLastSeen;
    private TextView txt_mute;
    private ImageView imvUserPicture;
    private RecyclerView recyclerView;

    AdapterChat mAdapter;
    private MyType.ChatType chatType;
    private String contactId;
    private boolean isMute = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();  // get chat type and contact id and  finish activity if value equal null
        if (bundle != null) {
            chatType = (MyType.ChatType) bundle.getSerializable("chattype");
            contactId = bundle.getString("contactid");
            isMute = bundle.getBoolean("ismute");
        }
        if (chatType == null || contactId == null) {
            finish();
        }


        setContentView(R.layout.activity_chat);

        initComponent();
    }

    private void initComponent() {

        Button btnBack = (Button) findViewById(R.id.chl_btn_back);
        btnBack.setTypeface(G.fontawesome);

        txtName = (TextView) findViewById(R.id.chl_txt_name);
        txtLastSeen = (TextView) findViewById(R.id.chl_txt_last_seen);

        if (isMute) {
            txt_mute = (TextView) findViewById(R.id.chl_txt_mute);
            txt_mute.setTypeface(G.fontawesome);
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


        recyclerView = (RecyclerView) findViewById(R.id.chl_recycler_view_chat);
        mAdapter = new AdapterChat(ActivityChat.this, chatType, getChatList());
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
        c14.messag = "the good media";
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
