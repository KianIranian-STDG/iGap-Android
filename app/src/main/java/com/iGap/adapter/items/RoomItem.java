package com.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.interface_package.IChatItemAvatar;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.EmojiTextView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.OnComplete;
import com.iGap.module.StructChatInfo;
import com.iGap.module.StructDownloadAttachment;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmChatHistoryFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestFileDownload;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/``016.
 */

/**
 * chat item for main displaying chats
 */
public class RoomItem extends AbstractItem<RoomItem, RoomItem.ViewHolder> implements IChatItemAvatar {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public StructChatInfo mInfo;
    public OnComplete mComplete;

    public RoomItem setComplete(OnComplete complete) {
        this.mComplete = complete;
        return this;
    }

    public RoomItem setInfo(StructChatInfo info) {
        this.mInfo = info;
        return this;
    }

    public StructChatInfo getInfo() {
        return mInfo;
    }


    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * get string chat icon
     *
     * @param chatType chat type
     * @return String
     */
    private String getStringChatIcon(RoomType chatType) {
        switch (chatType) {
            case CHAT:
                return "";//G.context.getString(R.string.md_user_shape);
            case CHANNEL:
                return G.context.getString(R.string.fa_bullhorn);
            case GROUP:
                return G.context.getString(R.string.md_users_social_symbol);
            default:
                return null;
        }
    }

    private void requestForAvatar() {
        // create new download attachment once with attachment token
        if (mInfo.downloadAttachment == null) {
            mInfo.downloadAttachment = new StructDownloadAttachment(mInfo.avatar.token);
        }

        // request thumbnail
        if (!mInfo.downloadAttachment.thumbnailRequested) {
            onRequestDownloadAvatar();
            // prevent from multiple requesting thumbnail
            mInfo.downloadAttachment.thumbnailRequested = true;
        }
    }

    @Override
    public void onRequestDownloadAvatar() {
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;
        if (mInfo.chatType == RoomType.CHAT) {
            if (mInfo.avatar != null && (mInfo.avatar.getLocalThumbnailPath() == null || mInfo.avatar.getLocalThumbnailPath().isEmpty())) {
                mInfo.avatar.setLocalThumbnailPathForAvatar(mInfo.ownerId, mInfo.downloadAttachment.token + System.nanoTime() + mInfo.avatar.name, selector);
            }
        } else {
            if (mInfo.avatar != null && (mInfo.avatar.getLocalThumbnailPath() == null || mInfo.avatar.getLocalThumbnailPath().isEmpty())) {
                mInfo.avatar.setLocalThumbnailPath(mInfo.chatId, mInfo.downloadAttachment.token + System.nanoTime() + mInfo.avatar.name);
            }
        }

        // I don't use offset in getting thumbnail
        String identity = mInfo.downloadAttachment.token + '*' + selector.toString() + '*' + mInfo.avatar.smallThumbnail.size + '*' + mInfo.avatar.getLocalThumbnailPath() + '*' + mInfo.downloadAttachment.offset;

        new RequestFileDownload().download(mInfo.downloadAttachment.token, 0, (int) mInfo.avatar.smallThumbnail.size, selector, identity);
    }

    private void requestForUserInfo() {
        if (!mInfo.userInfoAlreadyRequested) {
            RequestUserInfo requestUserInfo = new RequestUserInfo();
            requestUserInfo.userInfo(mInfo.ownerId);

            mInfo.userInfoAlreadyRequested = true;
        }
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        String lastMessage = AppUtils.rightLastMessage(holder.itemView.getResources(), mInfo.chatType, mInfo.lastMessageId);
        if (lastMessage == null || lastMessage.isEmpty()) {
            holder.messageStatus.setVisibility(View.GONE);
            holder.lastMessage.setVisibility(View.GONE);
            holder.lastMessageSender.setVisibility(View.GONE);
        } else {
            if (mInfo.lastMessageSenderIsMe) {
                AppUtils.rightMessageStatus(holder.messageStatus, mInfo.lastMessageStatus);

                holder.messageStatus.setVisibility(View.VISIBLE);
            } else {
                holder.messageStatus.setVisibility(View.GONE);
            }

               /*
                * here i get latest message from chat history with chatId and
                * get DisplayName with that . when login app client get latest
                * message for each group from server , if latest message that
                * send server and latest message that exist in client for that
                * room be different latest message sender showing will be wrong
                */

            String lastMessageSender = "";
            Log.i("RRR", "mInfo.lastMessageSenderIsMe : " + mInfo.lastMessageSenderIsMe);
            if (mInfo.lastMessageSenderIsMe) {
                lastMessageSender = "You : ";
            } else {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<RealmChatHistory> results = realm.where(RealmChatHistory.class).equalTo(RealmChatHistoryFields.ROOM_ID, mInfo.chatId).findAllSorted(RealmChatHistoryFields.ID, Sort.DESCENDING);
                if (results != null) {
                    RealmChatHistory realmChatHistory = results.first();
                    Log.i("RRR", "realmChatHistory 1 : " + realmChatHistory);
                    Log.i("RRR", "realmChatHistory.getRoomMessage() 2 : " + realmChatHistory.getRoomMessage());
                    if (realmChatHistory != null && realmChatHistory.getRoomMessage() != null) {
                        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, realmChatHistory.getRoomMessage().getUserId()).findFirst();
                        Log.i("RRR", "realmRegisteredInfo 3 : " + realmRegisteredInfo);
                        if (realmRegisteredInfo != null) {
                            Log.i("RRR", " realmRegisteredInfo.getDisplayName() 4 : " + realmRegisteredInfo.getDisplayName());
                            lastMessageSender = realmRegisteredInfo.getDisplayName() + " : ";
                        }
                    }
                }
                realm.close();
            }

            if (mInfo.chatType == RoomType.GROUP) {
                holder.lastMessageSender.setText(lastMessageSender);
                holder.lastMessageSender.setVisibility(View.VISIBLE);
            } else {
                holder.lastMessageSender.setVisibility(View.GONE);
            }
            holder.lastMessage.setVisibility(View.VISIBLE);
            holder.lastMessage.setText(lastMessage);
        }

