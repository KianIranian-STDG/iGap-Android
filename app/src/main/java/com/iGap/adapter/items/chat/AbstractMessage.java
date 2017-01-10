package com.iGap.adapter.items.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.MessagesAdapter;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperGetMessageState;
import com.iGap.helper.HelperUrl;
import com.iGap.interfaces.IChatItemAttachment;
import com.iGap.interfaces.IMessageItem;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnFileDownload;
import com.iGap.interfaces.OnProgressUpdate;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.MyType;
import com.iGap.module.ReserveSpaceGifImageView;
import com.iGap.module.ReserveSpaceRoundedImageView;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.StructDownloadAttachment;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.TimeUtils;
import com.iGap.module.enums.ConnectionMode;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAttachmentFields;
import com.iGap.realm.RealmChannelExtra;
import com.iGap.realm.RealmChannelExtraFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.request.RequestChannelAddMessageReaction;
import com.iGap.request.RequestFileDownload;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.meness.github.messageprogress.MessageProgress;
import io.meness.github.messageprogress.OnMessageProgressClick;
import io.meness.github.messageprogress.OnProgress;
import io.realm.Realm;
import java.io.IOException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.iGap.G.context;

public abstract class AbstractMessage<Item extends AbstractMessage<?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> implements IChatItemAttachment<VH> {//IChatItemAvatar
    public IMessageItem messageClickListener;
    public StructMessageInfo mMessage;
    public boolean directionalBased = true;
    public ProtoGlobal.Room.Type type;

    @Override public void onPlayPauseGIF(VH holder, String localPath) {
        // empty
    }

    public AbstractMessage(boolean directionalBased, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        this.directionalBased = directionalBased;
        this.type = type;
        this.messageClickListener = messageClickListener;
    }

    protected void setTextIfNeeded(TextView view, String msg) {
        if (!TextUtils.isEmpty(msg)) {
            if (mMessage.hasLinkInMessage) {
                view.setText(HelperUrl.setUrlLink(msg, true, true, mMessage.messageID, true));
            } else {
                view.setText(msg);
            }


            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public AbstractMessage setMessage(StructMessageInfo message) {
        this.mMessage = message;
        return this;
    }

    @Override public Item withIdentifier(long identifier) {
        return super.withIdentifier(identifier);
    }

    @Override @CallSuper public void bindView(final VH holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        /**
         * this use for select foreground in activity chat for search item and hash item
         *
         */

        if (holder instanceof ProgressWaiting.ViewHolder) return;

        mMessage.view = holder.itemView;

        /**
         * noinspection RedundantCast
         */
        if (!isSelected() && ((FrameLayout) holder.itemView).getForeground() != null) {
            /**
             * noinspection RedundantCast
             */
            ((FrameLayout) holder.itemView).setForeground(null);
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
            AppUtils.rightMessageStatus((ImageView) holder.itemView.findViewById(R.id.cslr_txt_tic), ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status),
                mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType, mMessage.isSenderMe());
        }
        /**
         * display 'edited' indicator beside message time if message was edited
         */
        if (holder.itemView.findViewById(R.id.txtEditedIndicator) != null) {
            if (mMessage.isEdited) {
                holder.itemView.findViewById(R.id.txtEditedIndicator).setVisibility(View.VISIBLE);
            } else {
                holder.itemView.findViewById(R.id.txtEditedIndicator).setVisibility(View.GONE);
            }
        }
        /**
         * display user avatar only if chat type is GROUP
         */
        if (type == ProtoGlobal.Room.Type.GROUP) {
            if (!mMessage.isSenderMe()) {
                holder.itemView.findViewById(R.id.messageSenderAvatar).setVisibility(View.VISIBLE);

                holder.itemView.findViewById(R.id.messageSenderAvatar).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        messageClickListener.onSenderAvatarClick(v, mMessage, holder.getAdapterPosition());
                    }
                });

                HelperAvatar.getAvatar(Long.parseLong(mMessage.senderID), HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                    @Override public void onAvatarGet(final String avatarPath, long ownerId) {
                        G.handler.post(new Runnable() {
                            @Override public void run() {
                                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), (ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar));
                            }
                        });
                    }

