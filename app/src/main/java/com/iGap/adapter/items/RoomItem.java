package com.iGap.adapter.items;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.chat.AbstractChatItem;
import com.iGap.module.CircleImageView;
import com.iGap.module.EmojiTextView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.OnComplete;
import com.iGap.module.StructChatInfo;
import com.iGap.module.StructDownloadAttachment;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestFileDownload;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */

/**
 * chat item for main displaying chats
 */
public class RoomItem extends AbstractItem<RoomItem, RoomItem.ViewHolder> {
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
                return G.context.getString(R.string.fa_user);
            case CHANNEL:
                return G.context.getString(R.string.fa_bullhorn);
            case GROUP:
                return G.context.getString(R.string.fa_group);
            default:
                return null;
        }
    }

    private void requestForThumbnail() {
        // create new download attachment once with attachment token
        if (mInfo.downloadAttachment == null) {
            mInfo.downloadAttachment = new StructDownloadAttachment(mInfo.avatar.token);
        }

        // request thumbnail
        if (!mInfo.downloadAttachment.thumbnailRequested) {
            onRequestDownloadThumbnail();
            // prevent from multiple requesting thumbnail
            mInfo.downloadAttachment.thumbnailRequested = true;
        }
    }

    public void onRequestDownloadThumbnail() {
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
        if (mInfo.avatar != null && (mInfo.avatar.getLocalThumbnailPath() == null || mInfo.avatar.getLocalThumbnailPath().isEmpty())) {
            mInfo.avatar.setLocalThumbnailPath(mInfo.chatId, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + mInfo.downloadAttachment.token + System.nanoTime() + mInfo.avatar.name);
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

    /**
     * return suitable path for using with UIL
     *
     * @param path String path
     * @return correct local path/passed path
     */
    protected String suitablePath(String path) {
        if (path.matches("\\w+?://")) {
            return path;
        } else {
            return Uri.fromFile(new File(path)).toString();
        }
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.lastMessage.setText(mInfo.lastmessage);

        if (mInfo.avatar != null) {
            if (mInfo.avatar.isFileExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(suitablePath(mInfo.avatar.getLocalFilePath()), holder.image);
            } else if (mInfo.avatar.isThumbnailExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(suitablePath(mInfo.avatar.getLocalThumbnailPath()), holder.image);
            } else {
                holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mInfo.initials, mInfo.color));

                if (mInfo.chatType != RoomType.CHAT) {
                    if (mInfo.avatar.token != null && !mInfo.avatar.token.isEmpty()) {
                        requestForThumbnail();
                    }
                } else {
                    if (mInfo.avatar.token != null && !mInfo.avatar.token.isEmpty()) {
                        requestForThumbnail();
                    } else {
                        requestForUserInfo();
                    }
                }
            }
        } else {
            holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mInfo.initials, mInfo.color));

            if (mInfo.chatType != RoomType.CHAT) {
                requestForThumbnail();
            } else {
                requestForUserInfo();
            }
        }

        holder.chatIcon.setText(getStringChatIcon(mInfo.chatType));
        if (mInfo.chatType == RoomType.CHAT) {
            holder.chatIcon.setVisibility(View.GONE);
        } else {
            holder.chatIcon.setVisibility(View.VISIBLE);
        }

        holder.name.setText(mInfo.chatTitle);

        if (!mInfo.lastmessage.isEmpty()) {
            holder.lastMessage.setText(mInfo.lastmessage);
            holder.lastMessage.setVisibility(View.VISIBLE);

            if (mInfo.lastMessageSenderIsMe) {
                AbstractChatItem.updateMessageStatus(holder.messageStatus, mInfo.lastMessageStatus);

                holder.messageStatus.setVisibility(View.VISIBLE);
            } else {
                holder.messageStatus.setVisibility(View.GONE);
            }
        } else {
            holder.lastMessage.setVisibility(View.GONE);
            holder.messageStatus.setVisibility(View.GONE);
        }


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
            lastSeen = (TextView) view.findViewById(R.id.cs_txt_contact_time);
            unreadMessage = (TextView) view.findViewById(R.id.cs_txt_unread_message);
            mute = (TextView) view.findViewById(R.id.cs_txt_mute);
            messageStatus = (MaterialDesignTextView) view.findViewById(R.id.cslr_txt_tic);

            chatIcon.setTypeface(G.fontawesome);
            mute.setTypeface(G.fontawesome);
            lastSeen.setTypeface(G.arial);
            unreadMessage.setTypeface(G.arial);
        }
    }
}
