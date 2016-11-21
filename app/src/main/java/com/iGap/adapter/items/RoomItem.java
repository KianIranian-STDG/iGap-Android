package com.iGap.adapter.items;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AvatarsAdapter;
import com.iGap.adapter.RoomsAdapter;
import com.iGap.interfaces.IChatItemAvatar;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.OnComplete;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoFileDownload;
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

import static android.view.View.GONE;

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
        if (!RoomsAdapter.userInfoAlreadyRequests.contains(mInfo.getOwnerId())) {
            RequestUserInfo requestUserInfo = new RequestUserInfo();
            requestUserInfo.userInfo(mInfo.getOwnerId());

            RoomsAdapter.userInfoAlreadyRequests.add(mInfo.getOwnerId());
        }
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        // to fix 'This Realm instance has already been closed, making it unusable.'
        if (!mInfo.isValid()) {
            return;
        }

        if (mInfo.getDraft() != null && !TextUtils.isEmpty(mInfo.getDraft().getMessage())) {
            holder.messageStatus.setVisibility(GONE);
            holder.lastMessage.setVisibility(View.VISIBLE);
            holder.lastMessageSender.setVisibility(View.VISIBLE);
            holder.lastMessageSender.setText("Draft : ");
            holder.lastMessageSender.setTextColor(Color.parseColor("#ff4644"));
            holder.lastMessage.setText(mInfo.getDraft().getMessage());
        } else {
            if (mInfo.getLastMessage() != null) {
                String lastMessage = AppUtils.rightLastMessage(holder.itemView.getResources(), mInfo.getType(), mInfo.getLastMessage().getMessageId());
                if (lastMessage == null) {
                    lastMessage = mInfo.getLastMessage().getMessage();
                }

                if (lastMessage == null || lastMessage.isEmpty()) {
                    holder.messageStatus.setVisibility(GONE);
                    holder.lastMessage.setVisibility(GONE);
                    holder.lastMessageSender.setVisibility(GONE);
                } else {
                    if (mInfo.getLastMessage().isSenderMe()) {
                        AppUtils.rightMessageStatus(holder.messageStatus, mInfo.getLastMessage().getStatus());
                        holder.messageStatus.setVisibility(View.VISIBLE);
                    } else {
                        holder.messageStatus.setVisibility(GONE);
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
                                .equalTo(RealmRoomMessageFields.ROOM_ID, mInfo.getId())
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
                        holder.lastMessageSender.setVisibility(GONE);
                    }

                    holder.lastMessage.setVisibility(View.VISIBLE);

                    if (mInfo.getLastMessage() != null) {
                        switch (mInfo.getLastMessage().getMessageType()) {
                            case VOICE:
                                holder.lastMessage.setText(R.string.voice_message);
                                break;
                            case VIDEO:
                                holder.lastMessage.setText(R.string.video_message);
                                break;
                            case FILE:
                                holder.lastMessage.setText(R.string.file_message);
                                break;
                            case AUDIO:
                                holder.lastMessage.setText(R.string.audio_message);
                                break;
                            case IMAGE:
                                holder.lastMessage.setText(R.string.image_message);
                                break;
                            case CONTACT:
                                holder.lastMessage.setText(R.string.contact_message);
                                break;
                            case GIF:
                                holder.lastMessage.setText(R.string.gif_message);
                                break;
                            case LOCATION:
                                holder.lastMessage.setText(R.string.location_message);
                                break;
                            default:
                                holder.lastMessage.setText(lastMessage);
                                break;
                        }
                    } else {
                        holder.lastMessage.setText(lastMessage);
                    }
                }
            } else {
                holder.lastMessage.setVisibility(GONE);
                holder.lastSeen.setVisibility(GONE);
                holder.messageStatus.setVisibility(GONE);
            }
        }

        if (mInfo.getAvatar() != null && mInfo.getAvatar().getFile() != null) {

            if (mInfo.getAvatar().getFile().isFileExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mInfo.getAvatar().getFile().getLocalFilePath()), holder.image);
            } else if (mInfo.getAvatar().getFile().isThumbnailExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mInfo.getAvatar().getFile().getLocalThumbnailPath()), holder.image);

            } else {
                holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView
                        .getContext()
                        .getResources()
                        .getDimension(R.dimen.dp60), mInfo.getInitials(), mInfo.getColor()));

                if (mInfo.getType() != RoomType.CHAT) {
                    if (mInfo.getAvatar().getFile().getToken() != null && !mInfo.getAvatar().getFile().getToken().isEmpty()) {
                        requestForAvatarThumbnail(mInfo.getAvatar().getFile().getToken());
                    }
                } else {
                    if (mInfo.getAvatar().getFile().getToken() != null && !mInfo.getAvatar().getFile().getToken().isEmpty()) {
                        requestForAvatarThumbnail(mInfo.getAvatar().getFile().getToken());
                    } else {
                        requestForUserInfo();
                    }
                }
            }
        } else {
            holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mInfo.getInitials(), mInfo.getColor()));
            requestForUserInfo();
        }

        if (mInfo.getType() == RoomType.CHAT) {
            holder.chatIcon.setVisibility(GONE);
        } else if (mInfo.getType() == RoomType.GROUP) {
            holder.chatIcon.setVisibility(View.VISIBLE);
            holder.chatIcon.setText(getStringChatIcon(RoomType.GROUP));
            holder.chatIcon.setTypeface(G.flaticon);
        } else if (mInfo.getType() == RoomType.CHANNEL) {
            holder.chatIcon.setVisibility(View.VISIBLE);
            holder.chatIcon.setText(getStringChatIcon(RoomType.CHANNEL));
            holder.chatIcon.setTypeface(G.fontawesome);
        }

        holder.name.setText(mInfo.getTitle());

        if (mInfo.getLastMessage() != null && mInfo.getLastMessage().getUpdateTime() != 0) {
            holder.lastSeen.setText(
                    TimeUtils.toLocal(mInfo.getLastMessage().getUpdateTime(), G.ROOM_LAST_MESSAGE_TIME));
            holder.lastSeen.setVisibility(View.VISIBLE);
        } else {
            holder.lastSeen.setVisibility(GONE);
        }

        if (mInfo.getUnreadCount() < 1) {
            holder.unreadMessage.setVisibility(GONE);
        } else {
            holder.unreadMessage.setVisibility(View.VISIBLE);

            holder.unreadMessage.setText(Integer.toString(mInfo.getUnreadCount()));

            if (mInfo.getMute()) {
                holder.unreadMessage.setBackgroundResource(R.drawable.oval_gray);
            } else {
                holder.unreadMessage.setBackgroundResource(R.drawable.oval_green);
            }
        }

        if (mInfo.getMute()) {
            holder.mute.setVisibility(View.VISIBLE);
        } else {
            holder.mute.setVisibility(GONE);
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