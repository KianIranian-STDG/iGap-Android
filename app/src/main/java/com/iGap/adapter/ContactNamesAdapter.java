package com.iGap.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivityChat;
import com.iGap.interface_package.OnChatGetRoom;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmRoom;
import com.iGap.request.RequestChatGetRoom;

import java.util.ArrayList;

public class ContactNamesAdapter extends RecyclerView.Adapter<ContactNamesAdapter.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0x01;
    private static final int VIEW_TYPE_CONTENT = 0x00;
    private ArrayList<StructContactInfo> mItems = new ArrayList<>();

    private final Context mContext;

    public ContactNamesAdapter(Context context, ArrayList<StructContactInfo> _mItems) {
        mContext = context;
        this.mItems = _mItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StructContactInfo item = mItems.get(position);
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
                public void onChatGetRoom(long roomId) {
                    Intent intent = new Intent(G.context, ActivityChat.class);
                    intent.putExtra("NewChatRoom", true);
                    intent.putExtra("ChatType", ProtoGlobal.Room.Type.CHAT.toString());
                    intent.putExtra("LastSeen", G.realm.where(RealmContacts.class).equalTo("id", peerId).findFirst().getLast_seen());
                    intent.putExtra("RoomId", roomId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    G.context.startActivity(intent);
                }
            };

            new RequestChatGetRoom().chatGetRoom(peerId);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public TextView mStatus;
        public ImageView img;


        ViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.imageView);
            mTextView = (TextView) view.findViewById(R.id.title);
            mStatus = (TextView) view.findViewById(R.id.subtitle);

        }

        public void bindItem(String text, String text2) {
            try {
                mTextView.setText(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mStatus.setText(text2);
            } catch (Exception e) {
                e.printStackTrace();
            }

      /*  try {
            if(sli.equals("sli"))
                mLine.setVisibility(View.VISIBLE);
            else
                mLine.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        }

   /* @Override
    public String toString() {
        return mTextView.getText().toString();
    }*/


    }

}
