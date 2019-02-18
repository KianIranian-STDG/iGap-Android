/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.support.annotation.CallSuper;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lalongooo.videocompressor.video.MediaController;
import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperGetMessageState;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.IChatItemAttachment;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnProgressUpdate;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messageprogress.OnMessageProgressClick;
import net.iGap.messageprogress.OnProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.MakeButtons;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyType;
import net.iGap.module.ReserveSpaceGifImageView;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.module.additionalData.ButtonActionType;
import net.iGap.module.additionalData.ButtonEntity;
import net.iGap.module.enums.LocalFileType;
import net.iGap.module.enums.SendingStep;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmChannelExtraFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestChannelAddMessageReaction;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;
import static net.iGap.fragments.FragmentChat.getRealmChat;
import static net.iGap.helper.HelperCalander.convertToUnicodeFarsiNumber;

public abstract class AbstractMessage<Item extends AbstractMessage<?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> implements IChatItemAttachment<VH> {//IChatItemAvatar
    public static ArrayMap<Long, String> updateForwardInfo = new ArrayMap<>();// after get user info or room info if need update view in chat activity
    public IMessageItem messageClickListener;
    public StructMessageInfo mMessage;
    public boolean directionalBased;
    public ProtoGlobal.Room.Type type;
    private int minWith = 0;
    CharSequence myText;
    private RealmAttachment realmAttachment;
    private RealmRoom realmRoom;
    private RealmChannelExtra realmChannelExtra;
    private RealmRoom realmRoomForwardedFrom;
    private MessagesAdapter<AbstractMessage> mAdapter;
    /**
     * add this prt for video player
     */
    //@Override public void onPlayPauseVideo(VH holder, String localPath, int isHide, double time) {
    //    // empty
    //}
    public AbstractMessage(MessagesAdapter<AbstractMessage> mAdapter, boolean directionalBased, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        //this.realmChat = realmChat;
        this.directionalBased = directionalBased;
        this.type = type;
        this.mAdapter = mAdapter;
        this.messageClickListener = messageClickListener;

    }