                    @Override public void onShowInitials(final String initials, final String color) {
                        G.handler.post(new Runnable() {
                            @Override public void run() {
                                ((ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar)).setImageBitmap(
                                    com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                            }
                        });
                    }
                });
            } else {
                holder.itemView.findViewById(R.id.messageSenderAvatar).setVisibility(View.GONE);
            }
        } else {
            if (!mMessage.isTimeOrLogMessage()) {
                holder.itemView.findViewById(R.id.messageSenderAvatar).setVisibility(View.GONE);
            }
        }
        /**
         * set message time
         */
        if (holder.itemView.findViewById(R.id.cslr_txt_time) != null) {
            ((TextView) holder.itemView.findViewById(R.id.cslr_txt_time)).setText(formatTime());
        }
        replyMessageIfNeeded(holder);
        forwardMessageIfNeeded(holder);

        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID)).findFirst();
        if (roomMessage != null) {
            prepareAttachmentIfNeeded(holder, roomMessage.getForwardMessage() != null ? roomMessage.getForwardMessage().getAttachment() : roomMessage.getAttachment(),
                mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType);
        }
        realm.close();
        TextView messageText = (TextView) holder.itemView.findViewById(R.id.messageText);
        if (messageText != null) {
            if (messageText.getParent() instanceof LinearLayout) {
                ((LinearLayout.LayoutParams) ((LinearLayout) messageText.getParent()).getLayoutParams()).gravity =
                    AndroidUtils.isTextRtl(mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessage() : mMessage.messageText) ? Gravity.RIGHT : Gravity.LEFT;
            }
        }

        /**
         * show vote layout for channel otherwise hide layout
         * also get message state for channel
         */
        if ((type == ProtoGlobal.Room.Type.CHANNEL)) {
            showVote(holder);
        } else {
            if (mMessage.forwardedFrom != null) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getRoomId()).findFirst();
                if (realmRoom != null && realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                    showVote(holder);
                } else {
                    hideVote(holder);
                }
            } else {
                hideVote(holder);
            }
        }
        Log.i("WWW", "12");
    }

    /**
     * show vote views
     */
    private void showVote(VH holder) {
        voteAction(holder);
        if (mMessage.forwardedFrom != null) {
            HelperGetMessageState.getMessageState(mMessage.forwardedFrom.getRoomId(), mMessage.forwardedFrom.getMessageId());
        } else {
            HelperGetMessageState.getMessageState(mMessage.roomId, Long.parseLong(mMessage.messageID));
        }
    }

    /**
     * hide vote views
     */
    private void hideVote(VH holder) {
        LinearLayout lytVote = (LinearLayout) holder.itemView.findViewById(R.id.lyt_vote);
        if (lytVote != null) {
            lytVote.setVisibility(View.GONE);
        }
    }

    @CallSuper protected void voteAction(VH holder) {

        LinearLayout lytVote = (LinearLayout) holder.itemView.findViewById(R.id.lyt_vote);
        if (lytVote != null) {

            LinearLayout lytVoteUp = (LinearLayout) holder.itemView.findViewById(R.id.lyt_vote_up);
            LinearLayout lytVoteDown = (LinearLayout) holder.itemView.findViewById(R.id.lyt_vote_down);
            TextView txtVoteUp = (TextView) holder.itemView.findViewById(R.id.txt_vote_up);
            TextView txtVoteDown = (TextView) holder.itemView.findViewById(R.id.txt_vote_down);
            TextView txtViewsLabel = (TextView) holder.itemView.findViewById(R.id.txt_views_label);
            TextView txtSignature = (TextView) holder.itemView.findViewById(R.id.txt_signature);

            lytVote.setVisibility(View.VISIBLE);
            if (mMessage.forwardedFrom != null) {
                Realm realm = Realm.getDefaultInstance();
                RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, mMessage.forwardedFrom.getMessageId()).findFirst();
                if (realmChannelExtra != null) {
                    txtVoteUp.setText(realmChannelExtra.getThumbsUp());
                    txtVoteDown.setText(realmChannelExtra.getThumbsDown());
                    txtViewsLabel.setText(realmChannelExtra.getViewsLabel());
                    txtSignature.setText(realmChannelExtra.getSignature());
                }
                realm.close();
            } else {
                txtVoteUp.setText(mMessage.channelExtra.thumbsUp);
                txtVoteDown.setText(mMessage.channelExtra.thumbsDown);
                txtViewsLabel.setText(mMessage.channelExtra.viewsLabel);
                txtSignature.setText(mMessage.channelExtra.signature);
            }

            lytVoteUp.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    voteSend(ProtoGlobal.RoomMessageReaction.THUMBS_UP);
                }
            });

            lytVoteDown.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    voteSend(ProtoGlobal.RoomMessageReaction.THUMBS_DOWN);
                }
            });
        }
    }

    /**
     * send vote action to RealmRoomMessage
     *
     * @param reaction Up or Down
     */
    private void voteSend(final ProtoGlobal.RoomMessageReaction reaction) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {

                RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID)).findFirst();
                if (realmRoomMessage != null) {
                    if (mMessage.forwardedFrom != null) {
                        new RequestChannelAddMessageReaction().channelAddMessageReactionForward(mMessage.forwardedFrom.getRoomId(), mMessage.forwardedFrom.getMessageId(), reaction,
                            Long.parseLong(mMessage.messageID));
                    } else {
                        new RequestChannelAddMessageReaction().channelAddMessageReaction(mMessage.roomId, Long.parseLong(mMessage.messageID), reaction);
                    }
                }
            }
        });
        realm.close();
    }

    @CallSuper protected void updateLayoutForReceive(VH holder) {
        ViewGroup frameLayout = (ViewGroup) holder.itemView.findViewById(R.id.mainContainer);

        ImageView imgTick = (ImageView) holder.itemView.findViewById(R.id.cslr_txt_tic);
        TextView messageText = (TextView) holder.itemView.findViewById(R.id.messageText);
        TextView timeText = (TextView) holder.itemView.findViewById(R.id.cslr_txt_time);
        LinearLayout lytRight = (LinearLayout) holder.itemView.findViewById(R.id.lyt_right);
        if (lytRight != null) {
            lytRight.setVisibility(View.GONE);
        }

        if (messageText != null) {
            messageText.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        }
        ProtoGlobal.RoomMessageType messageType = mMessage.forwardedFrom == null ? mMessage.messageType : mMessage.forwardedFrom.getMessageType();

        if (messageType == ProtoGlobal.RoomMessageType.IMAGE
            || messageType == ProtoGlobal.RoomMessageType.VIDEO
            || messageType == ProtoGlobal.RoomMessageType.GIF
            || messageType == ProtoGlobal.RoomMessageType.LOCATION) {
            timeText.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            imgTick.setColorFilter(ContextCompat.getColor(context, R.color.white));
        } else {
            imgTick.setColorFilter(ContextCompat.getColor(context, R.color.colorOldBlack));
            timeText.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        }

        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).gravity = Gravity.START;

        ((CardView) holder.itemView.findViewById(R.id.contentContainer)).setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.messageBox_receiveColor));

        /**
         * add main layout margin to prevent getting match parent completely
         * set to mainContainer not itemView because of selecting item foreground
         */
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp8);
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp28);
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp8);
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp28);

        ((LinearLayout.LayoutParams) (holder.itemView.findViewById(R.id.contentContainer).getLayoutParams())).leftMargin =
            (int) holder.itemView.getResources().getDimension(R.dimen.messageBox_minusLeftRightMargin);
        ((LinearLayout.LayoutParams) (holder.itemView.findViewById(R.id.contentContainer).getLayoutParams())).rightMargin = 0;
    }

    @CallSuper protected void updateLayoutForSend(VH holder) {
        ViewGroup frameLayout = (ViewGroup) holder.itemView.findViewById(R.id.mainContainer);
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).gravity = Gravity.END;

        ImageView imgTick = (ImageView) holder.itemView.findViewById(R.id.cslr_txt_tic);
        TextView messageText = (TextView) holder.itemView.findViewById(R.id.messageText);
        TextView timeText = (TextView) holder.itemView.findViewById(R.id.cslr_txt_time);
        LinearLayout lytRight = (LinearLayout) holder.itemView.findViewById(R.id.lyt_right);
        if (lytRight != null) {
            lytRight.setVisibility(View.VISIBLE);
        }

        if (messageText != null) {
            messageText.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        }
        ProtoGlobal.RoomMessageType messageType = mMessage.forwardedFrom == null ? mMessage.messageType : mMessage.forwardedFrom.getMessageType();

        if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == ProtoGlobal.RoomMessageType.VIDEO ||
            messageType == ProtoGlobal.RoomMessageType.GIF || messageType == ProtoGlobal.RoomMessageType.LOCATION) {
            timeText.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            imgTick.setColorFilter(ContextCompat.getColor(context, R.color.white));
        } else {
            imgTick.setColorFilter(ContextCompat.getColor(context, R.color.colorOldBlack));
            timeText.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        }

        ((CardView) holder.itemView.findViewById(R.id.contentContainer)).setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.messageBox_sendColor));
        /**
         * add main layout margin to prevent getting match parent completely
         * set to mainContainer not itemView because of selecting item foreground
         */
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp28);
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp8);

        ((LinearLayout.LayoutParams) (holder.itemView.findViewById(R.id.contentContainer).getLayoutParams())).rightMargin =
            (int) holder.itemView.getResources().getDimension(R.dimen.messageBox_minusLeftRightMargin);
        ((LinearLayout.LayoutParams) (holder.itemView.findViewById(R.id.contentContainer).getLayoutParams())).leftMargin = 0;
    }

    /**
     * format long time as string
     *
     * @return String
     */
    protected String formatTime() {
        return TimeUtils.toLocal(mMessage.time, G.CHAT_MESSAGE_TIME);
    }

    @CallSuper protected void replyMessageIfNeeded(VH holder) {
        /**
         * set replay container visible if message was replayed, otherwise, gone it
         */
        LinearLayout replayContainer = (LinearLayout) holder.itemView.findViewById(R.id.replayLayout);
        if (replayContainer != null) {
            TextView replyFrom = (TextView) holder.itemView.findViewById(R.id.chslr_txt_replay_from);
            TextView replayMessage = (TextView) holder.itemView.findViewById(R.id.chslr_txt_replay_message);
            if (mMessage.replayTo != null) {
                replayContainer.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        messageClickListener.onReplyClick(mMessage.replayTo);
                    }
                });
                holder.itemView.findViewById(R.id.chslr_imv_replay_pic).setVisibility(View.VISIBLE);

                try {
                    AppUtils.rightFileThumbnailIcon(((ImageView) holder.itemView.findViewById(R.id.chslr_imv_replay_pic)),
                        mMessage.replayTo.getForwardMessage() == null ? mMessage.replayTo.getMessageType() : mMessage.replayTo.getForwardMessage().getMessageType(),
                        mMessage.replayTo.getForwardMessage() == null ? mMessage.replayTo : mMessage.replayTo.getForwardMessage());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                Realm realm = Realm.getDefaultInstance();
                if (type == ProtoGlobal.Room.Type.CHANNEL) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.replayTo.getRoomId()).findFirst();
                    if (realmRoom != null) {
                        replyFrom.setText(realmRoom.getTitle());
                    }
                } else {
                    RealmRegisteredInfo replayToInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mMessage.replayTo.getUserId()).findFirst();
                    if (replayToInfo != null) {
                        replyFrom.setText(replayToInfo.getDisplayName());
                    }
                }

                ((TextView) holder.itemView.findViewById(R.id.chslr_txt_replay_message)).setText(
                    mMessage.replayTo.getForwardMessage() == null ? mMessage.replayTo.getMessage() : mMessage.replayTo.getForwardMessage().getMessage());

                replayContainer.setVisibility(View.VISIBLE);
                realm.close();

                if (mMessage.isSenderMe() && type != ProtoGlobal.Room.Type.CHANNEL) {
                    replayContainer.setBackgroundColor(holder.itemView.getResources().getColor(R.color.messageBox_replyBoxBackgroundSend));
                    holder.itemView.findViewById(R.id.verticalLine).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorOldBlack));
                    replyFrom.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
                    replayMessage.setTextColor(Color.WHITE);
                } else {
                    replayContainer.setBackgroundColor(holder.itemView.getResources().getColor(R.color.messageBox_replyBoxBackgroundReceive));
                    holder.itemView.findViewById(R.id.verticalLine).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.messageBox_sendColor));
                    replyFrom.setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
                    replayMessage.setTextColor(Color.BLACK);
                }
            } else {
                replayContainer.setVisibility(View.GONE);
            }
        }
    }

    @CallSuper protected void forwardMessageIfNeeded(VH holder) {
        /**
         * set forward container visible if message was forwarded, otherwise, gone it
         */
        LinearLayout forwardContainer = (LinearLayout) holder.itemView.findViewById(R.id.cslr_ll_forward);
        TextView txtForwardFrom = (TextView) holder.itemView.findViewById(R.id.cslr_txt_forward_from);
        if (forwardContainer != null) {
            if (mMessage.forwardedFrom != null) {
                forwardContainer.setVisibility(View.VISIBLE);
                Realm realm = Realm.getDefaultInstance();
                /**
                 * if forward message from chat or group , sender is user
                 * but if message forwarded from channel sender is room
                 */
                RealmRegisteredInfo info = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mMessage.forwardedFrom.getUserId()).findFirst();
                if (info != null) {
                    txtForwardFrom.setText(info.getDisplayName());
                    if (mMessage.isSenderMe()) {
                        txtForwardFrom.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
                    } else {
                        txtForwardFrom.setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
                    }
                } else {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getRoomId()).findFirst();
                    if (realmRoom != null) {
                        txtForwardFrom.setText(realmRoom.getTitle());
                        if (mMessage.isSenderMe()) {
                            txtForwardFrom.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
                        } else {
                            txtForwardFrom.setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
                        }
                    }
                }
                realm.close();
            } else {
                forwardContainer.setVisibility(View.GONE);
            }
        }
    }

    /**
     * does item have progress view
     *
     * @param itemView View
     * @return true if item has a progress
     */
    private boolean hasProgress(View itemView) {
        return itemView.findViewById(R.id.progress) != null;
    }

    private void setClickListener(SharedPreferences sharedPreferences, String key, final VH holder, final RealmAttachment attachment) {
        if (sharedPreferences.getInt(key, -1) != -1) {
            autoDownload(holder, attachment);
        } else {
            ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withOnMessageProgress(new OnMessageProgressClick() {
                @Override public void onMessageProgressClick(MessageProgress progress) {
                    forOnCLick(holder, attachment);
                }
            });
        }
    }

    private void checkAutoDownload(final VH holder, final RealmAttachment attachment, Context context, ConnectionMode connectionMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        switch (mMessage.messageType) {
            case IMAGE:
            case IMAGE_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_PHOTO, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_PHOTO, holder, attachment);
                        break;
                    // TODO: 12/4/2016 [Alireza] roaming and wimax ro check kon
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
                    // TODO: 12/4/2016 [Alireza] roaming and wimax ro check kon
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
                    // TODO: 12/4/2016 [Alireza] roaming and wimax ro check kon
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
                    // TODO: 12/4/2016 [Alireza] roaming and wimax ro check kon
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
                    // TODO: 12/4/2016 [Alireza] roaming and wimax ro check kon
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
                    // TODO: 12/4/2016 [Alireza] roaming and wimax ro check kon
                }
                break;
            default:
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withOnMessageProgress(new OnMessageProgressClick() {
                    @Override public void onMessageProgressClick(MessageProgress progress) {
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
        if (attachment != null) {
            if (messageType == ProtoGlobal.RoomMessageType.IMAGE
                || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT
                || messageType == ProtoGlobal.RoomMessageType.VIDEO
                || messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                ReserveSpaceRoundedImageView imageViewReservedSpace = (ReserveSpaceRoundedImageView) holder.itemView.findViewById(R.id.thumbnail);
                if (imageViewReservedSpace != null) {
                    int[] dimens = imageViewReservedSpace.reserveSpace(attachment.getWidth(), attachment.getHeight());
                    if (dimens[0] != 0 && dimens[1] != 0) {
                        ((ViewGroup) holder.itemView.findViewById(R.id.contentContainer)).getChildAt(0).getLayoutParams().width = dimens[0];
                    }
                }
            } else if (messageType == ProtoGlobal.RoomMessageType.GIF || messageType == ProtoGlobal.RoomMessageType.GIF_TEXT) {
                ReserveSpaceGifImageView imageViewReservedSpace = (ReserveSpaceGifImageView) holder.itemView.findViewById(R.id.thumbnail);
                if (imageViewReservedSpace != null) {
                    int[] dimens = imageViewReservedSpace.reserveSpace(attachment.getWidth(), attachment.getHeight());
                    ((ViewGroup) holder.itemView.findViewById(R.id.contentContainer)).getChildAt(0).getLayoutParams().width = dimens[0];
                }
            }

            /**
             * if file already exists, simply show the local one
             */
            if (attachment.isFileExistsOnLocalAndIsThumbnail()) {
                /**
                 * load file from local
                 */
                onLoadThumbnailFromLocal(holder, attachment.getLocalFilePath(), LocalFileType.FILE);
            } else if (messageType == ProtoGlobal.RoomMessageType.VOICE || messageType == ProtoGlobal.RoomMessageType.AUDIO || messageType == ProtoGlobal.RoomMessageType.AUDIO_TEXT) {
                onLoadThumbnailFromLocal(holder, attachment.getLocalFilePath(), LocalFileType.FILE);
            } else {
                /**
                 * file doesn't exist on local, I check for a thumbnail
                 * if thumbnail exists, I load it into the view
                 */
                if (attachment.isThumbnailExistsOnLocal()) {
                    /**
                     * load thumbnail from local
                     */
                    onLoadThumbnailFromLocal(holder, attachment.getLocalThumbnailPath(), LocalFileType.THUMBNAIL);
                } else {
                    requestForThumbnail();
                }
            }

            if (hasProgress(holder.itemView)) {
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withOnMessageProgress(new OnMessageProgressClick() {
                    @Override public void onMessageProgressClick(MessageProgress progress) {
                        forOnCLick(holder, attachment);
                    }
                });

                if (!attachment.isFileExistsOnLocal()) {
                    checkAutoDownload(holder, attachment, holder.itemView.getContext(), ConnectionMode.WIFI);
                    checkAutoDownload(holder, attachment, holder.itemView.getContext(), ConnectionMode.MOBILE);
                }

                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withOnProgress(new OnProgress() {
                    @Override public void onProgressFinished() {
                        holder.itemView.findViewById(R.id.thumbnail).setOnClickListener(null);
                        ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(null, true);

                        switch (messageType) {
                            case IMAGE:
                            case IMAGE_TEXT:
                                break;
                            case VIDEO:
                            case VIDEO_TEXT:
                                holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_play, true);
                                break;
                            case AUDIO:
                            case AUDIO_TEXT:
                                break;
                            case FILE:
                            case FILE_TEXT:
                                holder.itemView.findViewById(R.id.thumbnail).setOnClickListener(new View.OnClickListener() {
                                    @Override public void onClick(View v) {
                                        forOnCLick(holder, attachment);
                                    }
                                });
                                break;
                            case VOICE:
                                break;
                            case GIF:
                            case GIF_TEXT:
                                holder.itemView.findViewById(R.id.thumbnail).setOnClickListener(new View.OnClickListener() {
                                    @Override public void onClick(View v) {
                                        forOnCLick(holder, attachment);
                                    }
                                });

                                SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                if (sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS) == 0) {
                                    holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                                    ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_play, true);
                                } else {
                                    holder.itemView.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                                }
                                break;
                        }
                    }
                });
            }

            prepareProgress(holder, attachment);
        }
    }

    private void autoDownload(VH holder, RealmAttachment attachment) {
        if (mMessage.messageType == ProtoGlobal.RoomMessageType.FILE || mMessage.messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            View thumbnail = holder.itemView.findViewById(R.id.thumbnail);
            if (thumbnail != null) {
                thumbnail.setVisibility(View.INVISIBLE);
            }
        }

        /**
         * create new download attachment once with attachment token
         */
        if (mMessage.downloadAttachment == null) {
            mMessage.downloadAttachment = new StructDownloadAttachment(attachment);
        }

        /**
         * make sure to not request multiple times by checking last offset with the new one
         */
        if (mMessage.downloadAttachment.lastOffset < mMessage.downloadAttachment.offset) {
            onRequestDownloadFile(mMessage.downloadAttachment.offset, mMessage.downloadAttachment.progress, null);
            mMessage.downloadAttachment.lastOffset = mMessage.downloadAttachment.offset;
        }

        MessageProgress progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
        messageClickListener.onDownloadStart(progress, mMessage, holder.getAdapterPosition());
    }

    private void forOnCLick(VH holder, RealmAttachment attachment) {
        MessageProgress progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
        View thumbnail = holder.itemView.findViewById(R.id.thumbnail);

        if (mMessage.messageType == ProtoGlobal.RoomMessageType.FILE || mMessage.messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            if (thumbnail != null) {
                thumbnail.setVisibility(View.INVISIBLE);
            }
        }

        if (MessagesAdapter.hasUploadRequested(Long.parseLong(mMessage.messageID))) {
            messageClickListener.onUploadCancel(progress, mMessage, holder.getAdapterPosition());
        } else if (MessagesAdapter.hasDownloadRequested(attachment.getToken())) {
            messageClickListener.onDownloadCancel(progress, mMessage, holder.getAdapterPosition());
        } else {
            if (thumbnail != null) {
                thumbnail.setVisibility(View.VISIBLE);
            }

            if (attachment.isFileExistsOnLocal()) {
                if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                    return;
                }
                if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                    messageClickListener.onFailedMessageClick(progress, mMessage, holder.getAdapterPosition());
                } else {
                    // TODO: 12/7/2016 [Alireza] ba in shart dige nemishe GIF haro dar fragment show images did
                    if (mMessage.messageType == ProtoGlobal.RoomMessageType.GIF || mMessage.messageType == ProtoGlobal.RoomMessageType.GIF_TEXT) {
                        onPlayPauseGIF(holder, attachment.getLocalFilePath());
                    } else {
                        messageClickListener.onOpenClick(progress, mMessage, holder.getAdapterPosition());
                    }
                }
            } else {
                progress.withDrawable(R.drawable.ic_cancel, false);

                /**
                 * create new download attachment once with attachment token
                 */
                if (mMessage.downloadAttachment == null) {
                    mMessage.downloadAttachment = new StructDownloadAttachment(attachment);
                    progress.withProgress(mMessage.downloadAttachment.progress);
                }

                /**
                 * make sure to not request multiple times by checking last offset with the new one
                 */
                if (mMessage.downloadAttachment.lastOffset < mMessage.downloadAttachment.offset) {
                    onRequestDownloadFile(mMessage.downloadAttachment.offset, mMessage.downloadAttachment.progress, null);
                    mMessage.downloadAttachment.lastOffset = mMessage.downloadAttachment.offset;
                }

                messageClickListener.onDownloadStart(progress, mMessage, holder.getAdapterPosition());
            }
        }
    }

    @Override @CallSuper public void onLoadThumbnailFromLocal(VH holder, String localPath, LocalFileType fileType) {

    }

    @Override @CallSuper public void onRequestDownloadFile(long offset, int progress, final OnFileDownload onFileDownload) {
        if (mMessage.forwardedFrom != null) {
            final String fileName = mMessage.forwardedFrom.getAttachment().getToken() + "_" + mMessage.forwardedFrom.getAttachment().getName();
            final long forwardMessageID = mMessage.forwardedFrom.getMessageId();
            final ProtoGlobal.RoomMessageType forwardMessageType = mMessage.forwardedFrom.getMessageType();
            if (progress == 100) {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        realm.where(RealmRoomMessage.class)
                            .equalTo(RealmRoomMessageFields.MESSAGE_ID, forwardMessageID)
                            .findFirst()
                            .getAttachment()
                            .setLocalFilePath(AndroidUtils.suitableAppFilePath(forwardMessageType) + "/" + fileName);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override public void onSuccess() {
                        try {
                            AndroidUtils.cutFromTemp(mMessage.forwardedFrom.getMessageType(), fileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        onFileDownload.onFileDownloaded();
                        realm.close();
                    }
                });

                return; // necessary
            }

            if (!MessagesAdapter.hasDownloadRequested(mMessage.forwardedFrom.getAttachment().getToken())) {
                MessagesAdapter.requestDownload(mMessage.forwardedFrom.getAttachment().getToken(), progress, offset);
                G.downloadingTokens.add(mMessage.forwardedFrom.getAttachment().getToken());
            }
        } else {
            final String fileName = mMessage.attachment.token + "_" + mMessage.attachment.name;
            if (progress == 100) {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        realm.where(RealmRoomMessage.class)
                            .equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID))
                            .findFirst()
                            .getAttachment()
                            .setLocalFilePath(AndroidUtils.suitableAppFilePath(mMessage.messageType) + "/" + fileName);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override public void onSuccess() {
                        try {
                            AndroidUtils.cutFromTemp(mMessage.messageType, fileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (onFileDownload != null) {
                            onFileDownload.onFileDownloaded();
                        }

                        realm.close();
                    }
                });

                return; // necessary
            }

            if (!MessagesAdapter.hasDownloadRequested(mMessage.attachment.token)) {
                MessagesAdapter.requestDownload(mMessage.attachment.token, progress, offset);
                G.downloadingTokens.add(mMessage.attachment.token);
            }
        }

        if (onFileDownload != null) {
            onFileDownload.onFileDownloaded();
        }
    }

    @Override public void onRequestDownloadThumbnail(final String token, boolean done, final OnFileDownload onFileDownload) {
        if (mMessage.forwardedFrom != null && mMessage.forwardedFrom.getAttachment() != null && mMessage.forwardedFrom.getAttachment().getSmallThumbnail() != null) {
            if (mMessage.forwardedFrom.getAttachment().getSmallThumbnail().getSize() != 0) {

                final String fileName = "thumb_" + token + "_" + AppUtils.suitableThumbFileName(mMessage.forwardedFrom.getAttachment().getName());
                if (done) {
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override public void execute(Realm realm) {
                            RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();
                            if (attachment != null) {
                                attachment.setLocalThumbnailPath(G.DIR_TEMP + "/" + fileName);
                            }
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override public void onSuccess() {
                            onFileDownload.onFileDownloaded();
                            realm.close();
                        }
                    });

                    return; // necessary
                }

                ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                String identity = mMessage.attachment.token + '*' + selector.toString() + '*' + mMessage.forwardedFrom.getAttachment().getSmallThumbnail().getSize() + '*' + fileName + '*' + 0;

                new RequestFileDownload().download(token, 0, (int) mMessage.forwardedFrom.getAttachment().getSmallThumbnail().getSize(), selector, identity);
            }
        } else {
            if (mMessage.attachment.smallThumbnail != null && mMessage.attachment.smallThumbnail.size != 0) {

                final String fileName = "thumb_" + token + "_" + AppUtils.suitableThumbFileName(mMessage.attachment.name);
                if (done) {
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override public void execute(Realm realm) {
                            RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();
                            if (attachment != null) {
                                attachment.setLocalThumbnailPath(G.DIR_TEMP + "/" + fileName);
                            }
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override public void onSuccess() {
                            mMessage.attachment.localThumbnailPath = G.DIR_TEMP + "/" + fileName;
                            onFileDownload.onFileDownloaded();
                            realm.close();
                        }
                    });

                    return; // necessary
                }

                ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                String identity = mMessage.attachment.token + '*' + selector.toString() + '*' + mMessage.attachment.smallThumbnail.size + '*' + fileName + '*' + 0;

                new RequestFileDownload().download(token, 0, (int) mMessage.attachment.smallThumbnail.size, selector, identity);
            }
        }
    }

    private void requestForThumbnail() {
        if (mMessage.attachment == null) {
            return;
        }
        /**
         * create new download attachment once with attachment token
         */
        if (mMessage.downloadAttachment == null) {
            mMessage.downloadAttachment = new StructDownloadAttachment(mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getToken() : mMessage.attachment.token);
        }

        /**
         * request thumbnail
         */
        if (!mMessage.downloadAttachment.thumbnailRequested) {
            onRequestDownloadThumbnail(mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getToken() : mMessage.attachment.token, false, null);
            /**
             * prevent from multiple requesting thumbnail
             */
            mMessage.downloadAttachment.thumbnailRequested = true;
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
    private void prepareProgress(VH holder, RealmAttachment attachment) {
        if (!hasProgress(holder.itemView)) {
            return;
        }

        if (mMessage.sendType == MyType.SendType.send) {
            ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_cancel, false);
            /**
             * update progress when user trying to upload or download
             */
            if (MessagesAdapter.uploading.containsKey(Long.parseLong(mMessage.messageID))) {
                hideThumbnailIf(holder);

                holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withProgress(MessagesAdapter.uploading.get(Long.parseLong(mMessage.messageID)));
                if (MessagesAdapter.uploading.get(Long.parseLong(mMessage.messageID)) == 100) {
                    ((MessageProgress) holder.itemView.findViewById(R.id.progress)).performProgress();
                }
            } else {
                checkForDownloading(holder, attachment);
            }
        } else {
            checkForDownloading(holder, attachment);
        }
    }

    private void hideThumbnailIf(VH holder) {
        if (mMessage.messageType == ProtoGlobal.RoomMessageType.FILE || mMessage.messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            View view = holder.itemView.findViewById(R.id.thumbnail);
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void checkForDownloading(VH holder, RealmAttachment attachment) {
        MessageProgress progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
        if (MessagesAdapter.downloading.containsKey(attachment.getToken())) {
            hideThumbnailIf(holder);

            progress.withDrawable(R.drawable.ic_cancel, false);
            progress.setVisibility(View.VISIBLE);
            progress.withProgress(MessagesAdapter.downloading.get(attachment.getToken()));

            if (MessagesAdapter.downloading.get(attachment.getToken()) == 100) {
                MessagesAdapter.downloading.remove(attachment.getToken());
                progress.performProgress();
            }
        } else {
            if (attachment.isFileExistsOnLocal()) {
                progress.performProgress();
            } else {
                hideThumbnailIf(holder);
                progress.withDrawable(R.drawable.ic_download, true);
                progress.setVisibility(View.VISIBLE);
            }
        }
    }
}
