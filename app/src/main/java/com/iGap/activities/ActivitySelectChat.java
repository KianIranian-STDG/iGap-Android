package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.RoomItem;
import com.iGap.module.StructChatInfo;
import com.iGap.module.StructMessageInfo;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.enums.RoomType;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.ArrayList;

import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivitySelectChat extends ActivityEnhanced {

    private RecyclerView mRecyclerView;
    private FastItemAdapter<RoomItem> mAdapter;
    public static final String ARG_FORWARD_MESSAGE = "arg_forward_msg";
    private ArrayList<StructMessageInfo> mForwardMessages;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mForwardMessages = getIntent().getExtras().getParcelableArrayList(ARG_FORWARD_MESSAGE);

        initRecycleView();
        initComponent();
    }
    private void initComponent() {
        Button btnMenu = (Button) findViewById(R.id.cl_btn_menu);
        btnMenu.setTypeface(G.fontawesome);

        Button btnSearch = (Button) findViewById(R.id.amr_btn_search);
        btnSearch.setTypeface(G.flaticon);

        TextView txtIgap = (TextView) findViewById(R.id.cl_txt_igap);
        txtIgap.setTypeface(G.neuroplp);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
    private void initRecycleView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.cl_recycler_view_contact);
        mAdapter = new FastItemAdapter<>();
        mAdapter.withOnClickListener(new FastAdapter.OnClickListener<RoomItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RoomItem> adapter, RoomItem item, int position) {
                Intent intent = new Intent(ActivitySelectChat.this, ActivityChat.class);
                intent.putExtra("RoomId", item.mInfo.chatId);
                intent.putParcelableArrayListExtra(ARG_FORWARD_MESSAGE, mForwardMessages);
                startActivity(intent);
                return false;
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ActivitySelectChat.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        loadLocalChatList();
    }

    private void loadLocalChatList() {
        Realm realm = Realm.getDefaultInstance();
        for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
            final RoomItem roomItem = new RoomItem();
            StructChatInfo info = new StructChatInfo();
            info.unreadMessagesCount = realmRoom.getUnreadCount();
            info.chatId = realmRoom.getId();
            info.chatTitle = realmRoom.getTitle();
            info.initials = realmRoom.getInitials();
            switch (realmRoom.getType()) {
                case CHAT:
                    info.chatType = RoomType.CHAT;
                    info.memberCount = "1";
                    break;
                case CHANNEL:
                    info.chatType = RoomType.CHANNEL;
                    info.memberCount = realmRoom.getChannelRoom().getParticipantsCountLabel();
                    break;
                case GROUP:
                    info.chatType = RoomType.GROUP;
                    info.memberCount = realmRoom.getGroupRoom().getParticipantsCountLabel();
                    break;
            }
            info.color = realmRoom.getColor();
            RealmRoomMessage lastMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, realmRoom.getLastMessageId()).findFirst();
            if (lastMessage != null) {
                info.lastMessageTime = lastMessage.getUpdateTime();
                info.lastMessageId = realmRoom.getLastMessageId();
                info.lastMessageStatus = lastMessage.getStatus();
                info.lastMessageSenderIsMe = lastMessage.isSenderMe();
            }
            info.muteNotification = realmRoom.getMute(); // FIXME

            roomItem.setInfo(info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(roomItem);
                }
            });
        }
        realm.close();
    }
}
