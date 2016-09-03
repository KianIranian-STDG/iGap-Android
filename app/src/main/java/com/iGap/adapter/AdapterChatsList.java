package com.iGap.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivityChat;
import com.iGap.activitys.ActivityMain;
import com.iGap.activitys.MyDialog;
import com.iGap.module.CircleImageView;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter used to display chats list
 */
public class AdapterChatsList extends RecyclerView.Adapter<AdapterChatsList.ViewHolder> {
    private List<StructContactInfo> list;
    private Context context;
    private OnComplete complete;

    public AdapterChatsList(ArrayList<StructContactInfo> list, Context context, OnComplete complete) {
        this.list = list;
        this.context = context;
        this.complete = complete;
    }

    /**
     * used for clearing data set and notifying
     */
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    /**
     * used for inserting new item and notifying
     *
     * @param structContactInfo
     */
    public void insert(StructContactInfo structContactInfo) {
        list.add(structContactInfo);
        notifyDataSetChanged();
    }

    /**
     * used for inserting new items and notifying
     *
     * @param structContactInfoList list of new items
     * @param clear                 if you want data set to be cleared before adding new items, pass true
     */
    public void insert(List<StructContactInfo> structContactInfoList, boolean clear) {
        if (clear) {
            list.clear();
        }
        list.addAll(structContactInfoList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sub_layout, parent, false));
    }

    /**
     * setup views listeners
     *
     * @param holder ViewHolder
     */
    private void setupListeners(final ViewHolder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("XXX", "CLICK");
                if (ActivityMain.isMenuButtonAddShown) {
                    Log.i("XXX", "CLICK 1");
                    complete.complete(true, "closeMenuButton", "");
                } else {
                    Log.i("XXX", "CLICK ActivityChat");
                    Intent intent = new Intent(context, ActivityChat.class);
//                    intent.putExtra("ChatType", list.get(holder.getAdapterPosition()).contactType);
                    intent.putExtra("ChatType", "CHAT");
                    intent.putExtra("ContactID", list.get(holder.getAdapterPosition()).contactID);
                    intent.putExtra("IsMute", list.get(holder.getAdapterPosition()).muteNotification);
                    intent.putExtra("OwnerShip", list.get(holder.getAdapterPosition()).ownerShip);
                    intent.putExtra("ContactName", list.get(holder.getAdapterPosition()).contactName);
                    intent.putExtra("MemberCount", list.get(holder.getAdapterPosition()).memberCount);
                    intent.putExtra("LastSeen", list.get(holder.getAdapterPosition()).lastSeen);
                    intent.putExtra("RoomId", Long.parseLong(list.get(holder.getAdapterPosition()).contactID));

                    context.startActivity(intent);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (ActivityMain.isMenuButtonAddShown) {
                    complete.complete(true, "closeMenuButton", "");
                } else {
                    MyDialog.showDialogMenuItemContacts(context, list.get(holder.getAdapterPosition()).contactType, list.get(holder.getAdapterPosition()).muteNotification, new OnComplete() {
                        @Override
                        public void complete(boolean result, String messageOne, String MessageTow) {
                            onSelectContactMenu(messageOne, holder.getAdapterPosition());
                        }
                    });
                }
                return true;
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // TODO fill

        // setup views listeners
        setupListeners(holder);

        if (list.get(position).imageSource.length() > 0) {
            holder.image.setImageResource(Integer.parseInt(list.get(position).imageSource));
        } else {
            holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp60), list.get(position).contactName, ""));
        }

        holder.chatIcon.setText(getStringChatIcon(list.get(position).contactType));
        if (list.get(position).contactType == MyType.ChatType.singleChat) {
            holder.chatIcon.setVisibility(View.GONE);
        } else {
            holder.chatIcon.setVisibility(View.VISIBLE);
        }

        holder.name.setText(list.get(position).contactName);
        holder.lastMessage.setText(list.get(position).lastmessage);
        holder.lastSeen.setText(list.get(position).lastSeen);

        int unread = list.get(position).unreadMessag;
        if (unread < 1) {
            holder.unreadMessage.setVisibility(View.INVISIBLE);
        } else {
            holder.unreadMessage.setVisibility(View.VISIBLE);
//            if(unread>99)
//               holder.unreadMessage.setText("+99");
//            else
            holder.unreadMessage.setText(unread + "");

            if (list.get(position).muteNotification) {
                holder.unreadMessage.setBackgroundResource(R.drawable.oval_gray);
            } else {
                holder.unreadMessage.setBackgroundResource(R.drawable.oval_green);
            }
        }

        if (list.get(position).muteNotification) {
            holder.mute.setVisibility(View.VISIBLE);
        } else {
            holder.mute.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * get string chat icon
     *
     * @param chatType chat type
     * @return String
     */
    private String getStringChatIcon(MyType.ChatType chatType) {
        switch (chatType) {
            case singleChat:
                return G.context.getString(R.string.fa_user);
            case channel:
                return G.context.getString(R.string.fa_bullhorn);
            case groupChat:
                return G.context.getString(R.string.fa_group);
            default:
                return null;
        }
    }

    /**
     * on select contact menu
     *
     * @param message  message text
     * @param position position dfdfdfdf
     */
    private void onSelectContactMenu(String message, int position) {
        switch (message) {
            case "txtMuteNotification":
                muteNotification(position);
                break;
            case "txtClearHistory":
                clearHistory(position);
                break;
            case "txtDeleteChat":
                deleteChat(position);
                break;
        }
    }


    private void muteNotification(int position) {
        Log.e("fff", " txtMuteNotification " + position);

        notifyItemChanged(position);

        list.get(position).muteNotification = !list.get(position).muteNotification;
    }

    private void clearHistory(int position) {
        Log.e("fff", " txtClearHistory " + position);
    }

    private void deleteChat(int position) {
        Log.e("fff", " txtDeleteChat " + position);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView image;
        protected View distanceColor;
        protected TextView chatIcon;
        protected TextView name;
        protected TextView mute;
        protected TextView lastMessage;
        protected TextView lastSeen;
        protected TextView unreadMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (CircleImageView) itemView.findViewById(R.id.cs_img_contact_picture);
            distanceColor = itemView.findViewById(R.id.cs_view_distance_color);
            chatIcon = (TextView) itemView.findViewById(R.id.cs_txt_contact_icon);
            name = (TextView) itemView.findViewById(R.id.cs_txt_contact_name);
            lastMessage = (TextView) itemView.findViewById(R.id.cs_txt_last_message);
            lastSeen = (TextView) itemView.findViewById(R.id.cs_txt_contact_time);
            unreadMessage = (TextView) itemView.findViewById(R.id.cs_txt_unread_message);
            mute = (TextView) itemView.findViewById(R.id.cs_txt_mute);

            chatIcon.setTypeface(G.fontawesome);
            name.setTypeface(G.arialBold);
            lastMessage.setTypeface(G.arial);
            mute.setTypeface(G.fontawesome);
            lastSeen.setTypeface(G.arial);
            unreadMessage.setTypeface(G.arial);
        }
    }
}
