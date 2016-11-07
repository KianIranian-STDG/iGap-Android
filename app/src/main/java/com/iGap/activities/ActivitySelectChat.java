package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.RoomItem;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructChatInfo;
import com.iGap.module.StructMessageAttachment;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.enums.RoomType;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.Sort;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivitySelectChat extends ActivityEnhanced {

    public static final String ARG_FORWARD_MESSAGE = "arg_forward_msg";
    private RecyclerView mRecyclerView;
    private FastItemAdapter<RoomItem> mAdapter;
    private ArrayList<Parcelable> mForwardMessages;

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
        MaterialDesignTextView btnMenu = (MaterialDesignTextView) findViewById(R.id.cl_btn_menu);

        MaterialDesignTextView btnSearch =
                (MaterialDesignTextView) findViewById(R.id.amr_btn_search);

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
            public boolean onClick(View v, IAdapter<RoomItem> adapter, RoomItem item,
                                   int position) {
                Intent intent = new Intent(ActivitySelectChat.this, ActivityChat.class);
                intent.putExtra("RoomId", item.mInfo.chatId);
                intent.putParcelableArrayListExtra(ARG_FORWARD_MESSAGE, mForwardMessages);
                startActivity(intent);
                finish();
                return false;
            }
        });
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(ActivitySelectChat.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        loadLocalChatList();
    }

    private void loadLocalChatList() {
        mAdapter.clear();

        Realm realm = Realm.getDefaultInstance();
        for (RealmRoom realmRoom : realm.where(RealmRoom.class)
                .findAllSorted(RealmRoomFields.LAST_MESSAGE_TIME, Sort.DESCENDING)) {
            final RoomItem roomItem = new RoomItem();
            StructChatInfo info = new StructChatInfo();
            info.unreadMessagesCount = realmRoom.getUnreadCount();
            info.chatId = realmRoom.getId();
            info.chatTitle = realmRoom.getTitle();
            info.initials = realmRoom.getInitials();
            info.ownerId = realmRoom.getId();
            info.readOnly = realmRoom.getReadOnly();
            switch (realmRoom.getType()) {
                case CHAT:
                    info.chatType = RoomType.CHAT;
                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                            .equalTo(RealmRegisteredInfoFields.ID, realmRoom.getChatRoom().getPeerId())
                            .findFirst();
                    info.avatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(
                            realmRegisteredInfo.getLastAvatar()) : new StructMessageAttachment();
                    info.ownerId = realmRoom.getChatRoom().getPeerId();
                    break;
                case CHANNEL:
                    info.chatType = RoomType.CHANNEL;
                    info.memberCount = realmRoom.getChannelRoom().getParticipantsCountLabel();
                    info.description = realmRoom.getChannelRoom().getDescription();
                    info.avatarCount = realmRoom.getChannelRoom().getAvatarCount();
                    info.avatar = StructMessageAttachment.convert(realmRoom.getAvatar());
                    break;
                case GROUP:
                    info.chatType = RoomType.GROUP;
                    info.memberCount = realmRoom.getGroupRoom().getParticipantsCountLabel();
                    info.description = realmRoom.getGroupRoom().getDescription();
                    info.avatarCount = realmRoom.getGroupRoom().getAvatarCount();
                    info.avatar = StructMessageAttachment.convert(realmRoom.getAvatar());
                    break;
            }
            info.color = realmRoom.getColor();
            info.lastMessageId = realmRoom.getLastMessageId();
            info.lastMessageTime = realmRoom.getLastMessageTime();
            info.lastMessageStatus = realmRoom.getLastMessageStatus();
            RealmRoomMessage lastMessage = realm.where(RealmRoomMessage.class)
                    .equalTo(RealmRoomMessageFields.MESSAGE_ID, realmRoom.getLastMessageId())
                    .findFirst();
            if (lastMessage != null) {
                info.lastMessageTime = lastMessage.getUpdateTime();
                info.lastMessageSenderIsMe = lastMessage.isSenderMe();
                info.lastMessageStatus = lastMessage.getStatus();
            }
            info.muteNotification = realmRoom.getMute(); // FIXME

            roomItem.setInfo(info);
            //roomItem.setComplete(this);
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