    public static void processVideo(final TextView duration, final View holder1, final StructMessageInfo mMessage) {

        MediaController.onPercentCompress = new MediaController.OnPercentCompress() {
            @Override
            public void compress(final long percent, String path) {

                if (mMessage.getAttachment().getLocalFilePath() == null || !mMessage.getAttachment().getLocalFilePath().equals(path)) {
                    return;
                }

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (percent < 98) {

                            String p = percent + "";

                            if (HelperCalander.isLanguagePersian || HelperCalander.isLanguageArabic) {
                                p = convertToUnicodeFarsiNumber(p);
                            }
                            duration.setText(String.format(holder1.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.attachment.duration * 1000L)), AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true) + " " + G.context.getResources().getString(R.string.compressing) + " %" + p));
                        } else {
                            duration.setText(String.format(holder1.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.attachment.duration * 1000L)), AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true) + " " + G.context.getResources().getString(R.string.Uploading)));
                        }
                    }
                });
            }
        };
    }

    protected ProtoGlobal.Room.Type getRoomType() {
        return type;
    }

    @Override
    public void onPlayPauseGIF(VH holder, String localPath) throws ClassCastException {
        // empty
    }

    protected void setTextIfNeeded(TextView view) {
        if (!TextUtils.isEmpty(myText)) {
            view.setText(myText);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public AbstractMessage setMessage(StructMessageInfo message) {
        this.mMessage = message;

        if ((mMessage.forwardedFrom != null)) {
            long messageId = mMessage.forwardedFrom.getMessageId();
            if (mMessage.forwardedFrom.getMessageId() < 0) {
                messageId = messageId * (-1);
            }
            realmRoomForwardedFrom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getAuthorRoomId()).findFirst();
            realmChannelExtra = getRealmChat().where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, messageId).findFirst();
        } else {
            realmRoomForwardedFrom = null;
            realmChannelExtra = null;
        }

        realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.roomId).findFirst();
        RealmRoomMessage f = RealmRoomMessage.getFinalMessage(getRealmChat().where(RealmRoomMessage.class).
                equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID)).findFirst());
        if (f != null){
            realmAttachment = f.getAttachment();
        }
        if (mMessage.forwardedFrom != null) {
            myText = mMessage.forwardedFrom.getMessage();
        } else {
            myText = mMessage.messageText;
        }

        updateMessageText((String) myText);

        return this;
    }

    public void updateMessageText(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mMessage.hasLinkInMessage) {
                myText = HelperUrl.getLinkText(text, mMessage.linkInfo, mMessage.messageID);
            } else {
                myText = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(text) : text;
            }
        }
    }

    @Override
    public Item withIdentifier(long identifier) {
        return super.withIdentifier(identifier);
    }

    @Override
    @CallSuper
    public void bindView(final VH holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        ChatItemHolder mHolder;
        if (holder instanceof ChatItemHolder)
            mHolder = (ChatItemHolder) holder;
        else
            return;

        mHolder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("bagi" , "itemViewClick");
                new CountDownTimer(300, 100) {

                    public void onTick(long millisUntilFinished) {
                        view.setEnabled(false);
                    }

                    public void onFinish() {
                        view.setEnabled(true);
                    }
                }.start();

                if (FragmentChat.isInSelectionMode){
                    holder.itemView.performLongClick();
                } else {
                    if (G.isLinkClicked) {
                        G.isLinkClicked = false;
                        return;
                    }

                    if (messageClickListener != null && mMessage != null && mMessage.senderID != null && !mMessage.senderID.equalsIgnoreCase("-1")) {
                        if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            return ;
                        }
                        if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                            messageClickListener.onFailedMessageClick(view, mMessage, holder.getAdapterPosition());
                        } else {
                            messageClickListener.onContainerClick(view, mMessage, holder.getAdapterPosition());
                        }
                    }
                }
            }
        });


        if (holder instanceof ChatItemWithTextHolder) {
            ChatItemWithTextHolder withTextHolder = (ChatItemWithTextHolder) holder;
            withTextHolder.messageView.setHasEmoji(mMessage.hasEmojiInText);

            int maxsize = 0;
            withTextHolder.removeButtonLayout();
            if ((type == ProtoGlobal.Room.Type.CHANNEL) || (type == ProtoGlobal.Room.Type.CHAT) && mMessage.forwardedFrom != null) {
                maxsize = G.maxChatBox;
            }
            if (maxsize > 0)
                withTextHolder.messageView.setMaxWidth(maxsize);
            if (mMessage.hasLinkInMessage) {
                BetterLinkMovementMethod
                    .linkify(Linkify.ALL, withTextHolder.messageView)
                    .setOnLinkClickListener((tv, url) -> {
                        Log.d("bagi" , "OnMessageLinkClick");
                        return FragmentChat.isInSelectionMode;
                    })
                    .setOnLinkLongClickListener((tv, url) -> {
                        Log.d("bagi" , "OnMessageLinkLongClick");
                        return true;
                    });
            } else {
                // remove BetterLinkMovementMethod
            }

            try {
                if (mMessage.additionalData != null && mMessage.additionalData.AdditionalType == AdditionalType.UNDER_MESSAGE_BUTTON) {
                    HashMap<Integer, JSONArray> buttonList = MakeButtons.parseData(mMessage.additionalData.additionalData);
                    Gson gson = new GsonBuilder().create();
                    for (int i = 0; i < buttonList.size(); i++) {
                        LinearLayout childLayout = MakeButtons.createLayout();
                        for (int j = 0; j < buttonList.get(i).length(); j++) {
                            try {
                                ButtonEntity btnEntery = gson.fromJson(buttonList.get(i).get(j).toString(), new TypeToken<ButtonEntity>() {
                                }.getType());
                                btnEntery.setJsonObject(buttonList.get(i).get(j).toString());
                                childLayout = MakeButtons.addButtons(btnEntery, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (FragmentChat.isInSelectionMode) {
                                            holder.itemView.performLongClick();
                                            return;
                                        }
                                        onBotBtnClick(view);
                                    }
                                }, buttonList.get(i).length(), .75f, i, childLayout, mMessage.additionalData.AdditionalType);
                                //  childLayout = MakeButtons.addButtons(buttonList.get(i).get(j).toString(),this, buttonList.get(i).length(), .75f, btnEntery.getLable(), btnEntery.getLable(), btnEntery.getImageUrl(), i, btnEntery.getValue(), childLayout, btnEntery.getActionType(), mMessage.additionalData.AdditionalType);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        withTextHolder.addButtonLayout(childLayout);
                    }

                }

            } catch (Exception e) {
            }
            ((LinearLayout.LayoutParams) ((LinearLayout) withTextHolder.messageView.getParent()).getLayoutParams()).gravity = AndroidUtils.isTextRtl(mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessage() : mMessage.messageText) ? Gravity.RIGHT : Gravity.LEFT;
        }

        /**
         * for return message that start showing to view
         */
        messageClickListener.onItemShowingMessageId(mMessage);

        /**
         * this use for select foreground in activity chat for search item and hash item
         *
         */

        /**
         * noinspection RedundantCast
         */

        if (isSelected() || mMessage.isSelected) {
            ((FrameLayout) holder.itemView).setForeground(new ColorDrawable(G.context.getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
        } else {
            ((FrameLayout) holder.itemView).setForeground(new ColorDrawable(Color.TRANSPARENT));
        }

        /**
         * only will be called when message layout is directional-base (e.g. single chat)
         */
        if (directionalBased) {
            if ((mMessage.sendType == MyType.SendType.recvive) || type == ProtoGlobal.Room.Type.CHANNEL) {
                updateLayoutForReceive(holder);
            } else if (mMessage.sendType == MyType.SendType.send) {
                updateLayoutForSend(holder);
            }
        }

        if (!mMessage.isTimeOrLogMessage()) {
            /**
             * check failed state ,because if is failed we want show to user even is in channel
             */
            if (realmRoom != null && realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && ProtoGlobal.RoomMessageStatus.FAILED != ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status)) {
                mHolder.cslr_txt_tic.setVisibility(View.GONE);
            } else {
                mHolder.cslr_txt_tic.setVisibility(View.VISIBLE);
                AppUtils.rightMessageStatus(mHolder.cslr_txt_tic, ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status), mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType, mMessage.isSenderMe());
            }
        }
        /**
         * display 'edited' indicator beside message time if message was edited
         */
        if (mMessage.isEdited) {
            mHolder.txtEditedIndicator.setVisibility(View.VISIBLE);
        } else {
            mHolder.txtEditedIndicator.setVisibility(View.GONE);
        }
        /**
         * display user avatar only if chat type is GROUP
         */
        View messageSenderAvatar = mHolder.mainContainer.findViewById(R.id.messageSenderAvatar);
        if (messageSenderAvatar != null) {
            messageSenderAvatar.setVisibility(View.GONE);
        }

        replyMessageIfNeeded(holder, getRealmChat());
        forwardMessageIfNeeded(holder, getRealmChat());

        View messageSenderName = mHolder.m_container.findViewById(R.id.messageSenderName);
        if (messageSenderName != null) {
            messageSenderName.setVisibility(View.GONE);
        }

        if (type == ProtoGlobal.Room.Type.GROUP) {
            if (!mMessage.isSenderMe()) {
                addSenderNameToGroupIfNeed(mHolder, getRealmChat());

                if (messageSenderAvatar == null) {
                    messageSenderAvatar = ViewMaker.makeCircleImageView();
                    mHolder.mainContainer.addView(messageSenderAvatar, 0);
                }
                messageSenderAvatar.setVisibility(View.VISIBLE);

                messageSenderAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FragmentChat.isInSelectionMode) {
                            holder.itemView.performLongClick();
                            return;
                        }

                        messageClickListener.onSenderAvatarClick(v, mMessage, holder.getAdapterPosition());
                    }
                });

                messageSenderAvatar.setOnLongClickListener(getLongClickPerform(holder));

                //  String[] initialize =
                final ImageView copyMessageSenderAvatar = (ImageView) messageSenderAvatar;
                HelperAvatar.getAvatar(null, Long.parseLong(mMessage.senderID), HelperAvatar.AvatarType.USER, false, getRealmChat(), new OnAvatarGet() {
                    @Override
                    public void onAvatarGet(final String avatarPath, long ownerId) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), copyMessageSenderAvatar);
                            }
                        });
                    }

                    @Override
                    public void onShowInitials(final String initials, final String color) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                copyMessageSenderAvatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                            }
                        });
                    }
                });
                //if (initialize != null && initialize[0] != null && initialize[1] != null) {
                //    ((ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar)).setImageBitmap(
                //        net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initialize[0], initialize[1]));
                //}
            }
        }
        /**
         * set message time
         */

        String time = HelperCalander.getClocktime(mMessage.time, false);
        if (HelperCalander.isPersianUnicode) {
            mHolder.cslr_txt_time.setText(HelperCalander.convertToUnicodeFarsiNumber(time));
        } else {
            mHolder.cslr_txt_time.setText(time);
        }

        if (realmAttachment != null) {
            prepareAttachmentIfNeeded(holder, realmAttachment, mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType);
        }

        /**
         * show vote layout for channel otherwise hide layout also get message state for channel
         */

        mHolder.lyt_vote.setVisibility(View.GONE);
        mHolder.lyt_see.setVisibility(View.GONE);

        if ((type == ProtoGlobal.Room.Type.CHANNEL)) {
            showVote(holder, getRealmChat());
        } else if ((type == ProtoGlobal.Room.Type.CHAT)) {
            if (mMessage.forwardedFrom != null) {
                if (mMessage.forwardedFrom.getAuthorRoomId() > 0) {
                    if (realmRoomForwardedFrom != null && realmRoomForwardedFrom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                        showVote(holder, getRealmChat());

                        if (mMessage.isSenderMe()) {
                            mHolder.cslm_view_left_dis.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }

    }

    /**
     * show vote views
     */
    private void showVote(VH holder, Realm realm) {
        // add layout seen in channel
        ((ChatItemHolder) holder).lyt_see.setVisibility(View.VISIBLE);
        voteAction(((ChatItemHolder) holder), getRealmChat());
        getChannelMessageState();
    }

    /**
     * get channel message state, for clear unread message in channel client
     * need send request for getMessageState even show vote layout is hide
     */
    private void getChannelMessageState() {
        if ((mMessage.forwardedFrom != null)) {
            ProtoGlobal.Room.Type roomType = null;
            if (realmRoomForwardedFrom != null) {
                roomType = realmRoomForwardedFrom.getType();
            }
            if ((mMessage.forwardedFrom != null) && (roomType == ProtoGlobal.Room.Type.CHANNEL)) {
                /**
                 * if roomType is Channel don't consider forward
                 *
                 * when i add message to RealmRoomMessage(putOrUpdate) set (replyMessageId * (-1))
                 * so i need to (replyMessageId * (-1)) again for use this messageId
                 */
                long messageId = mMessage.forwardedFrom.getMessageId();
                if (mMessage.forwardedFrom.getMessageId() < 0) {
                    messageId = messageId * (-1);
                }
                HelperGetMessageState.getMessageState(mMessage.forwardedFrom.getAuthorRoomId(), messageId);
            } else {
                HelperGetMessageState.getMessageState(mMessage.roomId, Long.parseLong(mMessage.messageID));
            }
        } else {
            HelperGetMessageState.getMessageState(mMessage.roomId, Long.parseLong(mMessage.messageID));
        }
    }

    private void addSenderNameToGroupIfNeed(final ChatItemHolder holder, Realm realm) {

        if (G.showSenderNameInGroup) {
            View messageSenderName = holder.m_container.findViewById(R.id.messageSenderName);
            if (messageSenderName != null) {
                holder.m_container.removeView(messageSenderName);
            }

            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), Long.parseLong(mMessage.senderID));
            if (realmRegisteredInfo != null) {
                final EmojiTextViewE _tv = (EmojiTextViewE) ViewMaker.makeHeaderTextView(realmRegisteredInfo.getDisplayName());

                //_tv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                //    @Override
                //    public void onGlobalLayout() {
                //        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                //            _tv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //        } else {
                //            _tv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //        }
                //
                //        if (_tv.getWidth() < mContainer.getWidth()) {
                //            _tv.setWidth(mContainer.getWidth());
                //        }
                //    }
                //});

                _tv.measure(0, 0);       //must call measure!
                int maxWith = 0;
                maxWith = _tv.getMeasuredWidth() + i_Dp(R.dimen.dp40);

                if (minWith < maxWith) {
                    minWith = maxWith;
                }
                holder.m_container.setMinimumWidth(Math.min(minWith, G.maxChatBox));
                holder.m_container.addView(_tv, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    protected View.OnLongClickListener getLongClickPerform(final RecyclerView.ViewHolder holder){
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.itemView.performLongClick();
                return true;
            }
        };
    }

    protected void voteAction(ChatItemHolder mHolder, Realm realm) {
        boolean showThump = G.showVoteChannelLayout && messageClickListener.getShowVoteChannel();
        if (showThump) {
            mHolder.lyt_vote.setVisibility(View.VISIBLE);
        } else {
            mHolder.lyt_vote.setVisibility(View.INVISIBLE);
        }

        /**
         * userId != 0 means that this message is from channel
         * because for chat and group userId will be set
         */

        if ((mMessage.forwardedFrom != null)) {
            if (realmRoomForwardedFrom != null && realmRoomForwardedFrom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                 if (realmChannelExtra != null) {
                    mHolder.txt_vote_up.setText(realmChannelExtra.getThumbsUp());
                    mHolder.txt_vote_down.setText(realmChannelExtra.getThumbsDown());
                    mHolder.txt_views_label.setText(realmChannelExtra.getViewsLabel());
                    mHolder.txt_signature.setText(realmChannelExtra.getSignature());
                }
            } else {
                mHolder.txt_vote_up.setText(mMessage.channelExtra.thumbsUp);
                mHolder.txt_vote_down.setText(mMessage.channelExtra.thumbsDown);
                mHolder.txt_views_label.setText(mMessage.channelExtra.viewsLabel);
                mHolder.txt_signature.setText(mMessage.channelExtra.signature);
            }
        } else {
            mHolder.txt_vote_up.setText(mMessage.channelExtra.thumbsUp);
            mHolder.txt_vote_down.setText(mMessage.channelExtra.thumbsDown);
            mHolder.txt_views_label.setText(mMessage.channelExtra.viewsLabel);
            mHolder.txt_signature.setText(mMessage.channelExtra.signature);
        }

        if (mHolder.txt_signature.getText().length() > 0) {
            mHolder.lyt_signature.setVisibility(View.VISIBLE);
        }

        if (HelperCalander.isPersianUnicode) {
            mHolder.txt_views_label.setText(HelperCalander.convertToUnicodeFarsiNumber(mHolder.txt_views_label.getText().toString()));
            mHolder.txt_vote_down.setText(HelperCalander.convertToUnicodeFarsiNumber(mHolder.txt_vote_down.getText().toString()));
            mHolder.txt_vote_up.setText(HelperCalander.convertToUnicodeFarsiNumber(mHolder.txt_vote_up.getText().toString()));
        }

        mHolder.lyt_vote_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FragmentChat.isInSelectionMode) {
                    mHolder.itemView.performLongClick();
                } else {
                    voteSend(ProtoGlobal.RoomMessageReaction.THUMBS_UP);
                }
            }
        });

        mHolder.lyt_vote_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FragmentChat.isInSelectionMode) {
                    mHolder.itemView.performLongClick();
                } else {
                    voteSend(ProtoGlobal.RoomMessageReaction.THUMBS_DOWN);
                }
            }
        });
    }

    /**
     * send vote action to RealmRoomMessage
     *
     * @param reaction Up or Down
     */
    private void voteSend(final ProtoGlobal.RoomMessageReaction reaction) {

        getRealmChat().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID)).findFirst();
                if (realmRoomMessage != null) {
                    /**
                     * userId != 0 means that this message is from channel
                     * because for chat and group userId will be set
                     */

                    if ((mMessage.forwardedFrom != null)) {
                        ProtoGlobal.Room.Type roomType = null;
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getAuthorRoomId()).findFirst();
                        if (realmRoom != null) {
                            roomType = realmRoom.getType();
                        }
                        if ((roomType == ProtoGlobal.Room.Type.CHANNEL)) {
                            long forwardMessageId = mMessage.forwardedFrom.getMessageId();
                            /**
                             * check with this number for detect is multiply now or no
                             * hint : use another solution
                             */
                            if (mMessage.forwardedFrom.getMessageId() < 0) {
                                forwardMessageId = forwardMessageId * (-1);
                            }
                            new RequestChannelAddMessageReaction().channelAddMessageReactionForward(mMessage.forwardedFrom.getAuthorRoomId(), Long.parseLong(mMessage.messageID), reaction, forwardMessageId);
                        } else {
                            new RequestChannelAddMessageReaction().channelAddMessageReaction(mMessage.roomId, Long.parseLong(mMessage.messageID), reaction);
                        }
                    } else {
                        new RequestChannelAddMessageReaction().channelAddMessageReaction(mMessage.roomId, Long.parseLong(mMessage.messageID), reaction);
                    }
                }
            }
        });
    }

    @CallSuper
    protected void updateLayoutForReceive(VH holder) {
        ChatItemHolder mHolder;
        if (holder instanceof ChatItemHolder)
            mHolder = (ChatItemHolder) holder;
        else
            return;

        LinearLayout timeLayout = (LinearLayout) mHolder.contentContainer.getParent();
        timeLayout.setGravity(Gravity.LEFT);

        if (holder instanceof ChatItemWithTextHolder) {
            ((ChatItemWithTextHolder) holder).messageView.setTextColor(Color.parseColor(G.textBubble));
        }
        //   ProtoGlobal.RoomMessageType messageType = mMessage.forwardedFrom == null ? mMessage.messageType : mMessage.forwardedFrom.getMessageType();

        ((FrameLayout.LayoutParams) mHolder.mainContainer.getLayoutParams()).gravity = Gravity.LEFT;

        ((LinearLayout.LayoutParams) mHolder.contentContainer.getLayoutParams()).gravity = Gravity.LEFT;

        if (G.isDarkTheme) {
            setTextColor(mHolder.cslr_txt_tic, R.color.white);
            ((View) (mHolder.contentContainer).getParent()).setBackgroundResource(R.drawable.rectangel_white_round_dark);
        } else {
            setTextColor(mHolder.cslr_txt_tic, R.color.colorOldBlack);
            ((View) (mHolder.contentContainer).getParent()).setBackgroundResource(R.drawable.rectangel_white_round);
        }

        /**
         * add main layout margin to prevent getting match parent completely
         * set to mainContainer not itemView because of selecting item foreground
         */

        GradientDrawable circleDarkColor = (GradientDrawable) ((View) mHolder.contentContainer.getParent()).getBackground();
        circleDarkColor.setColor(Color.parseColor(G.bubbleChatReceive));

        ((FrameLayout.LayoutParams) mHolder.mainContainer.getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp10);
        ((FrameLayout.LayoutParams) mHolder.mainContainer.getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp28);
    }

    private void setTextColor(ImageView imageView, int color) {

        try {
            imageView.setColorFilter(ContextCompat.getColor(G.context, color));
        } catch (NullPointerException e) {
            // imageView.setColorFilter(color,android.graphics.PorterDuff.Mode.MULTIPLY);
            try {
                imageView.setColorFilter(G.context.getResources().getColor(color));
            } catch (Exception e1) {
            }
        }
    }

    @CallSuper
    protected void updateLayoutForSend(VH holder) {
        ChatItemHolder mHolder;
        if (holder instanceof ChatItemHolder)
            mHolder = (ChatItemHolder) holder;
        else
            return;
        ((FrameLayout.LayoutParams) mHolder.mainContainer.getLayoutParams()).gravity = Gravity.RIGHT;

        ((LinearLayout.LayoutParams) mHolder.contentContainer.getLayoutParams()).gravity = Gravity.RIGHT;

        LinearLayout timeLayout = (LinearLayout) mHolder.contentContainer.getParent();
        timeLayout.setGravity(Gravity.RIGHT);

        //  TextView iconHearing = (TextView) holder.itemView.findViewById(R.id.cslr_txt_hearing);

        if (holder instanceof ChatItemWithTextHolder) {
            ((ChatItemWithTextHolder) holder).messageView.setTextColor(Color.parseColor(G.textBubble));
        }
        //   ProtoGlobal.RoomMessageType messageType = mMessage.forwardedFrom == null ? mMessage.messageType : mMessage.forwardedFrom.getMessageType();


        ProtoGlobal.RoomMessageStatus status = ProtoGlobal.RoomMessageStatus.UNRECOGNIZED;
        if (mMessage.status != null) {
            try {
                status = ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        if (status == ProtoGlobal.RoomMessageStatus.SEEN) {
            mHolder.cslr_txt_tic.setColorFilter(Color.parseColor(G.SeenTickColor));

        } else if (status == ProtoGlobal.RoomMessageStatus.LISTENED) {
            // iconHearing.setVisibility(View.VISIBLE);
            if (G.isDarkTheme) {
                setTextColor(mHolder.cslr_txt_tic, R.color.iGapColor);
            } else {
                setTextColor(mHolder.cslr_txt_tic, R.color.backgroundColorCall2);
            }

            mHolder.cslr_txt_tic.setVisibility(View.VISIBLE);
        } else {
//            setTextColor(imgTick, Color.parseColor(G.txtIconCheck));
            mHolder.cslr_txt_tic.setColorFilter(Color.parseColor(G.txtIconCheck));
        }


        if (G.isDarkTheme) {
            ((View) (mHolder.contentContainer).getParent()).setBackgroundResource(R.drawable.rectangle_send_round_color_dark);
        } else {
            ((View) (mHolder.contentContainer).getParent()).setBackgroundResource(R.drawable.rectangle_send_round_color);
        }
        GradientDrawable circleDarkColor = (GradientDrawable) ((View) mHolder.contentContainer.getParent()).getBackground();
        circleDarkColor.setColor(Color.parseColor(G.bubbleChatSend));

        /**
         * add main layout margin to prevent getting match parent completely
         * set to mainContainer not itemView because of selecting item foreground
         */
        ((FrameLayout.LayoutParams) mHolder.mainContainer.getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp28);
        ((FrameLayout.LayoutParams) mHolder.mainContainer.getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp10);

        //((LinearLayout.LayoutParams) (holder.itemView.findViewById(R.id.contentContainer).getLayoutParams())).rightMargin = (int) holder.itemView.getResources().getDimension(R.dimen.messageBox_minusLeftRightMargin);
        //((LinearLayout.LayoutParams) (holder.itemView.findViewById(R.id.contentContainer).getLayoutParams())).leftMargin = 0;
    }

    @CallSuper
    protected void replyMessageIfNeeded(VH holder, Realm realm) {
        ChatItemHolder mHolder;
        if (holder instanceof ChatItemHolder)
            mHolder = (ChatItemHolder) holder;
        else
            return;

        mHolder.m_container.setMinimumWidth(0);
        mHolder.m_container.setMinimumHeight(0);

        /**
         * set replay container visible if message was replayed, otherwise, gone it
         */
        View cslr_replay_layout = ((ChatItemHolder) holder).m_container.findViewById(R.id.cslr_replay_layout);

        if (cslr_replay_layout != null) {
            mHolder.m_container.removeView(cslr_replay_layout);
        }

        if (mMessage.replayTo != null && mMessage.replayTo.isValid()) {

            final View replayView = ViewMaker.getViewReplay();

            if (replayView != null) {
                final TextView replyFrom = (TextView) replayView.findViewById(R.id.chslr_txt_replay_from);
                final EmojiTextViewE replayMessage = (EmojiTextViewE) replayView.findViewById(R.id.chslr_txt_replay_message);
                replayView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FragmentChat.isInSelectionMode) {
                            holder.itemView.performLongClick();
                            return;
                        }

                        messageClickListener.onReplyClick(mMessage.replayTo);
                    }
                });

                replayView.setOnLongClickListener(getLongClickPerform(holder));

                try {
                    AppUtils.rightFileThumbnailIcon(((ImageView) replayView.findViewById(R.id.chslr_imv_replay_pic)), mMessage.replayTo.getForwardMessage() == null ? mMessage.replayTo.getMessageType() : mMessage.replayTo.getForwardMessage().getMessageType(), mMessage.replayTo.getForwardMessage() == null ? mMessage.replayTo : mMessage.replayTo.getForwardMessage());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                if (type == ProtoGlobal.Room.Type.CHANNEL) {
                    if (realmRoom != null) {
                        replyFrom.setText(realmRoom.getTitle());
                    }
                } else {
                    RealmRegisteredInfo replayToInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), mMessage.replayTo.getUserId());
                    if (replayToInfo != null) {
                        replyFrom.setText(replayToInfo.getDisplayName());
                    }
                }

                String forwardMessage = AppUtils.replyTextMessage(mMessage.replayTo, holder.itemView.getResources());
                ((EmojiTextViewE) replayView.findViewById(R.id.chslr_txt_replay_message)).setText(forwardMessage);

                if (mMessage.isSenderMe() && type != ProtoGlobal.Room.Type.CHANNEL) {

                    replayView.setBackgroundResource(R.drawable.rectangle_reply_sender_round_color);
//
                    GradientDrawable circleDarkColor = (GradientDrawable) replayView.getBackground();
                    circleDarkColor.setColor(Color.parseColor(G.backgroundTheme_2));

                    replyFrom.setTextColor(Color.parseColor(G.textBubble));
                    replayMessage.setTextColor(Color.parseColor(G.textBubble));

                } else {

                    replayView.setBackgroundResource(R.drawable.rectangle_reply_recive_round_color);

                    GradientDrawable circleDarkColor = (GradientDrawable) replayView.getBackground();
                    circleDarkColor.setColor(Color.parseColor(G.backgroundTheme_2));

                    replyFrom.setTextColor(Color.parseColor(G.textBubble));
                    replayMessage.setTextColor(Color.parseColor(G.textBubble));

                }

                replyFrom.measure(0, 0);       //must call measure!
                replayMessage.measure(0, 0);

                int maxWith, withMessage, withTitle;
                withTitle = replyFrom.getMeasuredWidth();
                withMessage = replayMessage.getMeasuredWidth();
                maxWith = withTitle > withMessage ? withTitle : withMessage;
                maxWith += i_Dp(R.dimen.dp44);
                if (replayView.findViewById(R.id.chslr_imv_replay_pic).getVisibility() == View.VISIBLE) {
                    maxWith += i_Dp(R.dimen.dp52);
                }

                minWith = maxWith;
                mHolder.m_container.setMinimumWidth(Math.min(minWith, G.maxChatBox));

                mHolder.m_container.addView(replayView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                replayMessage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                replyFrom.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    @CallSuper
    protected void forwardMessageIfNeeded(VH holder, Realm realm) {
        ChatItemHolder mHolder;
        if (holder instanceof ChatItemHolder)
            mHolder = (ChatItemHolder) holder;
        else
            return;
        /**
         * set forward container visible if message was forwarded, otherwise, gone it
         */
        View cslr_ll_forward22 = ((ChatItemHolder) holder).m_container.findViewById(R.id.cslr_ll_forward);
        if (cslr_ll_forward22 != null) {
            mHolder.m_container.removeView(cslr_ll_forward22);
        }

        if (mMessage.forwardedFrom != null) {

            View forwardView = ViewMaker.getViewForward();
            forwardView.setOnLongClickListener(getLongClickPerform(holder));

            forwardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FragmentChat.isInSelectionMode) {
                        holder.itemView.performLongClick();
                    } else {
                        if (mMessage.username.length() > 0) {
                            HelperUrl.checkUsernameAndGoToRoomWithMessageId(mMessage.username, HelperUrl.ChatEntry.profile, (mMessage.forwardedFrom.getMessageId() * (-1)));
                        }
                    }
                }
            });

            TextView txtPrefixForwardFrom = (TextView) forwardView.findViewById(R.id.cslr_txt_prefix_forward);
            txtPrefixForwardFrom.setTypeface(G.typeface_IRANSansMobile);
            TextView txtForwardFrom = (TextView) forwardView.findViewById(R.id.cslr_txt_forward_from);
            txtForwardFrom.setTypeface(G.typeface_IRANSansMobile);

            /**
             * if forward message from chat or group , sender is user
             * but if message forwarded from channel sender is room
             */
            RealmRegisteredInfo info = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), mMessage.forwardedFrom.getUserId());
            if (info != null) {

                if (RealmRegisteredInfo.needUpdateUser(info.getId(), info.getCacheId())) {
                    if (!updateForwardInfo.containsKey(info.getId())) {
                        updateForwardInfo.put(info.getId(), mMessage.messageID);
                    }
                }

                txtForwardFrom.setText(info.getDisplayName());
                mMessage.username = info.getUsername();
                if (mMessage.isSenderMe()) {
                    txtForwardFrom.setTextColor(G.context.getResources().getColor(R.color.iGapColor));
                } else {
                    txtForwardFrom.setTextColor(G.context.getResources().getColor(R.color.iGapColor));
                }
            } else if (mMessage.forwardedFrom.getUserId() != 0) {

                if (RealmRegisteredInfo.needUpdateUser(mMessage.forwardedFrom.getUserId(), null)) {
                    if (!updateForwardInfo.containsKey(mMessage.forwardedFrom.getUserId())) {
                        updateForwardInfo.put(mMessage.forwardedFrom.getUserId(), mMessage.messageID);
                    }
                }
            } else {
                RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getRoomId()).findFirst();
                if (realmRoom != null) {
                    txtForwardFrom.setText(realmRoom.getTitle());
                    if (mMessage.isSenderMe()) {
                        txtForwardFrom.setTextColor(Color.parseColor(G.textBubble));
                    } else {
                        txtForwardFrom.setTextColor(G.context.getResources().getColor(R.color.iGapColor));
                    }

                    switch (realmRoom.getType()) {
                        case CHANNEL:
                            mMessage.username = realmRoom.getChannelRoom().getUsername();
                            break;
                        case GROUP:
                            mMessage.username = realmRoom.getGroupRoom().getUsername();
                            break;
                    }
                } else {
                    if (realmRoomForwardedFrom != null) {

                        switch (realmRoomForwardedFrom.getType()) {
                            case CHANNEL:
                                if (realmRoomForwardedFrom.getChannelRoom() != null && realmRoomForwardedFrom.getChannelRoom().getUsername() != null) {
                                    mMessage.username = realmRoomForwardedFrom.getChannelRoom().getUsername();
                                } else {
                                    mMessage.username = holder.itemView.getResources().getString(R.string.private_channel);
                                }

                                break;
                            case GROUP:
                                mMessage.username = realmRoomForwardedFrom.getGroupRoom().getUsername();
                                break;
                        }

                        if (RealmRoom.needUpdateRoomInfo(realmRoomForwardedFrom.getId())) {
                            if (!updateForwardInfo.containsKey(realmRoomForwardedFrom.getId())) {
                                updateForwardInfo.put(realmRoomForwardedFrom.getId(), mMessage.messageID);
                            }
                        }

                        txtForwardFrom.setText(realmRoomForwardedFrom.getTitle());
                        if (mMessage.isSenderMe()) {
                            txtForwardFrom.setTextColor(Color.parseColor(G.textBubble));
                        } else {
                            txtForwardFrom.setTextColor(G.context.getResources().getColor(R.color.iGapColor));
                        }
                    } else {
                        if (RealmRoom.needUpdateRoomInfo(mMessage.forwardedFrom.getAuthorRoomId())) {
                            if (!updateForwardInfo.containsKey(mMessage.forwardedFrom.getAuthorRoomId())) {
                                updateForwardInfo.put(mMessage.forwardedFrom.getAuthorRoomId(), mMessage.messageID);
                            }
                        }
                    }
                }
            }

            txtPrefixForwardFrom.measure(0, 0);       //must call measure!
            txtForwardFrom.measure(0, 0);
            int maxWith = txtPrefixForwardFrom.getMeasuredWidth() + txtForwardFrom.getMeasuredWidth() + i_Dp(R.dimen.dp32);

            if (minWith < maxWith) {
                minWith = maxWith;
            }
            mHolder.m_container.setMinimumWidth(Math.min(minWith, G.maxChatBox));
            mHolder.m_container.addView(forwardView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * does item have progress view
     *
     * @param holder holder
     * @return true if item has a progress
     */
    private boolean hasProgress(VH holder) {
        if (holder instanceof IProgress){
            MessageProgress _Progress = ((IProgress) holder).getProgress();
            _Progress.setTag(mMessage.messageID);
            _Progress.setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    }

    private void setClickListener(SharedPreferences sharedPreferences, String key, final VH holder, final RealmAttachment attachment) {

        /**
         * if type was gif auto file start auto download
         */
        if (sharedPreferences.getInt(key, ((key.equals(SHP_SETTING.KEY_AD_DATA_GIF) || key.equals(SHP_SETTING.KEY_AD_WIFI_GIF)) ? 5 : -1)) != -1) {
            autoDownload(holder, attachment);
        } else {

            MessageProgress _Progress = ((IProgress) holder).getProgress();
            AppUtils.setProgresColor(_Progress.progressBar);

            _Progress.withOnMessageProgress(new OnMessageProgressClick() {
                @Override
                public void onMessageProgressClick(MessageProgress progress) {
                    forOnCLick(holder, attachment);
                }
            });
        }
    }

    private void checkAutoDownload(final VH holder, final RealmAttachment attachment, Context context, HelperCheckInternetConnection.ConnectivityType connectionMode) {

        if (HelperDownloadFile.getInstance().manuallyStoppedDownload.contains(attachment.getCacheId())) { // for avoid from reDownload in autoDownload state , after that user manually stopped download.
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        ProtoGlobal.RoomMessageType messageType;
        if (mMessage.forwardedFrom != null) {
            messageType = mMessage.forwardedFrom.getMessageType();
        } else {
            messageType = mMessage.messageType;
        }
        switch (messageType) {
            case IMAGE:
            case IMAGE_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_PHOTO, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_PHOTO, holder, attachment);
                        break;
                }
                break;
            case VOICE:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, holder, attachment);
                        break;
                }
                break;
            case VIDEO:
            case VIDEO_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_VIDEO, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_VIDEO, holder, attachment);
                        break;
                }
                break;
            case FILE:
            case FILE_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_FILE, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_FILE, holder, attachment);
                        break;
                }
                break;
            case AUDIO:
            case AUDIO_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_MUSIC, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_MUSIC, holder, attachment);
                        break;
                }
                break;
            case GIF:
            case GIF_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_GIF, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_GIF, holder, attachment);
                        break;
                }
                break;
            default:

                MessageProgress _Progress = ((IProgress) holder).getProgress();
                AppUtils.setProgresColor(_Progress.progressBar);

                _Progress.withOnMessageProgress(new OnMessageProgressClick() {
                    @Override
                    public void onMessageProgressClick(MessageProgress progress) {
                        forOnCLick(holder, attachment);
                    }
                });
                break;
        }
    }

    private void prepareAttachmentIfNeeded(final VH holder, final RealmAttachment attachment, final ProtoGlobal.RoomMessageType messageType) {
        /**
         * runs if message has attachment
         */
        ChatItemHolder mHolder;
        if (holder instanceof ChatItemHolder)
            mHolder = (ChatItemHolder) holder;
        else
            return;

        if (attachment != null) {

            if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT || messageType == ProtoGlobal.RoomMessageType.VIDEO || messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                ReserveSpaceRoundedImageView imageViewReservedSpace = (ReserveSpaceRoundedImageView) ((IThumbNailItem) holder).getThumbNailImageView();
                int _with = attachment.getWidth();
                int _hight = attachment.getHeight();

                if (_with == 0) {
                    if (attachment.getSmallThumbnail() != null) {
                        _with = attachment.getSmallThumbnail().getWidth();
                        _hight = attachment.getSmallThumbnail().getHeight();
                    }
                }

                boolean setDefualtImage = false;

                if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                    if (attachment.getLocalFilePath() == null && attachment.getLocalThumbnailPath() == null && _with == 0) {
                        _with = (int) G.context.getResources().getDimension(R.dimen.dp120);
                        _hight = (int) G.context.getResources().getDimension(R.dimen.dp120);
                        setDefualtImage = true;
                    }
                } else {
                    if (attachment.getLocalThumbnailPath() == null && _with == 0) {
                        _with = (int) G.context.getResources().getDimension(R.dimen.dp120);
                        _hight = (int) G.context.getResources().getDimension(R.dimen.dp120);
                        setDefualtImage = true;
                    }
                }

                int[] dimens = imageViewReservedSpace.reserveSpace(_with, _hight, type);
                if (dimens[0] != 0 && dimens[1] != 0) {
                    mHolder.m_container.getLayoutParams().width = dimens[0];
                }

                if (setDefualtImage) {
                    imageViewReservedSpace.setImageResource(R.mipmap.difaultimage);
                }
            } else if (messageType == ProtoGlobal.RoomMessageType.GIF || messageType == ProtoGlobal.RoomMessageType.GIF_TEXT) {
                ReserveSpaceGifImageView imageViewReservedSpace = (ReserveSpaceGifImageView) ((IThumbNailItem) holder).getThumbNailImageView();
                int _with = attachment.getWidth();
                int _hight = attachment.getHeight();

                if (_with == 0) {
                    _with = (int) G.context.getResources().getDimension(R.dimen.dp200);
                    _hight = (int) G.context.getResources().getDimension(R.dimen.dp200);
                }

                int[] dimens = imageViewReservedSpace.reserveSpace(_with, _hight, type);
                ((ViewGroup) mHolder.m_container).getLayoutParams().width = dimens[0];
            }

            /**
             * if file already exists, simply show the local one
             */
            if (attachment.isFileExistsOnLocalAndIsThumbnail()) {
                /**
                 * load file from local
                 */
                onLoadThumbnailFromLocal(holder, getCacheId(mMessage), attachment.getLocalFilePath(), LocalFileType.FILE);
            } else if (messageType == ProtoGlobal.RoomMessageType.VOICE || messageType == ProtoGlobal.RoomMessageType.AUDIO || messageType == ProtoGlobal.RoomMessageType.AUDIO_TEXT) {
                onLoadThumbnailFromLocal(holder, getCacheId(mMessage), attachment.getLocalFilePath(), LocalFileType.FILE);
            } else {
                /**
                 * file doesn't exist on local, I check for a thumbnail
                 * if thumbnail exists, I load it into the view
                 */
                if (attachment.isThumbnailExistsOnLocal()) {
                    /**
                     * load thumbnail from local
                     */
                    onLoadThumbnailFromLocal(holder, getCacheId(mMessage), attachment.getLocalThumbnailPath(), LocalFileType.THUMBNAIL);
                } else {
                    if (messageType != ProtoGlobal.RoomMessageType.CONTACT) {
                        downLoadThumbnail(holder, attachment);
                    }
                }
            }

            if (hasProgress(holder)) {

                final MessageProgress _Progress = ((IProgress) holder).getProgress();
                AppUtils.setProgresColor(_Progress.progressBar);

                _Progress.withOnMessageProgress(new OnMessageProgressClick() {
                    @Override
                    public void onMessageProgressClick(MessageProgress progress) {
                        forOnCLick(holder, attachment);
                    }
                });

                if (!attachment.isFileExistsOnLocal()) {
                    if (HelperCheckInternetConnection.currentConnectivityType == null) {
                        checkAutoDownload(holder, attachment, holder.itemView.getContext(), HelperCheckInternetConnection.ConnectivityType.WIFI);
                        checkAutoDownload(holder, attachment, holder.itemView.getContext(), HelperCheckInternetConnection.ConnectivityType.MOBILE);
                    } else {
                        checkAutoDownload(holder, attachment, holder.itemView.getContext(), HelperCheckInternetConnection.currentConnectivityType);
                    }
                }

                _Progress.withOnProgress(new OnProgress() {
                    @Override
                    public void onProgressFinished() {

                        if (_Progress.getTag() == null || !_Progress.getTag().equals(mMessage.messageID)) {
                            return;
                        }
                        _Progress.setVisibility(View.GONE);
                        View thumbnailView = ((IThumbNailItem) holder).getThumbNailImageView();
                        thumbnailView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (FragmentChat.isInSelectionMode) {
                                    holder.itemView.performLongClick();
                                }
                            }
                        });

                        thumbnailView.setOnLongClickListener(getLongClickPerform(holder));

                        _Progress.withDrawable(null, true);

                        switch (messageType) {
                            case VIDEO:
                            case VIDEO_TEXT:
                                ((IProgress) holder).getProgress().setVisibility(View.VISIBLE);
                                _Progress.withDrawable(R.drawable.ic_play, true);
                                break;
                            case AUDIO:
                            case AUDIO_TEXT:
                                break;
                            case FILE:
                            case FILE_TEXT:
                            case IMAGE:
                            case IMAGE_TEXT:
                                thumbnailView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        forOnCLick(holder, attachment);
                                    }
                                });
                                break;
                            case VOICE:
                                break;
                            case GIF:
                            case GIF_TEXT:
                                thumbnailView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        forOnCLick(holder, attachment);
                                    }
                                });

                                SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                if (sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS) == 0) {
                                    ((IProgress) holder).getProgress().setVisibility(View.VISIBLE);
                                    _Progress.withDrawable(R.mipmap.photogif, true);
                                } else {
                                    ((IProgress) holder).getProgress().setVisibility(View.INVISIBLE);
                                }
                                break;
                        }
                    }
                });

                prepareProgress(holder, attachment);
            }

        }
    }


    private void autoDownload(VH holder, RealmAttachment attachment) {
        if (mMessage.messageType == ProtoGlobal.RoomMessageType.FILE || mMessage.messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            ((IThumbNailItem) holder).getThumbNailImageView().setVisibility(View.INVISIBLE);
        }

        downLoadFile(holder, attachment, 0);
    }

    public boolean hasFileSize(String filPath) {
        if (filPath != null) {
            File file = new File(filPath);
            if (file.exists() && file.length() > 0) {
                return true;
            }
        }
        return false;
    }

    private void forOnCLick(VH holder, RealmAttachment attachment) {

        if (FragmentChat.isInSelectionMode) {
            holder.itemView.performLongClick();
            return;
        }

        final MessageProgress progress = ((IProgress) holder).getProgress();
        AppUtils.setProgresColor(progress.progressBar);

        View thumbnail = ((IThumbNailItem) holder).getThumbNailImageView();

        //if (mMessage.messageType == ProtoGlobal.RoomMessageType.FILE || mMessage.messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
        //    if (thumbnail != null) {
        //        thumbnail.setVisibility(View.INVISIBLE);
        //    }
        //}

        if (HelperUploadFile.isUploading(mMessage.messageID)) {
            if (mMessage.status.equals(ProtoGlobal.RoomMessageStatus.FAILED.toString()) && hasFileSize(attachment.getLocalFilePath())) {
                if (G.userLogin) {
                    messageClickListener.onFailedMessageClick(progress, mMessage, holder.getAdapterPosition());

                    //HelperUploadFile.reUpload(mMessage.messageID);
                    //progress.withDrawable(R.drawable.ic_cancel, false);
                    //holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);

                } else {
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                }
            } else {
                messageClickListener.onUploadOrCompressCancel(progress, mMessage, holder.getAdapterPosition(), SendingStep.UPLOADING);
            }
        } else if (HelperDownloadFile.getInstance().isDownLoading(attachment.getCacheId())) {
            HelperDownloadFile.getInstance().stopDownLoad(attachment.getCacheId());
        } else {
            thumbnail.setVisibility(View.VISIBLE);


            if (attachment.isFileExistsOnLocal()) {
                String _status = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getStatus() : mMessage.status;
                ProtoGlobal.RoomMessageType _type = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType;

                if (_status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                    messageClickListener.onFailedMessageClick(progress, mMessage, holder.getAdapterPosition());
                } else if (FragmentChat.compressingFiles.containsKey(Long.parseLong(mMessage.messageID))) {
                    messageClickListener.onUploadOrCompressCancel(progress, mMessage, holder.getAdapterPosition(), SendingStep.COMPRESSING);
                } else if (_status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                    messageClickListener.onUploadOrCompressCancel(progress, mMessage, holder.getAdapterPosition(), SendingStep.CORRUPTED_FILE);
                } else {
                    /**
                     * avoid from show GIF in fragment show image
                     */
                    if (_type == ProtoGlobal.RoomMessageType.GIF || _type == ProtoGlobal.RoomMessageType.GIF_TEXT) {
                        try {
                            onPlayPauseGIF(holder, attachment.getLocalFilePath());
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                    } else {
                        progress.performProgress();
                        messageClickListener.onOpenClick(progress, mMessage, holder.getAdapterPosition());
                    }
                }
            } else {
                downLoadFile(holder, attachment, 2);
            }
        }
    }

    @Override
    @CallSuper
    public void onLoadThumbnailFromLocal(VH holder, String tag, String localPath, LocalFileType fileType) {

    }

    private void downLoadThumbnail(final VH holder, final RealmAttachment attachment) {

        if (attachment == null) return;

        String token = attachment.getToken();
        String url = attachment.getUrl();
        String name = attachment.getName();

        long size = 0;

        if (attachment.getSmallThumbnail() != null) size = attachment.getSmallThumbnail().getSize();

        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;

        if (attachment.getCacheId() == null || attachment.getCacheId().length() == 0) {
            return;
        }

        //  final String _path = AndroidUtils.getFilePathWithCashId(attachment.getCacheId(), name, G.DIR_TEMP, true);

        if (token != null && token.length() > 0 && size > 0) {

            HelperDownloadFile.getInstance().startDownload(mMessage.messageType, mMessage.messageID, token, url, attachment.getCacheId(), name, size, selector, "", 4, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(final String path, int progress) {

                    if (FragmentChat.canUpdateAfterDownload) {
                        if (progress == 100) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String type;
                                    if (mMessage.forwardedFrom != null) {
                                        type = mMessage.forwardedFrom.getMessageType().toString().toLowerCase();
                                    } else {
                                        type = mMessage.messageType.toString().toLowerCase();
                                    }
                                    if (type.contains("image") || type.contains("video") || type.contains("gif")) {
                                        onLoadThumbnailFromLocal(holder, attachment.getCacheId(), path, LocalFileType.THUMBNAIL);
                                    }
                                }
                            });
                        }
                    }
                }

                @Override
                public void OnError(String token) {
                }
            });
        }
    }

    void downLoadFile(final VH holder, final RealmAttachment attachment, int priority) {

        if (attachment == null || attachment.getCacheId() == null) {
            return;
        }

        boolean _isDownloading = HelperDownloadFile.getInstance().isDownLoading(attachment.getCacheId());

        final MessageProgress progressBar = ((IProgress) holder).getProgress();
        AppUtils.setProgresColor(progressBar.progressBar);


        final String token = attachment.getToken();
        final String url = attachment.getUrl();
        String name = attachment.getName();
        Long size = attachment.getSize();
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

        final ProtoGlobal.RoomMessageType messageType = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType;

        final String _path = AndroidUtils.getFilePathWithCashId(attachment.getCacheId(), name, messageType);

        if (token != null && token.length() > 0 && size > 0) {

            progressBar.setVisibility(View.VISIBLE);
            progressBar.withDrawable(R.drawable.ic_cancel, false);


            HelperDownloadFile.getInstance().startDownload(messageType, mMessage.messageID, token, url, attachment.getCacheId(), name, size, selector, _path, priority, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(final String path, final int progress) {

                    if (FragmentChat.canUpdateAfterDownload) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {
                                    progressBar.withProgress(progress);

                                    if (progress == 100) {

                                        if (messageType == ProtoGlobal.RoomMessageType.AUDIO || messageType == ProtoGlobal.RoomMessageType.AUDIO_TEXT || messageType == ProtoGlobal.RoomMessageType.VOICE) {
                                            if (mMessage.roomId == MusicPlayer.roomId) {
                                                MusicPlayer.downloadNewItem = true;
                                            }
                                        }
                                        onLoadThumbnailFromLocal(holder, attachment.getCacheId(), path, LocalFileType.FILE);
                                    }
                                }
                            }
                        });
                    }

                }

                @Override
                public void OnError(String token) {

                    if (FragmentChat.canUpdateAfterDownload) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {
                                    progressBar.withProgress(0);
                                    progressBar.withDrawable(R.drawable.ic_download, true);

                                }
                            }
                        });
                    }


                }
            });

            if (!_isDownloading) {
                messageClickListener.onDownloadAllEqualCashId(attachment.getCacheId(), mMessage.messageID);
            }
        }
    }

    public void updateProgress(OnProgressUpdate onProgressUpdate) {
        onProgressUpdate.onProgressUpdate();
    }

    /**
     * automatically update progress if layout has one
     *
     * @param holder VH
     */
    private void prepareProgress(final VH holder, RealmAttachment attachment) {
        if (mMessage.sendType == MyType.SendType.send) {

            final MessageProgress progressBar = ((IProgress) holder).getProgress();
            AppUtils.setProgresColor(progressBar.progressBar);

            progressBar.setVisibility(View.VISIBLE);
            progressBar.withDrawable(R.drawable.ic_cancel, false);

            /**
             * update progress when user trying to upload or download also if
             * file is compressing do this action for add listener and use later
             */
            if (HelperUploadFile.isUploading(mMessage.messageID) || (mMessage.status.equals(ProtoGlobal.RoomMessageStatus.SENDING.toString()) || FragmentChat.compressingFiles.containsKey(Long.parseLong(mMessage.messageID)))) {
                //(mMessage.status.equals(ProtoGlobal.RoomMessageStatus.SENDING.toString()) this code newly added
                hideThumbnailIf(holder);

                HelperUploadFile.AddListener(mMessage.messageID, new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(final int progress, FileUploadStructure struct) {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {

                                //float p = progress;
                                //if ((mMessage.messageType == ProtoGlobal.RoomMessageType.VIDEO || mMessage.messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) && FragmentChat.compressingFiles.containsKey(Long.parseLong(mMessage.messageID))) {
                                //    if (progress < mMessage.uploadProgress) {
                                //        p = mMessage.uploadProgress;
                                //    }
                                //}
                                if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID) && !(mMessage.status.equals(ProtoGlobal.RoomMessageStatus.FAILED.toString()))) {
                                    if (progress >= 1) {
                                        progressBar.withProgress(progress);

                                    }
                                    if (progress == 100) {
                                        progressBar.performProgress();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void OnError() {
                        if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mMessage.status = ProtoGlobal.RoomMessageStatus.FAILED.toString();
                                    progressBar.withProgress(0);
                                    progressBar.withDrawable(R.drawable.upload, true);
                                }
                            });
                        }
                    }
                });

                //if (mMessage.messageType == ProtoGlobal.RoomMessageType.VIDEO || mMessage.messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                //
                //    MediaController.onPercentCompress = new MediaController.OnPercentCompress() {
                //        @Override
                //        public void compress(final long percent, String path) {
                //
                //            G.handler.post(new Runnable() {
                //                @Override
                //                public void run() {
                //                    if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {
                //                        int p = (int) (percent / 10);
                //                        progressBar.withProgress(p);
                //                        mMessage.uploadProgress = p;
                //                    }
                //                }
                //            });
                //        }
                //    };
                //}

                ((IProgress) holder).getProgress().setVisibility(View.VISIBLE);
                progressBar.withProgress(HelperUploadFile.getUploadProgress(mMessage.messageID));
            } else {
                checkForDownloading(holder, attachment);
            }

            String _status = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getStatus() : mMessage.status;
            if (_status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                onFaildUpload(holder);
            }
        } else {
            checkForDownloading(holder, attachment);
        }
    }

    private void onFaildUpload(VH holder) {

        MessageProgress progressBar = ((IProgress) holder).getProgress();
        if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {
            AppUtils.setProgresColor(progressBar.progressBar);
            progressBar.withProgress(0);
            progressBar.withDrawable(R.drawable.upload, true);
        }
    }

    private void hideThumbnailIf(VH holder) {
        if (mMessage.messageType == ProtoGlobal.RoomMessageType.FILE || mMessage.messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            ((IThumbNailItem) holder).getThumbNailImageView().setVisibility(View.INVISIBLE);
        }
    }

    private void checkForDownloading(VH holder, RealmAttachment attachment) {

        MessageProgress progress = ((IProgress) holder).getProgress();
        AppUtils.setProgresColor(progress.progressBar);

        if (HelperDownloadFile.getInstance().isDownLoading(attachment.getCacheId())) {
            hideThumbnailIf(holder);

            downLoadFile(holder, attachment, 0);
        } else {
            if (attachment.isFileExistsOnLocal()) {
                if (!(mMessage.status.equals(ProtoGlobal.RoomMessageStatus.SENDING.toString()) && !(mMessage.status.equals(ProtoGlobal.RoomMessageStatus.FAILED.toString())))) {
                    progress.performProgress();
                }
            } else {
                hideThumbnailIf(holder);
                progress.withDrawable(R.drawable.ic_download, true);
                progress.setVisibility(View.VISIBLE);
            }
        }
    }

    public String getCacheId(StructMessageInfo mMessage) {
        if (mMessage.forwardedFrom != null && mMessage.forwardedFrom.getAttachment() != null && mMessage.forwardedFrom.getAttachment().getCacheId() != null) {
            return mMessage.forwardedFrom.getAttachment().getCacheId();
        } else if (mMessage.getAttachment() != null && mMessage.getAttachment().cashID != null) {
            return mMessage.getAttachment().cashID;
        }

        return "";
    }

    public void onBotBtnClick(View v) {
        try {
            if (v.getId() == ButtonActionType.USERNAME_LINK) {
                HelperUrl.checkUsernameAndGoToRoomWithMessageId(((ArrayList<String>) v.getTag()).get(0).toString().substring(1), HelperUrl.ChatEntry.chat, 0);
            } else if (v.getId() == ButtonActionType.BOT_ACTION) {
                try {
                    Long identity = System.currentTimeMillis();
                    Realm realm = Realm.getDefaultInstance();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmRoomMessage realmRoomMessage = RealmRoomMessage.makeAdditionalData(mMessage.roomId, identity, ((ArrayList<String>) v.getTag()).get(1).toString(), ((ArrayList<String>) v.getTag()).get(2).toString(), 3, realm);
                            G.chatSendMessageUtil.build(type, mMessage.roomId, realmRoomMessage).sendMessage(identity + "");
                            messageClickListener.sendFromBot(realmRoomMessage);
                        }
                    });

                } catch (Exception e) {
                }
            } else if (v.getId() ==  ButtonActionType.JOIN_LINK) {
                HelperUrl.checkAndJoinToRoom(((ArrayList<String>) v.getTag()).get(0).toString().substring(14));

            } else if (v.getId() == ButtonActionType.WEB_LINK) {
                HelperUrl.openBrowser(((ArrayList<String>) v.getTag()).get(0).toString());

            } else if (v.getId() == ButtonActionType.WEBVIEW_LINK) {
                messageClickListener.sendFromBot(((ArrayList<String>) v.getTag()).get(0).toString());
            }

        } catch (Exception e) {
            Toast.makeText(G.context, "دستور با خطا مواجه شد", Toast.LENGTH_LONG).show();
        }

    }

}
