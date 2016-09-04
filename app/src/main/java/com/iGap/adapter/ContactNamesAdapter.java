package com.iGap.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivityChat;
import com.iGap.interface_package.OnChatGetRoom;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmRoom;
import com.iGap.request.RequestChatGetRoom;

import java.util.ArrayList;

public class ContactNamesAdapter extends RecyclerView.Adapter<CountryViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0x01;
    private static final int VIEW_TYPE_CONTENT = 0x00;
    private ArrayList<LineItem> mItems = new ArrayList<>();

    private final Context mContext;

    public ContactNamesAdapter(Context context, ArrayList<LineItem> _mItems) {
        mContext = context;
        this.mItems = _mItems;
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_item, parent, false); //TODO [Saeed Mozaffari] [2016-09-03 11:48 AM] - header naming is wrong
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_line_item, parent, false);
        }
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        final LineItem item = mItems.get(position);
        final View itemView = holder.itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatGetRoom(item.peerId);
            }
        });

        try {
            holder.bindItem(item.text, item.Status);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public static class LineItem {

        public long peerId;
        public int sectionManager;
        public int sectionFirstPosition;
        public boolean isHeader;
        public String text;
        public String Status;

        public LineItem(long peerId, String text, String status, boolean isHeader, int sectionManager, int sectionFirstPosition) {
            this.peerId = peerId;
            this.isHeader = isHeader;
            this.text = text;
            this.Status = status;
            this.sectionManager = sectionManager;
            this.sectionFirstPosition = sectionFirstPosition;
        }
    }

    private void chatGetRoom(final long peerId) {

        final RealmRoom realmRoom = G.realm.where(RealmRoom.class).equalTo("chat_room.peer_id", peerId).findFirst();

        if (realmRoom != null) {
            Log.i("XXX", "Room Exist");
            Intent intent = new Intent(G.context, ActivityChat.class);
            intent.putExtra("ChatID", realmRoom.getId());
            intent.putExtra("ChatType", realmRoom.getType().toString());
            intent.putExtra("NewChatRoom", false);
            //intent.putExtra("IsMute", ); //TODO [Saeed Mozaffari] [2016-09-03 11:12 AM] - set IsMute in RealmRoom
            intent.putExtra("LastSeen", G.realm.where(RealmContacts.class).equalTo("id", peerId).findFirst().getLast_seen());
            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            G.context.startActivity(intent);

        } else {
            Log.i("XXX", "Create new Room");
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final long roomId) {
                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(G.context, ActivityChat.class);
                            intent.putExtra("NewChatRoom", true);
                            intent.putExtra("ChatType", ProtoGlobal.Room.Type.CHAT.toString());
                            intent.putExtra("LastSeen", G.realm.where(RealmContacts.class).equalTo("id", peerId).findFirst().getLast_seen());
                            intent.putExtra("RoomId", roomId);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            G.context.startActivity(intent);
                        }
                    });
                }
            };

            new RequestChatGetRoom().chatGetRoom(peerId);
        }
    }
}
