/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.lalongooo.videocompressor.video.MediaController;
import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentPaymentBill;
import net.iGap.helper.CardToCardHelper;
import net.iGap.helper.DirectPayHelper;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetMessageState;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.IChatItemAttachment;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.OnProgressUpdate;
import net.iGap.libs.Tuple;
import net.iGap.libs.bottomNavigation.Util.Utils;
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
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChannelAddMessageReaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.isLocationFromBot;
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
    SpannableString myText;
    private RealmAttachment realmAttachment;
    private RealmRoom realmRoom;
    private RealmChannelExtra realmChannelExtra;
    private RealmRoom realmRoomForwardedFrom;
    private MessagesAdapter<AbstractMessage> mAdapter;


    /**
     * add this prt for video player
     */

    public AbstractMessage(MessagesAdapter<AbstractMessage> mAdapter, boolean directionalBased, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
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



    public static ArrayList<Tuple<Integer, Integer>> getBoldPlaces(String text) {
        ArrayList<Tuple<Integer, Integer>> result = new ArrayList<>();
        int start = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '*' && (i + 1) < text.length() && text.charAt(i + 1) == '*') {
                if (start == -1) {
                    start = i;
                } else {
                    Tuple<Integer, Integer> t = new Tuple<>(start, i);
                    result.add(t);
                    start = -1;
                }
                i += 1;
            }
        }

        return result;
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
            // if this not work then use view.requestLayout();
            view.forceLayout();
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

            RealmRoom realmRoomForwardedFrom22 = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getAuthorRoomId()).findFirst();
            if (realmRoomForwardedFrom22 != null && realmRoomForwardedFrom22.isValid())
                this.realmRoomForwardedFrom = getRealmChat().copyFromRealm(realmRoomForwardedFrom22);

            RealmChannelExtra realmChannelExtra22 = getRealmChat().where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, messageId).findFirst();
            if (realmChannelExtra22 != null && realmChannelExtra22.isValid())
                this.realmChannelExtra = getRealmChat().copyFromRealm(realmChannelExtra22);

        } else {
            realmRoomForwardedFrom = null;
            realmChannelExtra = null;
        }

        RealmRoom realmRoom22 = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.roomId).findFirst();
        if (realmRoom22 != null && realmRoom22.isValid())
            this.realmRoom = getRealmChat().copyFromRealm(realmRoom22);

        RealmRoomMessage f = RealmRoomMessage.getFinalMessage(getRealmChat().where(RealmRoomMessage.class).
                equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID)).findFirst());
        if (f != null) {
            realmAttachment = f.getAttachment();
        }
        if (mMessage.forwardedFrom != null) {
            myText = new SpannableString(mMessage.forwardedFrom.getMessage());
        } else if (mMessage.messageText != null) {
            myText = new SpannableString(mMessage.messageText);
        } else {
            myText = new SpannableString("");
        }

        updateMessageText();

        return this;
    }

    public static String removeBoldMark(String text, ArrayList<Tuple<Integer, Integer>> boldPlaces) {
        StringBuilder stringBuilder = new StringBuilder();
        if (boldPlaces.size() == 0)
            stringBuilder.append(text);
        else {
            for (int i = 0 ; i < boldPlaces.size(); i++) {
                Tuple<Integer, Integer> point = boldPlaces.get(i);
                Tuple<Integer, Integer> previousPoint = null;

                if (i != 0)
                    previousPoint = boldPlaces.get(i - 1);

                if (previousPoint == null)
                    stringBuilder.append(text.substring(0, point.x));
                else
                    stringBuilder.append(text.substring(previousPoint.y + 2, point.x));

                stringBuilder.append(text.substring(point.x + 2, point.y));

                if (i == boldPlaces.size() - 1)
                    stringBuilder.append(text.substring(point.y + 2));
            }
        }
        return stringBuilder.toString();
    }

    private void updateBoldPlaces(ArrayList<Tuple<Integer, Integer>> boldPlaces) {
        for (int i = 0 ; i < boldPlaces.size(); i++) {
            Tuple<Integer, Integer> point = boldPlaces.get(i);
            point.x = point.x - i * 4;
            point.y = point.y - i * 4 - 2;
        }
    }

    private ArrayList<Tuple<Integer, Integer>> MessageBoldSetup(String text) {
        ArrayList<Tuple<Integer, Integer>> boldPlaces = getBoldPlaces(text);
        myText = new SpannableString(removeBoldMark(text, boldPlaces));
        updateBoldPlaces(boldPlaces);
        return boldPlaces;
    }

    public void updateMessageText(String text) {
        myText = new SpannableString(text);
        updateMessageText();
    }

    public void updateMessageText() {
        if (!TextUtils.isEmpty(myText)) {
            ArrayList<Tuple<Integer, Integer>> results = MessageBoldSetup(myText.toString());
            if (mMessage.hasLinkInMessage) {
                myText = SpannableString.valueOf(HelperUrl.getLinkText(G.currentActivity, myText.toString(), mMessage.linkInfo, mMessage.messageID));
            } else {
                myText = new SpannableString(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(myText.toString()) : myText);
            }

            for (int i = 0; i < results.size(); i++) {
                Tuple<Integer, Integer> point = results.get(i);
                myText.setSpan(new StyleSpan(Typeface.BOLD), point.x, point.y, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    @Override
    public Item withIdentifier(long identifier) {
        return super.withIdentifier(identifier);
    }

    private void OnClickRow(NewChatItemHolder holder, View view) {
        new CountDownTimer(300, 100) {

            public void onTick(long millisUntilFinished) {
                view.setEnabled(false);
            }

            public void onFinish() {
                view.setEnabled(true);
            }
        }.start();

        if (FragmentChat.isInSelectionMode) {
            holder.itemView.performLongClick();
        } else {
            if (G.isLinkClicked) {
                G.isLinkClicked = false;
                return;
            }

            if (messageClickListener != null && mMessage != null && mMessage.senderID != null && !mMessage.senderID.equalsIgnoreCase("-1")) {
                if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                    return;
                }
                if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                    messageClickListener.onFailedMessageClick(view, mMessage, holder.getAdapterPosition());
                } else {
                    messageClickListener.onContainerClick(view, mMessage, holder.getAdapterPosition());
                }
            }
        }
    }

    @Override
    @CallSuper
    public void bindView(final VH holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        NewChatItemHolder mHolder;
        if (holder instanceof NewChatItemHolder)
            mHolder = (NewChatItemHolder) holder;
        else
            return;

        mHolder.getItemContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickRow(mHolder, view);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickRow(mHolder, view);
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
                            return FragmentChat.isInSelectionMode;
                        })
                        .setOnLinkLongClickListener((tv, url) -> {
                            return true;
                        });
            } else {
                // remove BetterLinkMovementMethod
            }

            try {
                if (mMessage.forwardedFrom == null && mMessage.additionalData != null && mMessage.additionalData.AdditionalType == AdditionalType.UNDER_MESSAGE_BUTTON) {
                    HashMap<Integer, JSONArray> buttonList = MakeButtons.parseData(mMessage.additionalData.additionalData);
                    Gson gson = new GsonBuilder().create();
                    for (int i = 0; i < buttonList.size(); i++) {
                        LinearLayout childLayout = MakeButtons.createLayout();
                        for (int j = 0; j < buttonList.get(i).length(); j++) {
                            try {

                                JSONObject json = new JSONObject(buttonList.get(i).get(j).toString());
                                ButtonEntity btnEntery = gson.fromJson(buttonList.get(i).get(j).toString(), new TypeToken<ButtonEntity>() {
                                }.getType());
                                if (btnEntery.getActionType() == ProtoGlobal.DiscoveryField.ButtonActionType.CARD_TO_CARD.getNumber()) {
//                                    btnEntery.setLongValue(json.getLong("value"));
                                }
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        withTextHolder.addButtonLayout(childLayout);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
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
            if (realmRoom != null && realmRoom.isValid() && realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && ProtoGlobal.RoomMessageStatus.FAILED != ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status)) {
                mHolder.getMessageStatusTv().setVisibility(View.GONE);
            } else {
                mHolder.getMessageStatusTv().setVisibility(View.VISIBLE);
                AppUtils.rightMessageStatus(mHolder.getMessageStatusTv(), ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status), mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType, mMessage.isSenderMe());
            }
        }
        /**
         * display 'edited' indicator beside message time if message was edited
         */
        if (mMessage.isEdited) {
            mHolder.getEditedIndicatorTv().setVisibility(View.VISIBLE);
        } else {
            mHolder.getEditedIndicatorTv().setVisibility(View.GONE);
        }
        /**
         * display user avatar only if chat type is GROUP
         */
        View messageSenderAvatar = mHolder.getItemContainer().findViewById(R.id.messageSenderAvatar);
        if (messageSenderAvatar != null) {
            messageSenderAvatar.setVisibility(View.GONE);
        }

        replyMessageIfNeeded(holder, getRealmChat());
        forwardMessageIfNeeded(holder, getRealmChat());

        View messageSenderName = mHolder.getContentBloke().findViewById(R.id.messageSenderName);
        if (messageSenderName != null) {
            messageSenderName.setVisibility(View.GONE);
        }

        if (type == ProtoGlobal.Room.Type.GROUP) {
            if (!mMessage.isSenderMe()) {
                addSenderNameToGroupIfNeed(mHolder, getRealmChat());

                if (messageSenderAvatar == null) {
                    messageSenderAvatar = ViewMaker.makeCircleImageView();
                    mHolder.getItemContainer().addView(messageSenderAvatar, 0);
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

                final ImageView copyMessageSenderAvatar = (ImageView) messageSenderAvatar;
                mAdapter.avatarHandler.getAvatar(new ParamWithAvatarType(copyMessageSenderAvatar, Long.parseLong(mMessage.senderID)).avatarType(AvatarHandler.AvatarType.USER));
            }
        }
        /**
         * set message time
         */

        String time = HelperCalander.getClocktime(mMessage.time, false);
        if (HelperCalander.isPersianUnicode) {
            mHolder.getMessageTimeTv().setText(HelperCalander.convertToUnicodeFarsiNumber(time));
        } else {
            mHolder.getMessageTimeTv().setText(time);
        }

        prepareAttachmentIfNeeded(holder, realmAttachment, mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType);

        /**
         * show vote layout for channel otherwise hide layout also get message state for channel
         */

        mHolder.getVoteContainer().setVisibility(View.GONE);
        mHolder.getViewContainer().setVisibility(View.GONE);
        if (!(holder instanceof StickerItem.ViewHolder)) {
            if ((type == ProtoGlobal.Room.Type.CHANNEL)) {
                showVote(holder, getRealmChat());
            } else if ((type == ProtoGlobal.Room.Type.CHAT)) {
                if (mMessage.forwardedFrom != null) {
                    if (mMessage.forwardedFrom.getAuthorRoomId() > 0) {
                        if (realmRoomForwardedFrom != null && realmRoomForwardedFrom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                            showVote(holder, getRealmChat());

                            if (mMessage.isSenderMe()) {
                                mHolder.getCslm_view_left_dis().setVisibility(View.VISIBLE);
                            }
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
        ((NewChatItemHolder) holder).getViewContainer().setVisibility(View.VISIBLE);
        voteAction(((NewChatItemHolder) holder), getRealmChat());
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

    private void addSenderNameToGroupIfNeed(final NewChatItemHolder holder, Realm realm) {

        if (G.showSenderNameInGroup) {
            View messageSenderName = holder.getContentBloke().findViewById(R.id.messageSenderName);
            if (messageSenderName != null) {
                holder.getContentBloke().removeView(messageSenderName);
            }

            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), Long.parseLong(mMessage.senderID));
            if (realmRegisteredInfo != null) {
                final EmojiTextViewE _tv = (EmojiTextViewE) ViewMaker.makeHeaderTextView(realmRegisteredInfo.getDisplayName());

                _tv.measure(0, 0);       //must call measure!
                int maxWith = 0;
                maxWith = _tv.getMeasuredWidth() + i_Dp(R.dimen.dp40);

                if (minWith < maxWith) {
                    minWith = maxWith;
                }
                holder.getContentBloke().setMinimumWidth(Math.min(minWith, G.maxChatBox));
                holder.getContentBloke().addView(_tv, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    protected View.OnLongClickListener getLongClickPerform(final RecyclerView.ViewHolder holder) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.itemView.performLongClick();
                return true;
            }
        };
    }

    protected void voteAction(NewChatItemHolder mHolder, Realm realm) {
        boolean showThump = G.showVoteChannelLayout && messageClickListener.getShowVoteChannel();
        if (showThump) {
            mHolder.getVoteContainer().setVisibility(View.VISIBLE);
        } else {
            mHolder.getVoteContainer().setVisibility(View.INVISIBLE);
        }

        /**
         * userId != 0 means that this message is from channel
         * because for chat and group userId will be set
         */

        Utils.darkModeHandlerGray(mHolder.getVoteDownIv());
        Utils.darkModeHandlerGray(mHolder.getVoteUpTv());
        Utils.darkModeHandlerGray(mHolder.getViewsLabelTv());
        Utils.darkModeHandlerGray(mHolder.getSignatureTv());
        Utils.darkModeHandlerGray(mHolder.getEyeIconTv());
        Utils.darkModeHandlerGray(mHolder.getEditedIndicatorTv());
        Utils.darkModeHandlerGray(mHolder.getMessageStatusTv());
        Utils.darkModeHandlerGray(mHolder.getVoteUpIv());
        Utils.darkModeHandlerGray(mHolder.getVoteDownTv());
        Utils.darkModeHandlerGray(mHolder.getMessageTimeTv());

        if ((mMessage.forwardedFrom != null)) {
            if (realmRoomForwardedFrom != null && realmRoomForwardedFrom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                if (realmChannelExtra != null) {
                    mHolder.getVoteUpTv().setText(realmChannelExtra.getThumbsUp());
                    mHolder.getVoteDownTv().setText(realmChannelExtra.getThumbsDown());
                    mHolder.getViewsLabelTv().setText(realmChannelExtra.getViewsLabel());
                    mHolder.getSignatureTv().setText(realmChannelExtra.getSignature());
                }
            } else {
                mHolder.getVoteUpTv().setText(mMessage.channelExtra.thumbsUp);
                mHolder.getVoteDownTv().setText(mMessage.channelExtra.thumbsDown);
                mHolder.getViewsLabelTv().setText(mMessage.channelExtra.viewsLabel);
                mHolder.getSignatureTv().setText(mMessage.channelExtra.signature);
            }
        } else {
            mHolder.getVoteUpTv().setText(mMessage.channelExtra.thumbsUp);
            mHolder.getVoteDownTv().setText(mMessage.channelExtra.thumbsDown);
            mHolder.getViewsLabelTv().setText(mMessage.channelExtra.viewsLabel);
            mHolder.getSignatureTv().setText(mMessage.channelExtra.signature);
        }

        if (mHolder.getSignatureTv().getText().length() > 0) {
            mHolder.getSignatureTv().setVisibility(View.VISIBLE);
        }

        if (HelperCalander.isPersianUnicode) {
            mHolder.getViewsLabelTv().setText(HelperCalander.convertToUnicodeFarsiNumber(mHolder.getViewsLabelTv().getText().toString()));
            mHolder.getVoteDownTv().setText(HelperCalander.convertToUnicodeFarsiNumber(mHolder.getVoteDownTv().getText().toString()));
            mHolder.getVoteUpTv().setText(HelperCalander.convertToUnicodeFarsiNumber(mHolder.getVoteUpTv().getText().toString()));
        }

        mHolder.getVoteUpContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FragmentChat.isInSelectionMode) {
                    mHolder.itemView.performLongClick();
                } else {
                    voteSend(ProtoGlobal.RoomMessageReaction.THUMBS_UP);
                }
            }
        });

        mHolder.getVoteDownContainer().setOnClickListener(new View.OnClickListener() {
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
        long authorRoomId = 0;
        long messageId = 0;
        if (mMessage.forwardedFrom != null) {
            authorRoomId = mMessage.forwardedFrom.getAuthorRoomId();
            messageId = mMessage.forwardedFrom.getMessageId();
        }

        if (messageId < 0) {
            messageId = messageId * (-1);
        }

        long finalAuthorRoomId = authorRoomId;
        long finalMessageId = messageId;

        getRealmChat().executeTransactionAsync(new Realm.Transaction() {
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
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, finalAuthorRoomId).findFirst();
                        if (realmRoom != null) {
                            roomType = realmRoom.getType();
                        }
                        if ((roomType == ProtoGlobal.Room.Type.CHANNEL)) {
                            G.handler.post(() -> new RequestChannelAddMessageReaction().channelAddMessageReactionForward(finalAuthorRoomId, Long.parseLong(mMessage.messageID), reaction, finalMessageId));
                        } else {
                            G.handler.post(() -> new RequestChannelAddMessageReaction().channelAddMessageReaction(mMessage.roomId, Long.parseLong(mMessage.messageID), reaction));
                        }
                    } else {
                        G.handler.post(() -> new RequestChannelAddMessageReaction().channelAddMessageReaction(mMessage.roomId, Long.parseLong(mMessage.messageID), reaction));
                    }
                }
            }
        });
    }

    @CallSuper
    protected void updateLayoutForReceive(VH holder) {
        NewChatItemHolder viewHolder;
        if (holder instanceof NewChatItemHolder)
            viewHolder = (NewChatItemHolder) holder;
        else
            return;

        if (holder instanceof ChatItemWithTextHolder) {
            if (G.isDarkTheme)
                ((ChatItemWithTextHolder) holder).messageView.setTextColor(G.context.getResources().getColor(R.color.receive_message_text_dark));
            else
                ((ChatItemWithTextHolder) holder).messageView.setTextColor(G.context.getResources().getColor(R.color.receive_message_text_light));
        }


        if (G.isDarkTheme) {
            viewHolder.getChatBloke().setBackgroundResource(R.drawable.shape_message_receive_dark);
            setThemeColor(viewHolder.getViewsLabelTv(), G.context.getResources().getColor(R.color.receive_message_time_dark));
            setThemeColor(viewHolder.getEyeIconTv(), G.context.getResources().getColor(R.color.receive_message_time_dark));
            setThemeColor(viewHolder.getVoteUpTv(), G.context.getResources().getColor(R.color.receive_message_time_dark));
            setThemeColor(viewHolder.getVoteUpIv(), G.context.getResources().getColor(R.color.receive_message_time_dark));
            setThemeColor(viewHolder.getVoteDownTv(), G.context.getResources().getColor(R.color.receive_message_time_dark));
            setThemeColor(viewHolder.getVoteDownIv(), G.context.getResources().getColor(R.color.receive_message_time_dark));
            setThemeColor(viewHolder.getEditedIndicatorTv(), G.context.getResources().getColor(R.color.receive_message_time_dark));
            setThemeColor(viewHolder.getSignatureTv(), G.context.getResources().getColor(R.color.receive_message_time_dark));
            setThemeColor(viewHolder.getMessageTimeTv(), G.context.getResources().getColor(R.color.receive_message_time_dark));
        } else {
            viewHolder.getChatBloke().setBackgroundResource(R.drawable.shape_message_receive_light);
            setThemeColor(viewHolder.getViewsLabelTv(), G.context.getResources().getColor(R.color.receive_message_time_light));
            setThemeColor(viewHolder.getEyeIconTv(), G.context.getResources().getColor(R.color.receive_message_time_light));
            setThemeColor(viewHolder.getVoteUpTv(), G.context.getResources().getColor(R.color.receive_message_time_light));
            setThemeColor(viewHolder.getVoteUpIv(), G.context.getResources().getColor(R.color.receive_message_time_light));
            setThemeColor(viewHolder.getVoteDownTv(), G.context.getResources().getColor(R.color.receive_message_time_light));
            setThemeColor(viewHolder.getVoteDownIv(), G.context.getResources().getColor(R.color.receive_message_time_light));
            setThemeColor(viewHolder.getEditedIndicatorTv(), G.context.getResources().getColor(R.color.receive_message_time_light));
            setThemeColor(viewHolder.getSignatureTv(), G.context.getResources().getColor(R.color.receive_message_time_light));
            setThemeColor(viewHolder.getMessageTimeTv(), G.context.getResources().getColor(R.color.receive_message_time_light));
        }

        /**
         * add main layout margin to prevent getting match parent completely
         * set to getItemContainer() not itemView because of selecting item foreground
         */

        ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).gravity = Gravity.LEFT;

        ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp4);
        ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp36);

    }

    @CallSuper
    protected void updateLayoutForSend(VH holder) {
        NewChatItemHolder viewHolder;
        if (holder instanceof NewChatItemHolder)
            viewHolder = (NewChatItemHolder) holder;
        else
            return;
        ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).gravity = Gravity.RIGHT;


        if (holder instanceof ChatItemWithTextHolder) {
            if (G.isDarkTheme)
                ((ChatItemWithTextHolder) holder).messageView.setTextColor(G.context.getResources().getColor(R.color.receive_message_text_dark));
            else
                ((ChatItemWithTextHolder) holder).messageView.setTextColor(G.context.getResources().getColor(R.color.receive_message_text_light));
        }


        ProtoGlobal.RoomMessageStatus status = ProtoGlobal.RoomMessageStatus.UNRECOGNIZED;
        if (mMessage.status != null) {
            try {
                status = ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        if (status == ProtoGlobal.RoomMessageStatus.SEEN) {

        } else if (status == ProtoGlobal.RoomMessageStatus.LISTENED) {
            if (G.isDarkTheme) {
                viewHolder.getMessageStatusTv().setTextColor(viewHolder.getColor(R.color.iGapColor));
            } else {
                viewHolder.getMessageStatusTv().setTextColor(viewHolder.getColor(R.color.backgroundColorCall2));
            }
            viewHolder.getMessageStatusTv().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getMessageStatusTv().setTextColor(viewHolder.getColor(R.color.unread_status));
        }


        if (G.isDarkTheme) {
            viewHolder.getChatBloke().setBackgroundResource(R.drawable.rectangle_send_round_color_dark);
            setThemeColor(viewHolder.getMessageTimeTv(), G.context.getResources().getColor(R.color.send_message_time_dark));
            setThemeColor(viewHolder.getEditedIndicatorTv(), G.context.getResources().getColor(R.color.send_message_time_dark));
        } else {
            viewHolder.getChatBloke().setBackgroundResource(R.drawable.rectangle_send_round_color);
            setThemeColor(viewHolder.getMessageTimeTv(), G.context.getResources().getColor(R.color.send_message_time_light));
            setThemeColor(viewHolder.getEditedIndicatorTv(), G.context.getResources().getColor(R.color.send_message_time_light));
        }
        ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp36);
        ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp4);
    }

    private void setThemeColor(TextView textView, int themeColor) {
        textView.setTextColor(themeColor);
    }

    @CallSuper
    protected void replyMessageIfNeeded(VH holder, Realm realm) {
        NewChatItemHolder mHolder;
        if (holder instanceof NewChatItemHolder)
            mHolder = (NewChatItemHolder) holder;
        else
            return;

        mHolder.getContentBloke().setMinimumWidth(0);
        mHolder.getContentBloke().setMinimumHeight(0);

        /**
         * set replay container visible if message was replayed, otherwise, gone it
         */
        View cslr_replay_layout = ((NewChatItemHolder) holder).getContentBloke().findViewById(R.id.cslr_replay_layout);

        if (cslr_replay_layout != null) {
            mHolder.getContentBloke().removeView(cslr_replay_layout);
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

                String replayText = AppUtils.replyTextMessage(mMessage.replayTo, holder.itemView.getResources());
                replayMessage.setText(replayText);

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
                mHolder.getContentBloke().setMinimumWidth(Math.min(minWith, G.maxChatBox));

                mHolder.getContentBloke().addView(replayView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                replayMessage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                replyFrom.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    @CallSuper
    protected void forwardMessageIfNeeded(VH holder, Realm realm) {
        NewChatItemHolder mHolder;
        if (holder instanceof NewChatItemHolder)
            mHolder = (NewChatItemHolder) holder;
        else
            return;
        /**
         * set forward container visible if message was forwarded, otherwise, gone it
         */
        View cslr_ll_forward22 = ((NewChatItemHolder) holder).getContentBloke().findViewById(R.id.cslr_ll_forward);
        if (cslr_ll_forward22 != null) {
            mHolder.getContentBloke().removeView(cslr_ll_forward22);
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
                            //TODO: fixed this and do not use G.currentActivity
                            HelperUrl.checkUsernameAndGoToRoomWithMessageId(G.currentActivity, mMessage.username, HelperUrl.ChatEntry.profile, (mMessage.forwardedFrom.getMessageId() * (-1)));
                        }
                    }
                }
            });

            TextView txtPrefixForwardFrom = forwardView.findViewById(R.id.cslr_txt_prefix_forward);
            txtPrefixForwardFrom.setTypeface(G.typeface_IRANSansMobile);
            TextView txtForwardFrom = forwardView.findViewById(R.id.cslr_txt_forward_from);
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
            mHolder.getContentBloke().setMinimumWidth(Math.min(minWith, G.maxChatBox));
            mHolder.getContentBloke().addView(forwardView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
        NewChatItemHolder mHolder;
        if (holder instanceof NewChatItemHolder)
            mHolder = (NewChatItemHolder) holder;
        else
            return;

        if (attachment != null) {

            if (mHolder instanceof VideoWithTextItem.ViewHolder || mHolder instanceof ImageWithTextItem.ViewHolder || mHolder instanceof StickerItem.ViewHolder) {
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
                    mHolder.getContentBloke().getLayoutParams().width = dimens[0];
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
                ((ViewGroup) mHolder.getContentBloke()).getLayoutParams().width = dimens[0];
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
                        if (mHolder instanceof StickerItem.ViewHolder) {
                            downLoadFile(holder, attachment, 0);
                        } else {
                            downLoadThumbnail(holder, attachment);
                        }
                    }
                }
            }

            if (hasProgress(holder)) {
                final MessageProgress _Progress = ((IProgress) holder).getProgress();
                _Progress.setTag(mMessage.messageID);
                _Progress.setVisibility(View.GONE);

                if (mHolder instanceof StickerItem.ViewHolder)
                    return;

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


        if (HelperUploadFile.isUploading(mMessage.messageID)) {
            if (mMessage.status.equals(ProtoGlobal.RoomMessageStatus.FAILED.toString()) && hasFileSize(attachment.getLocalFilePath())) {
                if (G.userLogin) {
                    messageClickListener.onFailedMessageClick(progress, mMessage, holder.getAdapterPosition());

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
        try (final Realm realm = Realm.getDefaultInstance()) {
            if (v.getId() == ButtonActionType.USERNAME_LINK) {
                //TODO: fixed this and do not use G.currentActivity
                HelperUrl.checkUsernameAndGoToRoomWithMessageId(G.currentActivity,((ArrayList<String>) v.getTag()).get(0).toString().substring(1), HelperUrl.ChatEntry.chat, 0);
            } else if (v.getId() == ButtonActionType.BOT_ACTION) {
                Long identity = System.currentTimeMillis();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoomMessage realmRoomMessage = RealmRoomMessage.makeAdditionalData(mMessage.roomId, identity, ((ArrayList<String>) v.getTag()).get(1).toString(), ((ArrayList<String>) v.getTag()).get(2).toString(), 3, realm ,ProtoGlobal.RoomMessageType.TEXT);
                        G.chatSendMessageUtil.build(type, mMessage.roomId, realmRoomMessage).sendMessage(identity + "");
                        messageClickListener.sendFromBot(realmRoomMessage);
                    }
                });

            } else if (v.getId() == ButtonActionType.JOIN_LINK) {
                //TODO: fixed this and do not use G.currentActivity
                HelperUrl.checkAndJoinToRoom(G.currentActivity,((ArrayList<String>) v.getTag()).get(0).toString().substring(14));

            } else if (v.getId() == ButtonActionType.WEB_LINK) {
                HelperUrl.openBrowser(((ArrayList<String>) v.getTag()).get(0).toString());

            } else if (v.getId() == ButtonActionType.WEBVIEW_LINK) {
                messageClickListener.sendFromBot(((ArrayList<String>) v.getTag()).get(0).toString());
            } else if (v.getId() == ButtonActionType.REQUEST_PHONE) {
                try {
                    new MaterialDialog.Builder(G.currentActivity).title(R.string.access_phone_number).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Long identity = System.currentTimeMillis();

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmUserInfo realmUserInfo = RealmUserInfo.getRealmUserInfo(realm);
                                    RealmRoomMessage realmRoomMessage = RealmRoomMessage.makeAdditionalData(mMessage.roomId, identity, realmUserInfo.getUserInfo().getPhoneNumber(),null, 0, realm, ProtoGlobal.RoomMessageType.TEXT);
                                    G.chatSendMessageUtil.build(type, mMessage.roomId, realmRoomMessage).sendMessage(identity + "");
                                    messageClickListener.sendFromBot(realmRoomMessage);
                                }
                            });
                        }
                    }).show();


                } catch (Exception e) {
                }

            } else if (v.getId() == ButtonActionType.REQUEST_LOCATION) {
                try {
                    new MaterialDialog.Builder(G.currentActivity).title(R.string.access_location).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            if (G.locationListener != null) {
                                isLocationFromBot = true;
                                G.locationListener.requestLocation();
                            }
                        }
                    }).show();


                } catch (Exception e) {
                }


            } else if (v.getId() == ButtonActionType.PAY_DIRECT) {
                JSONObject jsonObject = new JSONObject(((ArrayList<String>) v.getTag()).get(0));
                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.roomId).findFirst();
                long peerId;
                if (room != null && room.getChatRoom() != null) {
                    peerId = room.getChatRoom().getPeerId();
                } else {
                    peerId = Long.parseLong(mMessage.senderID);
                }
                DirectPayHelper.directPayBot(jsonObject, peerId);
            } else if (v.getId() == ProtoGlobal.DiscoveryField.ButtonActionType.CARD_TO_CARD.getNumber()) {
                JSONObject value = new JSONObject(((ArrayList<String>) v.getTag()).get(0));
                String cardNumber = value.getString("cardNumber");
                String amount = value.getString("amount");
                long userId = value.getLong("userId");
                CardToCardHelper.CallCardToCard(G.currentActivity, userId, amount, cardNumber);
            } else if (v.getId() == ProtoGlobal.DiscoveryField.ButtonActionType.BILL_MENU.getNumber()) {
                try {
                    JSONObject jsonObject = new JSONObject(((ArrayList<String>) v.getTag()).get(0));
                    new HelperFragment(G.currentActivity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills, jsonObject)).setReplace(false).load();
                } catch (JSONException e) {
                    new HelperFragment(G.currentActivity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills)).setReplace(false).load();
                }
            } else if (v.getId() == ProtoGlobal.DiscoveryField.ButtonActionType.TRAFFIC_BILL_MENU.getNumber()) {
                try {
                    JSONObject jsonObject = new JSONObject(((ArrayList<String>) v.getTag()).get(0));
                    new HelperFragment(G.currentActivity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills_crime, jsonObject)).setReplace(false).load();
                } catch (JSONException e) {
                    new HelperFragment(G.currentActivity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills_crime)).setReplace(false).load();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(G.context, "دستور با خطا مواجه شد", Toast.LENGTH_LONG).show();
        }

    }

}
