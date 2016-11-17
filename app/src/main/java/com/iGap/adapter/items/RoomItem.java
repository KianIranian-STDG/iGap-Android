package com.iGap.adapter.items;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AvatarsAdapter;
import com.iGap.interfaces.IChatItemAvatar;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.OnComplete;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAttachmentFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestFileDownload;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import io.github.meness.emoji.EmojiTextView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/``016.
 */

/**
 * chat item for main displaying chats
 */
public class RoomItem extends AbstractItem<RoomItem, RoomItem.ViewHolder>
        implements IChatItemAvatar {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public RealmRoom mInfo;
    public OnComplete mComplete;

    public RoomItem setComplete(OnComplete complete) {
        this.mComplete = complete;
        return this;
    }

    public RealmRoom getInfo() {
        return mInfo;
    }

    public RoomItem setInfo(RealmRoom info) {
        this.mInfo = info;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout;
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

    /**
     * request for avatar thumbnail
     */
    private void requestForAvatarThumbnail(String token) {
        if (!AvatarsAdapter.hasThumbnailRequested(token)) {
            AvatarsAdapter.thumbnailRequests.add(token);

            onRequestDownloadAvatarThumbnail(token, false);
        }
    }

    public void onRequestDownloadAvatarThumbnail(String token, boolean done) {
        final String fileName = "thumb_" + token + "_" + mInfo.getAvatar().getFile().getName();
        if (done) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, mInfo.getAvatar().getFile().getToken()).findFirst();
                    attachment.setLocalThumbnailPath(G.DIR_TEMP + "/" + fileName);
                }
            });
            realm.close();

            return; // necessary
        }

        ProtoFileDownload.FileDownload.Selector selector =
                ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
        String identity =
                mInfo.getAvatar().getFile().getToken() + '*' + selector.toString() + '*' + mInfo.getAvatar().getFile().getSmallThumbnail().getSize() + '*' + fileName + '*' + 0;

        new RequestFileDownload().download(token, 0, (int) mInfo.getAvatar().getFile().getSmallThumbnail().getSize(),
                selector, identity);
    }

    @Override
    public void onRequestDownloadAvatar(long offset, int progress) {

    }

    private void requestForUserInfo() {
        if (!mInfo.userInfoAlreadyRequested) {
            RequestUserInfo requestUserInfo = new RequestUserInfo();
            requestUserInfo.userInfo(mInfo.getOwnerId());

            mInfo.userInfoAlreadyRequested = true;
        }
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (!TextUtils.isEmpty(mInfo.getDraft().getMessage())) {
            holder.messageStatus.setVisibility(View.GONE);
            holder.lastMessage.setVisibility(View.VISIBLE);
            holder.lastMessageSender.setVisibility(View.VISIBLE);
            holder.lastMessageSender.setText("Draft : ");
            holder.lastMessageSender.setTextColor(Color.parseColor("#ff4644"));
            holder.lastMessage.setText(mInfo.getDraft().getMessage());
        } else {
            String lastMessage = AppUtils.rightLastMessage(holder.itemView.getResources(), mInfo.getType(), mInfo.getLastMessage().getMessageId());
            if (lastMessage == null) {
                lastMessage = mInfo.getLastMessage().getMessage();
            }

            if (lastMessage == null || lastMessage.isEmpty()) {
                holder.messageStatus.setVisibility(View.GONE);
                holder.lastMessage.setVisibility(View.GONE);
                holder.lastMessageSender.setVisibility(View.GONE);
            } else {
                if (mInfo.getLastMessage().isSenderMe()) {
                    AppUtils.rightMessageStatus(holder.messageStatus, mInfo.getLastMessage().getStatus());
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
                if (mInfo.getLastMessage().isSenderMe()) {
                    lastMessageSender = "You : ";
                } else {
                    Realm realm1 = Realm.getDefaultInstance();
                    RealmResults<RealmRoomMessage> results = realm1.where(RealmRoomMessage.class)
                            .equalTo(RealmRoomMessageFields.ROOM_ID, mInfo.chatId)
                            .findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                    if (!results.isEmpty()) {
                        RealmRoomMessage realmRoomMessage = results.first();
                        if (realmRoomMessage != null) {
                            RealmRegisteredInfo realmRegisteredInfo =
                                    realm1.where(RealmRegisteredInfo.class)
                                            .equalTo(RealmRegisteredInfoFields.ID,
                                                    realmRoomMessage.getUserId())
                                            .findFirst();
                            if (realmRegisteredInfo != null) {
                                lastMessageSender = realmRegisteredInfo.getDisplayName() + " : ";
                            }
                        }
                    }
                    realm1.close();
                }

                if (mInfo.getType() == RoomType.GROUP) {
                    holder.lastMessageSender.setText(lastMessageSender);
                    holder.lastMessageSender.setTextColor(Color.parseColor("#2bbfbd"));
                    holder.lastMessageSender.setVisibility(View.VISIBLE);
                } else {
                    holder.lastMessageSender.setVisibility(View.GONE);
                }

                holder.lastMessage.setVisibility(View.VISIBLE);


                if (mInfo.getLastMessage() != null) {
                    switch (mInfo.getLastMessage().getMessageType()) {
                        case
                    }
                    if (realmRoomMessage.getMessageType().contains(ProtoGlobal.RoomMessageType.VOICE.toString())) {
                        holder.lastMessage.setText(R.string.voice_message);
                    } else if (realmRoomMessage.getMessageType().contains(ProtoGlobal.RoomMessageType.VIDEO.toString())) {
                        holder.lastMessage.setText(R.string.video_message);
                    } else if (realmRoomMessage.getMessageType().contains(ProtoGlobal.RoomMessageType.FILE.toString())) {
                        holder.lastMessage.setText(R.string.file_message);
                    } else if (realmRoomMessage.getMessageType().contains(ProtoGlobal.RoomMessageType.AUDIO.toString())) {
                        holder.lastMessage.setText(R.string.audio_message);
                    } else if (realmRoomMessage.getMessageType().contains(ProtoGlobal.RoomMessageType.IMAGE.toString())) {
                        holder.lastMessage.setText(R.string.image_message);
                    } else if (realmRoomMessage.getMessageType().contains(ProtoGlobal.RoomMessageType.CONTACT.toString())) {
                        holder.lastMessage.setText(R.string.contact_message);
                    } else if (realmRoomMessage.getMessageType().contains(ProtoGlobal.RoomMessageType.GIF.toString())) {
                        holder.lastMessage.setText(R.string.gif_message);
                    } else if (realmRoomMessage.getMessageType().contains(ProtoGlobal.RoomMessageType.LOCATION.toString())) {
                        holder.lastMessage.setText(R.string.location_message);
                    } else {
                        holder.lastMessage.setText(lastMessage);
                    }


                } else {
                    holder.lastMessage.setText(lastMessage);
                }
            }
        }

        if (mInfo.getAvatar().getFile() != null) {

            if (mInfo.getAvatar().getFile().isFileExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mInfo.getAvatar().getFile().getLocalFilePath()), holder.image);
            } else if (mInfo.getAvatar().getFile().isThumbnailExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mInfo.getAvatar().getFile().getLocalThumbnailPath()), holder.image);

            } else {
                holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView
                        .getContext()
                        .getResources()
                        .getDimension(R.dimen.dp60), mInfo.initials, mInfo.color));

                if (mInfo.chatType != RoomType.CHAT) {
                    if (mInfo.getAvatar().getFile().token != null && !mInfo.getAvatar().getFile().token.isEmpty()) {
                        requestForAvatarThumbnail(mInfo.getAvatar().getFile().token);
                    }
                } else {
                    if (mInfo.getAvatar().getFile().token != null && !mInfo.getAvatar().getFile().token.isEmpty()) {
                        requestForAvatarThumbnail(mInfo.getAvatar().getFile().token);
                    } else {
                        requestForUserInfo();
                    }
                }
            }
        } else {
            holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mInfo.initials, mInfo.color));

            if (mInfo.chatType != RoomType.CHAT) {
                requestForAvatarThumbnail(mInfo.getAvatar().getFile().token);
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
            holder.lastSeen.setText(
                    TimeUtils.toLocal(mInfo.lastMessageTime, G.ROOM_LAST_MESSAGE_TIME));
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

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView image;
        protected View distanceColor;
        protected TextView chatIcon;
        protected EmojiTextView name;
        protected EmojiTextView lastMessageSender;
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