        if (mInfo.avatar != null) {
            if (mInfo.avatar.isFileExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mInfo.avatar.getLocalFilePath()), holder.image);
            } else if (mInfo.avatar.isThumbnailExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mInfo.avatar.getLocalThumbnailPath()), holder.image);
            } else {
                holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mInfo.initials, mInfo.color));

                if (mInfo.chatType != RoomType.CHAT) {
                    if (mInfo.avatar.token != null && !mInfo.avatar.token.isEmpty()) {
                        requestForAvatar();
                    }
                } else {
                    if (mInfo.avatar.token != null && !mInfo.avatar.token.isEmpty()) {
                        requestForAvatar();
                    } else {
                        requestForUserInfo();
                    }
                }
            }
        } else {
            holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mInfo.initials, mInfo.color));

            if (mInfo.chatType != RoomType.CHAT) {
                requestForAvatar();
            } else {
                requestForUserInfo();
            }
        }

        if (mInfo.chatType == RoomType.CHAT) {
            holder.chatIcon.setVisibility(View.GONE);
        } else if (mInfo.chatType == RoomType.GROUP) {
            holder.chatIcon.setVisibility(View.VISIBLE);
            holder.chatIcon.setText(getStringChatIcon(RoomType.GROUP));
            holder.chatIcon.setTypeface(G.flaticon);

        } else if (mInfo.chatType == RoomType.CHANNEL) {
            holder.chatIcon.setVisibility(View.VISIBLE);
            holder.chatIcon.setText(getStringChatIcon(RoomType.CHANNEL));
            holder.chatIcon.setTypeface(G.fontawesome);

        }

        holder.name.setText(mInfo.chatTitle);


        if (mInfo.lastMessageTime != 0) {
            holder.lastSeen.setText(TimeUtils.toLocal(mInfo.lastMessageTime, G.ROOM_LAST_MESSAGE_TIME));
            holder.lastSeen.setVisibility(View.VISIBLE);
        } else {
            holder.lastSeen.setVisibility(View.GONE);
        }

        if (mInfo.unreadMessagesCount < 1) {
            holder.unreadMessage.setVisibility(View.GONE);
        } else {
            holder.unreadMessage.setVisibility(View.VISIBLE);

            holder.unreadMessage.setText(Integer.toString(mInfo.unreadMessagesCount));

            if (mInfo.muteNotification) {
                holder.unreadMessage.setBackgroundResource(R.drawable.oval_gray);
            } else {
                holder.unreadMessage.setBackgroundResource(R.drawable.oval_green);
            }
        }

        if (mInfo.muteNotification) {
            holder.mute.setVisibility(View.VISIBLE);
        } else {
            holder.mute.setVisibility(View.GONE);
        }
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView image;
        protected View distanceColor;
        protected TextView chatIcon;
        protected EmojiTextView name;
        protected TextView mute;
        protected EmojiTextView lastMessage;
        protected EmojiTextView lastMessageSender;
        protected TextView lastSeen;
        protected TextView unreadMessage;
        protected MaterialDesignTextView messageStatus;

        public ViewHolder(View view) {
            super(view);

            image = (CircleImageView) view.findViewById(R.id.cs_img_contact_picture);
            distanceColor = view.findViewById(R.id.cs_view_distance_color);
            chatIcon = (TextView) view.findViewById(R.id.cs_txt_contact_icon);
            name = (EmojiTextView) view.findViewById(R.id.cs_txt_contact_name);
            lastMessage = (EmojiTextView) view.findViewById(R.id.cs_txt_last_message);
            lastMessageSender = (EmojiTextView) view.findViewById(R.id.cs_txt_last_message_sender);
            lastSeen = (TextView) view.findViewById(R.id.cs_txt_contact_time);
            unreadMessage = (TextView) view.findViewById(R.id.cs_txt_unread_message);
            mute = (TextView) view.findViewById(R.id.cs_txt_mute);
            messageStatus = (MaterialDesignTextView) view.findViewById(R.id.cslr_txt_tic);


            mute.setTypeface(G.fontawesome);
            lastSeen.setTypeface(G.arial);
            unreadMessage.setTypeface(G.arial);
        }
    }
}
