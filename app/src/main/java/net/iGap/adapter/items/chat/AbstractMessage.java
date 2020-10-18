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
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.collection.ArrayMap;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentPaymentBill;
import net.iGap.helper.CardToCardHelper;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetMessageState;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.libs.Tuple;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messageprogress.OnMessageProgressClick;
import net.iGap.messageprogress.OnProgress;
import net.iGap.model.CardToCardValue;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.MakeButtons;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyType;
import net.iGap.module.ReserveSpaceGifImageView;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.TimeUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.module.additionalData.ButtonActionType;
import net.iGap.module.additionalData.ButtonEntity;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.Downloader;
import net.iGap.module.enums.LocalFileType;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.network.RequestManager;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.IChatItemAttachment;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.observers.interfaces.OnProgressUpdate;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAdditional;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChannelAddMessageReaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.isLocationFromBot;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public abstract class AbstractMessage<Item extends AbstractMessage<?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> implements IChatItemAttachment<VH>, EventListener {//IChatItemAvatar
    public static ArrayMap<Long, String> updateForwardInfo = new ArrayMap<>();// after get user info or room info if need update view in chat activity
    public IMessageItem messageClickListener;
    public RealmRoomMessage mMessage;
    public StructMessageInfo structMessage;
    public boolean directionalBased;
    public ProtoGlobal.Room.Type type;
    private int minWith = 0;
    private SpannableString myText;
    private RealmRoom realmRoomForwardedFrom;
    public MessagesAdapter<AbstractMessage> mAdapter;
    private final Drawable SEND_ITEM_BACKGROUND = G.context.getResources().getDrawable(R.drawable.chat_item_sent_bg_light);
    private final Drawable RECEIVED_ITEM_BACKGROUND = G.context.getResources().getDrawable(R.drawable.chat_item_receive_bg_light);
    protected Theme theme;
    private VH holder;
    private int currentAccount;

    /**
     * add this prt for video player
     */

    public AbstractMessage(MessagesAdapter<AbstractMessage> mAdapter, boolean directionalBased, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        this.directionalBased = directionalBased;
        this.type = type;
        this.mAdapter = mAdapter;
        this.messageClickListener = messageClickListener;
        this.theme = Theme.getInstance();
        currentAccount = AccountManager.selectedAccount;
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
            view.setText(EmojiManager.getInstance().replaceEmoji(myText, view.getPaint().getFontMetricsInt(), LayoutCreator.dp(20), false));
            // if this not work then use view.requestLayout();
            view.forceLayout();
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public AbstractMessage setMessage(StructMessageInfo message) {
        return DbManager.getInstance().doRealmTask((DbManager.RealmTaskWithReturn<AbstractMessage>) realm -> {
            structMessage = message;

            AbstractMessage.this.mMessage = message.realmRoomMessage;

            if ((mMessage.getForwardMessage() != null)) {
                long messageId = mMessage.getForwardMessage().getMessageId();
                if (mMessage.getForwardMessage().getMessageId() < 0) {
                    messageId = messageId * (-1);
                }

                RealmRoom realmRoomForwardedFrom22 = realm.where(RealmRoom.class).equalTo("id", mMessage.getForwardMessage().getAuthorRoomId()).findFirst();
                if (realmRoomForwardedFrom22 != null && realmRoomForwardedFrom22.isValid())
                    AbstractMessage.this.realmRoomForwardedFrom = realm.copyFromRealm(realmRoomForwardedFrom22);
            } else {
                realmRoomForwardedFrom = null;
            }

            if (mMessage.getForwardMessage() != null) {
                myText = new SpannableString(mMessage.getForwardMessage().getMessage());
            } else if (mMessage.getMessage() != null) {
                myText = new SpannableString(mMessage.getMessage());
            } else {
                myText = new SpannableString("");
            }

            updateMessageText();

            return AbstractMessage.this;
        });
    }

    public static String removeBoldMark(String text, ArrayList<Tuple<Integer, Integer>> boldPlaces) {
        StringBuilder stringBuilder = new StringBuilder();
        if (boldPlaces.size() == 0)
            stringBuilder.append(text);
        else {
            for (int i = 0; i < boldPlaces.size(); i++) {
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
        for (int i = 0; i < boldPlaces.size(); i++) {
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
            if (structMessage.hasLinkInMessage()) {
                myText = SpannableString.valueOf(HelperUrl.getLinkText(G.currentActivity, myText.toString(), structMessage.getLinkInfo(), mMessage.getMessageId() + ""));
            } /*else {
                myText = new SpannableString(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(myText.toString()) : myText);
            }*/

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

            if (messageClickListener != null && mMessage != null && mMessage.getUserId() != -1L) {
                if (mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                    return;
                }
                if (mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                    messageClickListener.onFailedMessageClick(view, structMessage, holder.getAdapterPosition());
                } else {
                    messageClickListener.onContainerClick(view, structMessage, holder.getAdapterPosition());
                }
            }
        }
    }


    @Override
    public void receivedMessage(int id, Object... message) {
        if (id == EventManager.ON_UPLOAD_PROGRESS) {
            G.runOnUiThread(() -> {
                String messageKey = (String) message[0];
                String messageId = String.valueOf(mMessage.getMessageId());

                if (messageKey.equals(messageId)) {
                    int progress = (int) message[1];
                    String progressString = String.valueOf(progress);
                    String attachmentSizeString = AndroidUtils.humanReadableByteCount(mMessage.getAttachment().getSize(), true);

                    if (G.selectedLanguage.equals("fa")) {
                        progressString = HelperCalander.convertToUnicodeFarsiNumber(progressString);
                        attachmentSizeString = HelperCalander.convertToUnicodeFarsiNumber(attachmentSizeString);
                    }

                    if (holder instanceof FileItem.ViewHolder || holder instanceof VideoWithTextItem.ViewHolder) {
                        TextView progressTextView = ((IProgress) holder).getProgressTextView();
                        progressTextView.setText(String.format(Locale.US, "%s%% — %s", progressString, attachmentSizeString));
                    } else if (holder instanceof AudioItem.ViewHolder) {
                        AudioItem.ViewHolder audioHolder = (AudioItem.ViewHolder) holder;
                        audioHolder.getSongTimeTv().setText(String.format(Locale.US, "%s%% — %s", progressString, attachmentSizeString));
                    }

                    MessageProgress progressView = ((IProgress) holder).getProgress();
                    if (progressView.getTag() != null && progressView.getTag().equals(mMessage.getMessageId()) && !(mMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.FAILED.toString()))) {
                        if (progress >= 1 && progress != 100) {
                            progressView.withProgress(progress);
                        }
                    }
                }
            });
        } else if (id == EventManager.ON_UPLOAD_COMPRESS) {
            G.runOnUiThread(() -> {
                String messageKey = (String) message[0];
                String messageId = String.valueOf(mMessage.getMessageId());

                if (messageKey.equals(messageId) && holder instanceof VideoWithTextItem.ViewHolder) {
                    VideoWithTextItem.ViewHolder videoHolder = (VideoWithTextItem.ViewHolder) holder;
                    int progress = (int) message[1];

                    if (progress <= 99) {
                        String progressString = String.valueOf(progress);

                        if (G.selectedLanguage.equals("fa")) {
                            progressString = HelperCalander.convertToUnicodeFarsiNumber(progressString);
                        }

                        videoHolder.duration.setText(String.format(G.context.getResources().getString(R.string.video_duration), progressString + "%" + G.context.getResources().getString(R.string.compressing) + "—" + AndroidUtils.humanReadableByteCount(structMessage.getAttachment().getSize(), true), AndroidUtils.formatDuration((int) (structMessage.getAttachment().getDuration() * 1000L))));
                    } else {
                        videoHolder.duration.setText(String.format(G.context.getResources().getString(R.string.video_duration), AndroidUtils.humanReadableByteCount(structMessage.getAttachment().getSize(), true) + " ", AndroidUtils.formatDuration((int) (structMessage.getAttachment().getDuration() * 1000L)) + G.context.getResources().getString(R.string.Uploading)));
                    }
                }
            });
        }

    }

    @Override
    @CallSuper
    public void bindView(final VH holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        NewChatItemHolder mHolder;
        if (holder instanceof NewChatItemHolder) {
            mHolder = (NewChatItemHolder) holder;
        } else if (holder instanceof LogItem.ViewHolder ||
                holder instanceof LogWalletCardToCard.ViewHolder ||
                holder instanceof LogWalletBill.ViewHolder ||
                holder instanceof LogWalletTopup.ViewHolder ||
                holder instanceof LogWallet.ViewHolder) {
            messageClickListener.onItemShowingMessageId(structMessage);
            return;
        } else {
            return;
        }
        this.holder = holder;

        if (mMessage.getForwardMessage() == null && mMessage.isSenderMe() && mMessage.getAttachment() != null &&
                mMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString())
        ) {
            EventManager.getInstance().addEventListener(EventManager.ON_UPLOAD_PROGRESS, this);
            EventManager.getInstance().addEventListener(EventManager.ON_UPLOAD_COMPRESS, this);

            if (!Uploader.getInstance().isCompressingOrUploading(mMessage.getMessageId() + "")) {
                UploadObject fileObject = UploadObject.createForMessage(mMessage, type);
                if (fileObject != null) {
                    Uploader.getInstance().upload(fileObject);
                }
            } else {
                MessageProgress messageProgress = ((IProgress) holder).getProgress();
                int progress = Uploader.getInstance().getUploadProgress(mMessage.getMessageId() + "");
                messageProgress.withProgress(progress);
                receivedMessage(EventManager.ON_UPLOAD_PROGRESS, String.valueOf(mMessage.getMessageId()), progress);
            }
        } else {
            EventManager.getInstance().removeEventListener(EventManager.ON_UPLOAD_PROGRESS, this);
            EventManager.getInstance().removeEventListener(EventManager.ON_UPLOAD_COMPRESS, this);
        }

        structMessage.addAttachmentChangeListener(this, getIdentifier(), this, holder, mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getMessageType() : mMessage.getMessageType());
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

        if (holder instanceof CardToCardItem.ViewHolder) {
            CardToCardItem.ViewHolder cardToCardHolder = (CardToCardItem.ViewHolder) holder;
            cardToCardHolder.getRootView().setMinWidth(G.maxChatBox - i_Dp(R.dimen.dp100));
            cardToCardHolder.getInnerLayout().setMinimumWidth(G.maxChatBox - i_Dp(R.dimen.dp100));

            if (mMessage.getForwardMessage() == null && structMessage.getAdditional().getAdditionalData() != null && structMessage.getAdditional().getAdditionalType() == AdditionalType.CARD_TO_CARD_MESSAGE) {

                CardToCardValue value = new CardToCardValue();
                try {
                    JSONArray rootJsonArray = new JSONArray(structMessage.getAdditional().getAdditionalData());
                    for (int i = 0; i < rootJsonArray.length(); i++) {
                        JSONArray valuJsonArray = rootJsonArray.getJSONArray(i);
                        for (int j = 0; j < valuJsonArray.length(); j++) {

                            JSONObject rootJsonObject = new JSONObject(valuJsonArray.getJSONObject(i).toString());
                            JSONObject valueObject = rootJsonObject.getJSONObject("value");

                            String cardNumber = valueObject.getString("cardNumber");
                            int amount = valueObject.getInt("amount");
                            long userId = valueObject.getLong("userId");

                            value.setAmount(amount);
                            value.setCardNumber(cardNumber);
                            value.setUserId(userId);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cardToCardHolder.setValue(value);
            }

            cardToCardHolder.setOnCardToCard(cardToCard -> {
                CardToCardHelper.NewCallCardToCard(G.currentActivity, cardToCard.getUserId(), cardToCard.getAmount(), cardToCard.getCardNumber());
            });
        }

        if (holder instanceof ChatItemWithTextHolder) {
            ChatItemWithTextHolder withTextHolder = (ChatItemWithTextHolder) holder;
//            withTextHolder.messageView.setHasEmoji(structMessage.hasEmojiInText());

            int maxsize = 0;
            withTextHolder.removeButtonLayout();
            if ((type == ProtoGlobal.Room.Type.CHANNEL) || (type == ProtoGlobal.Room.Type.CHAT) && mMessage.getForwardMessage() != null) {
                maxsize = G.maxChatBox - 16;
            }
            if (maxsize > 0)
                withTextHolder.messageView.setMaxWidth(maxsize);
            if (structMessage.hasLinkInMessage()) {
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, withTextHolder.messageView)
                        .setOnLinkClickListener((tv, url) -> {
                            return FragmentChat.isInSelectionMode;
                        })
                        .setOnLinkLongClickListener((tv, url) -> {

                            if (!FragmentChat.isInSelectionMode) {
                                if (HelperUrl.isTextLink(url)) {
                                    G.isLinkClicked = true;
                                    messageClickListener.onOpenLinkDialog(url);
                                }

                            }
                            return true;
                        });
            } else {
                // remove BetterLinkMovementMethod
            }

            try {
                if (mMessage.getForwardMessage() == null && structMessage.getAdditional() != null && structMessage.getAdditional().getAdditionalType() == AdditionalType.UNDER_MESSAGE_BUTTON) {
                    HashMap<Integer, JSONArray> buttonList = MakeButtons.parseData(structMessage.getAdditional().getAdditionalData());

                    int rowSize = buttonList.size();

                    for (int i = 0; i < rowSize; i++) {
                        int columSize = buttonList.get(i).length();
                        LinearLayout childLayout = MakeButtons.createLayout(((ChatItemWithTextHolder) holder).getContext());
                        for (int j = 0; j < columSize; j++) {
                            ButtonEntity buttonEntity = new Gson().fromJson(buttonList.get(i).get(j).toString(), ButtonEntity.class);
                            buttonEntity.setJsonObject(buttonList.get(i).get(j).toString());

                            childLayout = MakeButtons.addButtons(theme, buttonEntity, buttonList.get(i).length(), 1f, childLayout, (view, btnEntity) -> {
                                if (FragmentChat.isInSelectionMode) {
                                    holder.itemView.performLongClick();
                                    return;
                                }
                                mAdapter.onBotButtonClicked(() -> onBotBtnClick(view, btnEntity));
                            });
                        }
                        withTextHolder.addButtonLayout(childLayout);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            ((LinearLayout.LayoutParams) ((LinearLayout) withTextHolder.messageView.getParent()).getLayoutParams()).gravity = AndroidUtils.isTextRtl(mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getMessage() : mMessage.getMessage()) ? Gravity.RIGHT : Gravity.LEFT;
        }

        /**
         * for return message that start showing to view
         */
        messageClickListener.onItemShowingMessageId(structMessage);

        /**
         * this use for select foreground in activity chat for search item and hash item
         *
         */

        /**
         * noinspection RedundantCast
         */

        if (isSelected() || structMessage.isSelected) {
            ((FrameLayout) holder.itemView).setForeground(new ColorDrawable(G.context.getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
        } else {
            ((FrameLayout) holder.itemView).setForeground(new ColorDrawable(Color.TRANSPARENT));
        }

        /**
         * only will be called when message layout is directional-base (e.g. single chat)
         */
        if (directionalBased) {
            if ((structMessage.getSendType() == MyType.SendType.recvive) || type == ProtoGlobal.Room.Type.CHANNEL) {
                updateLayoutForReceive(holder);
            } else if (structMessage.getSendType() == MyType.SendType.send) {
                updateLayoutForSend(holder);
            }
        }

        if (!structMessage.isTimeOrLogMessage()) {
            /**
             * check failed state ,because if is failed we want show to user even is in channel
             */
            if (mAdapter.getRealmRoom() != null && mAdapter.getRealmRoom().isValid() && mAdapter.getRealmRoom().getType() == ProtoGlobal.Room.Type.CHANNEL && ProtoGlobal.RoomMessageStatus.FAILED != ProtoGlobal.RoomMessageStatus.valueOf(mMessage.getStatus())) {
                mHolder.getMessageStatusTv().setVisibility(View.GONE);
            } else {
                mHolder.getMessageStatusTv().setVisibility(View.VISIBLE);
                AppUtils.rightMessageStatus(mHolder.getMessageStatusTv(), ProtoGlobal.RoomMessageStatus.valueOf(mMessage.getStatus()), mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getMessageType() : mMessage.getMessageType(), mMessage.isSenderMe());
            }
        }
        /**
         * display 'edited' indicator beside message time if message was edited
         */

        if (mMessage.isEdited())
            if (structMessage.getChannelExtraWithoutForward() != null && structMessage.getChannelExtraWithoutForward().getSignature() != null && structMessage.getChannelExtraWithoutForward().getSignature().length() > 0)
                mHolder.getSignatureTv().setText(mHolder.getResources().getString(R.string.edited) + " " + structMessage.getChannelExtraWithoutForward().getSignature());
            else {
                mHolder.getSignatureTv().setText(mHolder.getResources().getString(R.string.edited));
            }
        else
            mHolder.getSignatureTv().setText("");
        /**
         * display user avatar only if chat type is GROUP
         */
        View messageSenderAvatar = mHolder.getItemContainer().findViewById(R.id.messageSenderAvatar);
        if (messageSenderAvatar != null) {
            messageSenderAvatar.setVisibility(View.GONE);
        }

        replyMessageIfNeeded(holder);
        forwardMessageIfNeeded(holder);

        View messageSenderName = mHolder.getContentBloke().findViewById(R.id.messageSenderName);
        if (messageSenderName != null) {
            messageSenderName.setVisibility(View.GONE);
        }

        if (type == ProtoGlobal.Room.Type.GROUP) {
            if (!mMessage.isSenderMe()) {
                addSenderNameToGroupIfNeed(mHolder);

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

                        messageClickListener.onSenderAvatarClick(v, structMessage, holder.getAdapterPosition());
                    }
                });

                messageSenderAvatar.setOnLongClickListener(getLongClickPerform(holder));

                final ImageView copyMessageSenderAvatar = (ImageView) messageSenderAvatar;
                mAdapter.avatarHandler.getAvatar(new ParamWithAvatarType(copyMessageSenderAvatar, mMessage.getUserId()).avatarType(AvatarHandler.AvatarType.USER));
            }
        } else {
            FrameLayout forwardContainer = mHolder.getItemContainer().findViewById(R.id.messageForwardContainer);
            ContextThemeWrapper wrapper = new ContextThemeWrapper(holder.itemView.getContext(), Theme.getInstance().getTheme(holder.itemView.getContext()));

            if (forwardContainer == null) {
                forwardContainer = new FrameLayout(holder.itemView.getContext());
                forwardContainer.setId(R.id.messageForwardContainer);

                if (mAdapter.getRealmRoom() != null && mAdapter.getRealmRoom().getChatRoom() != null && mAdapter.getRealmRoom().getChatRoom().getPeerId() == AccountManager.getInstance().getCurrentUser().getId()) {
                    mHolder.getItemContainer().addView(forwardContainer, 0, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.BOTTOM));
                } else {
                    mHolder.getItemContainer().addView(forwardContainer, 1, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.BOTTOM));
                }

                forwardContainer.addView(mHolder.getChannelForwardIv(), LayoutCreator.createFrame(26, 26, Gravity.BOTTOM, 4, 4, 8, 4));
                forwardContainer.setVisibility(View.GONE);
            }

            if (type == ProtoGlobal.Room.Type.CHANNEL) {
                mHolder.getChannelForwardIv().setImageDrawable(VectorDrawableCompat.create(holder.itemView.getContext().getResources(), R.drawable.ic_channel_forward_light, wrapper.getTheme()));
                forwardContainer.setVisibility(View.VISIBLE);
                mHolder.getChannelForwardIv().setOnClickListener(v -> {
                    if (isAllowToForward(mMessage))
                        messageClickListener.onForwardClick(structMessage);
                });
            }

            if (type == ProtoGlobal.Room.Type.CHAT && mAdapter.getRealmRoom().getChatRoom().getPeerId() == AccountManager.getInstance().getCurrentUser().getId() && !structMessage.isGiftSticker()) {
                mHolder.getChannelForwardIv().setImageDrawable(VectorDrawableCompat.create(holder.itemView.getContext().getResources(), R.drawable.ic_cloud_forward, wrapper.getTheme()));
                forwardContainer.setVisibility(View.VISIBLE);
                mHolder.getChannelForwardIv().setOnClickListener(v -> {
                    if (isAllowToForward(mMessage))
                        messageClickListener.onForwardFromCloudClick(structMessage);
                });
            }
        }


        /**
         * set message time
         */

        String time = HelperCalander.getClocktime(mMessage.getCreateTime(), false);
        if (HelperCalander.isPersianUnicode) {
            mHolder.getMessageTimeTv().setText(HelperCalander.convertToUnicodeFarsiNumber(time));
        } else {
            mHolder.getMessageTimeTv().setText(time);
        }

        prepareAttachmentIfNeeded(holder, mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getMessageType() : mMessage.getMessageType());

        /**
         * show vote layout for channel otherwise hide layout also get message state for channel
         */

        mHolder.getVoteContainer().setVisibility(View.GONE);
        mHolder.getViewContainer().setVisibility(View.GONE);
        if (!(holder instanceof StickerItem.ViewHolder /*|| mHolder instanceof AnimatedStickerItem.ViewHolder*/)) {
            if ((type == ProtoGlobal.Room.Type.CHANNEL)) {
                showVote(holder);
            } else {
                if (mMessage.getForwardMessage() != null) {
                    if (mMessage.getForwardMessage().getAuthorRoomId() > 0) {
                        if (realmRoomForwardedFrom != null && realmRoomForwardedFrom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                            showVote(holder);

                            if (mMessage.isSenderMe()) {
                                mHolder.getCslm_view_left_dis().setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        }


        if (structMessage.getChannelExtraWithoutForward() != null && structMessage.getChannelExtraWithoutForward().getSignature() != null && structMessage.getChannelExtraWithoutForward().getSignature().length() > 0) {
            mHolder.getContentBloke().setMinimumWidth(LayoutCreator.dp(200));
        } else if (mMessage.isEdited()) {
            mHolder.getContentBloke().setMinimumWidth(LayoutCreator.dp(100));
        }
    }

    private boolean isAllowToForward(RealmRoomMessage message) {
        return !FragmentChat.isInSelectionMode &&
                message != null &&
                !message.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString()) &&
                !message.getStatus().equals(ProtoGlobal.RoomMessageStatus.FAILED.toString());
    }

    /**
     * show vote views
     */
    private void showVote(VH holder) {
        // add layout seen in channel
        ((NewChatItemHolder) holder).getViewContainer().setVisibility(View.VISIBLE);
        voteAction(((NewChatItemHolder) holder));
        getChannelMessageState();
    }

    /**
     * get channel message state, for clear unread message in channel client
     * need send request for getMessageState even show vote layout is hide
     */
    private void getChannelMessageState() {
        if ((mMessage.getForwardMessage() != null)) {
            ProtoGlobal.Room.Type roomType = null;
            if (realmRoomForwardedFrom != null) {
                roomType = realmRoomForwardedFrom.getType();
            }
            if ((mMessage.getForwardMessage() != null) && (roomType == ProtoGlobal.Room.Type.CHANNEL)) {
                /**
                 * if roomType is Channel don't consider forward
                 *
                 * when i add message to RealmRoomMessage(putOrUpdate) set (replyMessageId * (-1))
                 * so i need to (replyMessageId * (-1)) again for use this messageId
                 */
                long messageId = mMessage.getForwardMessage().getMessageId();
                if (mMessage.getForwardMessage().getMessageId() < 0) {
                    messageId = messageId * (-1);
                }
                HelperGetMessageState.getMessageState(mMessage.getForwardMessage().getAuthorRoomId(), messageId);
            } else {
                HelperGetMessageState.getMessageState(mMessage.getRoomId(), mMessage.getMessageId());
            }
        } else {
            HelperGetMessageState.getMessageState(mMessage.getRoomId(), mMessage.getMessageId());
        }
    }

    private void addSenderNameToGroupIfNeed(final NewChatItemHolder holder) {

        if (G.showSenderNameInGroup) {
            View messageSenderName = holder.getContentBloke().findViewById(R.id.messageSenderName);
            if (messageSenderName != null) {
                holder.getContentBloke().removeView(messageSenderName);
            }

            RealmRegisteredInfo realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
                return RealmRegisteredInfo.getRegistrationInfo(realm, mMessage.getUserId());
            });
            if (realmRegisteredInfo != null) {
                final AppCompatTextView _tv = ViewMaker.makeHeaderTextView(holder.getContext(), realmRegisteredInfo.getDisplayName());

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

    protected void voteAction(NewChatItemHolder mHolder) {
        boolean showThump = G.showVoteChannelLayout && messageClickListener.getShowVoteChannel();
        if (showThump) {
            mHolder.getVoteContainer().setVisibility(View.VISIBLE);
        } else {
            mHolder.getVoteContainer().setVisibility(View.GONE);
        }

        /**
         * userId != 0 means that this message is from channel
         * because for chat and group userId will be set
         */

        mHolder.getVoteUpTv().setText(structMessage.getChannelExtra() == null ? "0" : structMessage.getChannelExtra().getThumbsUp());
        mHolder.getVoteDownTv().setText(structMessage.getChannelExtra() == null ? "0" : structMessage.getChannelExtra().getThumbsDown());
        mHolder.getViewsLabelTv().setText(structMessage.getChannelExtra() == null ? "1" : structMessage.getChannelExtra().getViewsLabel());
        if (mMessage.isEdited())
            mHolder.getSignatureTv().setText(mHolder.itemView.getContext().getResources().getString(R.string.edited) + " " + (structMessage.getChannelExtra() != null ? structMessage.getChannelExtra().getSignature() : ""));
        else
            mHolder.getSignatureTv().setText(structMessage.getChannelExtra() == null ? "" : structMessage.getChannelExtra().getSignature());


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
        if ((mMessage.getForwardMessage() != null)) {
            long forwardMessageId = mMessage.getForwardMessage().getMessageId();

            if (forwardMessageId < 0) {
                forwardMessageId = forwardMessageId * (-1);
            }
            RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoom.class).equalTo("id", mMessage.getForwardMessage().getAuthorRoomId()).findFirst();
            });
            if (realmRoom != null && realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                new RequestChannelAddMessageReaction().channelAddMessageReactionForward(mMessage.getForwardMessage().getAuthorRoomId(), mMessage.getMessageId(), reaction, forwardMessageId);
            } else {
                new RequestChannelAddMessageReaction().channelAddMessageReaction(mMessage.getRoomId(), mMessage.getMessageId(), reaction);
            }
        } else {
            new RequestChannelAddMessageReaction().channelAddMessageReaction(mMessage.getRoomId(), mMessage.getMessageId(), reaction);
        }
    }

    @CallSuper
    protected void updateLayoutForReceive(VH holder) {
        NewChatItemHolder viewHolder;
        if (holder instanceof NewChatItemHolder)
            viewHolder = (NewChatItemHolder) holder;
        else
            return;

        if (holder instanceof ChatItemWithTextHolder) {
            ((ChatItemWithTextHolder) holder).messageView.setTextColor(theme.getReceivedMessageColor(viewHolder.getContext()));
        }

        viewHolder.getChatBloke().setBackground(theme.tintDrawable(RECEIVED_ITEM_BACKGROUND, viewHolder.getContext(), R.attr.colorPrimaryLight));

        /**
         * add main layout margin to prevent getting match parent completely
         * set to getItemContainer() not itemView because of selecting item foreground
         */

        ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).gravity = Gravity.LEFT;

        ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp4);
        viewHolder.getMessageTimeTv().setTextColor(theme.getReceivedMessageOtherTextColor(viewHolder.getContext()));
        viewHolder.getSignatureTv().setTextColor(theme.getReceivedMessageOtherTextColor(viewHolder.getContext()));

        if (type == ProtoGlobal.Room.Type.CHANNEL) {
            ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp15);
        } else {
            ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp36);
        }

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
            ((ChatItemWithTextHolder) holder).messageView.setTextColor(theme.getSendMessageTextColor(((ChatItemWithTextHolder) holder).getContext()));
        }


        ProtoGlobal.RoomMessageStatus status = ProtoGlobal.RoomMessageStatus.UNRECOGNIZED;
        if (mMessage.getStatus() != null) {
            try {
                status = ProtoGlobal.RoomMessageStatus.valueOf(mMessage.getStatus());
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        if (status != ProtoGlobal.RoomMessageStatus.SEEN) {
            viewHolder.getMessageStatusTv().setTextColor(theme.getSendMessageOtherTextColor(viewHolder.getContext()));
        }
        viewHolder.getChatBloke().setBackground(theme.tintDrawable(SEND_ITEM_BACKGROUND, viewHolder.getContext(), R.attr.iGapSendMessageBubbleColor));
        viewHolder.getMessageTimeTv().setTextColor(theme.getSendMessageOtherTextColor(viewHolder.getContext()));
        viewHolder.getSignatureTv().setTextColor(theme.getSendMessageOtherTextColor(viewHolder.getContext()));
        ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp36);
        ((FrameLayout.LayoutParams) viewHolder.getItemContainer().getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp4);
    }

    @CallSuper
    protected void replyMessageIfNeeded(VH holder) {
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

        if (mMessage.replyTo != null && mMessage.replyTo.isValid() && !mMessage.replyTo.deleted) {

            final View replayView = ViewMaker.getViewReplay(((NewChatItemHolder) holder).getContext());

            final TextView replyFrom = replayView.findViewById(R.id.chslr_txt_replay_from);
            final AppCompatTextView replayMessage = replayView.findViewById(R.id.chslr_txt_replay_message);
            replayView.setOnClickListener(v -> {
                if (FragmentChat.isInSelectionMode) {
                    holder.itemView.performLongClick();
                    return;
                }

                messageClickListener.onReplyClick(mMessage.replyTo);
            });

            replayView.setOnLongClickListener(getLongClickPerform(holder));

            try {
                AppUtils.rightFileThumbnailIcon(replayView.findViewById(R.id.chslr_imv_replay_pic), mMessage.replyTo.forwardMessage == null ? mMessage.replyTo.getMessageType() : mMessage.replyTo.forwardMessage.getMessageType(), mMessage.replyTo.forwardMessage == null ? mMessage.replyTo : mMessage.replyTo.forwardMessage);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            if (type == ProtoGlobal.Room.Type.CHANNEL) {
                if (mAdapter.getRealmRoom() != null) {
                    replyFrom.setText(EmojiManager.getInstance().replaceEmoji(mAdapter.getRealmRoom().getTitle(), replyFrom.getPaint().getFontMetricsInt()));
                }
            } else {
                RealmRegisteredInfo replayToInfo = DbManager.getInstance().doRealmTask(realm -> {
                    return RealmRegisteredInfo.getRegistrationInfo(realm, mMessage.replyTo.userId);
                });
                if (replayToInfo != null) {
                    replyFrom.setText(EmojiManager.getInstance().replaceEmoji(replayToInfo.getDisplayName(), replyFrom.getPaint().getFontMetricsInt()));
                }
            }

            String replayText = AppUtils.replyTextMessage(mMessage.replyTo, holder.itemView.getResources());
            replayMessage.setText(EmojiManager.getInstance().replaceEmoji(replayText, replayMessage.getPaint().getFontMetricsInt()));

            if (mMessage.isSenderMe() && type != ProtoGlobal.Room.Type.CHANNEL) {
                replayView.setBackgroundResource(theme.getSendReplay(replayView.getContext()));
            } else {
                replayView.setBackgroundResource(theme.getReceivedReplay(replayView.getContext()));
            }
            replyFrom.setTextColor(theme.getSendReplayUserColor(replyFrom.getContext()));
            replayMessage.setTextColor(theme.getSendMessageTextColor(replayMessage.getContext()));

            replyFrom.measure(0, 0);       //must call measure!
            replayMessage.measure(0, 0);

            int maxWith, withMessage, withTitle;
            withTitle = replyFrom.getMeasuredWidth();
            withMessage = replayMessage.getMeasuredWidth();
            maxWith = Math.max(withTitle, withMessage);
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

    @CallSuper
    protected void forwardMessageIfNeeded(VH holder) {
        NewChatItemHolder mHolder;
        if (holder instanceof NewChatItemHolder && !(holder instanceof GiftStickerItem.ViewHolder))
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

        if (mMessage.getForwardMessage() != null) {

            View forwardView = ViewMaker.getViewForward(((NewChatItemHolder) holder).getContext());
            forwardView.setOnLongClickListener(getLongClickPerform(holder));

            forwardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FragmentChat.isInSelectionMode) {
                        holder.itemView.performLongClick();
                    } else {
                        if (structMessage.username.length() > 0) {
                            //TODO: fixed this and do not use G.currentActivity
                            HelperUrl.checkUsernameAndGoToRoomWithMessageId(G.currentActivity, structMessage.username, HelperUrl.ChatEntry.profile, (mMessage.getForwardMessage().getMessageId() * (-1)));
                        }
                    }
                }
            });

            AppCompatTextView txtPrefixForwardFrom = forwardView.findViewById(R.id.cslr_txt_prefix_forward);
            txtPrefixForwardFrom.setTypeface(ResourcesCompat.getFont(txtPrefixForwardFrom.getContext(), R.font.main_font));
            AppCompatTextView txtForwardFrom = forwardView.findViewById(R.id.cslr_txt_forward_from);
            txtForwardFrom.setTypeface(ResourcesCompat.getFont(txtPrefixForwardFrom.getContext(), R.font.main_font));

            /**
             * if forward message from chat or group , sender is user
             * but if message forwarded from channel sender is room
             */
            RealmRegisteredInfo info = DbManager.getInstance().doRealmTask(realm -> {
                return RealmRegisteredInfo.getRegistrationInfo(realm, mMessage.getForwardMessage().getUserId());
            });
            if (info != null) {

                if (RealmRegisteredInfo.needUpdateUser(info.getId(), info.getCacheId())) {
                    if (!updateForwardInfo.containsKey(info.getId())) {
                        updateForwardInfo.put(info.getId(), mMessage.getMessageId() + "");
                    }
                }

                txtForwardFrom.setText(EmojiManager.getInstance().replaceEmoji(info.getDisplayName(), txtForwardFrom.getPaint().getFontMetricsInt()));
                structMessage.username = info.getUsername();
                txtForwardFrom.setTextColor(theme.getForwardFromTextColor(txtForwardFrom.getContext()));
            } else if (mMessage.getForwardMessage().getUserId() != 0) {

                if (RealmRegisteredInfo.needUpdateUser(mMessage.getForwardMessage().getUserId(), null)) {
                    if (!updateForwardInfo.containsKey(mMessage.getForwardMessage().getUserId())) {
                        updateForwardInfo.put(mMessage.getForwardMessage().getUserId(), mMessage.getMessageId() + "");
                    }
                }
            } else {
                RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
                    return realm.where(RealmRoom.class).equalTo("id", mMessage.getForwardMessage().getRoomId()).findFirst();
                });
                if (realmRoom != null) {
                    txtForwardFrom.setText(EmojiManager.getInstance().replaceEmoji(realmRoom.getTitle(), txtForwardFrom.getPaint().getFontMetricsInt()));
                    txtForwardFrom.setTextColor(theme.getForwardFromTextColor(txtForwardFrom.getContext()));

                    switch (realmRoom.getType()) {
                        case CHANNEL:
                            structMessage.username = realmRoom.getChannelRoom().getUsername();
                            break;
                        case GROUP:
                            structMessage.username = realmRoom.getGroupRoom().getUsername();
                            break;
                    }
                } else {
                    if (realmRoomForwardedFrom != null) {

                        switch (realmRoomForwardedFrom.getType()) {
                            case CHANNEL:
                                if (realmRoomForwardedFrom.getChannelRoom() != null && realmRoomForwardedFrom.getChannelRoom().getUsername() != null) {
                                    structMessage.username = realmRoomForwardedFrom.getChannelRoom().getUsername();
                                } else {
                                    structMessage.username = holder.itemView.getResources().getString(R.string.private_channel);
                                }

                                break;
                            case GROUP:
                                structMessage.username = realmRoomForwardedFrom.getGroupRoom().getUsername();
                                break;
                        }

                        if (RealmRoom.needUpdateRoomInfo(realmRoomForwardedFrom.getId())) {
                            if (!updateForwardInfo.containsKey(realmRoomForwardedFrom.getId())) {
                                updateForwardInfo.put(realmRoomForwardedFrom.getId(), mMessage.getMessageId() + "");
                            }
                        }

                        txtForwardFrom.setText(EmojiManager.getInstance().replaceEmoji(realmRoomForwardedFrom.getTitle(), txtForwardFrom.getPaint().getFontMetricsInt()));
                        txtForwardFrom.setTextColor(theme.getForwardFromTextColor(txtForwardFrom.getContext()));
                    } else {
                        if (RealmRoom.needUpdateRoomInfo(mMessage.getForwardMessage().getAuthorRoomId())) {
                            if (!updateForwardInfo.containsKey(mMessage.getForwardMessage().getAuthorRoomId())) {
                                updateForwardInfo.put(mMessage.getForwardMessage().getAuthorRoomId(), mMessage.getMessageId() + "");
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
        return holder instanceof IProgress;
    }

    private void setClickListener(SharedPreferences sharedPreferences, String key, final VH holder) {

        /**
         * if type was gif auto file start auto download
         */
        if (sharedPreferences.getInt(key, ((key.equals(SHP_SETTING.KEY_AD_DATA_GIF) || key.equals(SHP_SETTING.KEY_AD_WIFI_GIF)) ? 5 : -1)) != -1) {
            autoDownload(holder);
        } else {

            MessageProgress _Progress = ((IProgress) holder).getProgress();
            AppUtils.setProgresColor(_Progress.progressBar);

            _Progress.withOnMessageProgress(new OnMessageProgressClick() {
                @Override
                public void onMessageProgressClick(MessageProgress progress) {
                    forOnCLick(holder);
                }
            });
        }
    }

    private void checkAutoDownload(final VH holder, Context context, HelperCheckInternetConnection.ConnectivityType connectionMode) {

        if (HelperDownloadFile.getInstance().manuallyStoppedDownload.contains(structMessage.getAttachment().getCacheId())) { // for avoid from reDownload in autoDownload state , after that user manually stopped download.
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        ProtoGlobal.RoomMessageType messageType;
        if (mMessage.getForwardMessage() != null) {
            messageType = mMessage.getForwardMessage().getMessageType();
        } else {
            messageType = mMessage.getMessageType();
        }
        switch (messageType) {
            case IMAGE:
            case IMAGE_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_PHOTO, holder);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_PHOTO, holder);
                        break;
                }
                break;
            case VOICE:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, holder);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, holder);
                        break;
                }
                break;
            case VIDEO:
            case VIDEO_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_VIDEO, holder);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_VIDEO, holder);
                        break;
                }
                break;
            case FILE:
            case FILE_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_FILE, holder);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_FILE, holder);
                        break;
                }
                break;
            case AUDIO:
            case AUDIO_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_MUSIC, holder);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_MUSIC, holder);
                        break;
                }
                break;
            case GIF:
            case GIF_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_GIF, holder);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_GIF, holder);
                        break;
                }
                break;
            default:

                MessageProgress _Progress = ((IProgress) holder).getProgress();
                AppUtils.setProgresColor(_Progress.progressBar);

                _Progress.withOnMessageProgress(new OnMessageProgressClick() {
                    @Override
                    public void onMessageProgressClick(MessageProgress progress) {
                        forOnCLick(holder);
                    }
                });
                break;
        }
    }

    private void prepareAttachmentIfNeeded(final VH holder, final ProtoGlobal.RoomMessageType messageType) {
        /**
         * runs if message has attachment
         */
        NewChatItemHolder mHolder;
        if (holder instanceof NewChatItemHolder)
            mHolder = (NewChatItemHolder) holder;
        else
            return;

        if (structMessage.getAttachment() != null) {

            if (mHolder instanceof VideoWithTextItem.ViewHolder || mHolder instanceof ImageWithTextItem.ViewHolder) {
                ReserveSpaceRoundedImageView imageViewReservedSpace = (ReserveSpaceRoundedImageView) ((IThumbNailItem) holder).getThumbNailImageView();
                int _with = structMessage.getAttachment().getWidth();
                int _hight = structMessage.getAttachment().getHeight();

                if (_with == 0) {
                    if (structMessage.getAttachment().getSmallThumbnail() != null) {
                        _with = structMessage.getAttachment().getSmallThumbnail().getWidth();
                        _hight = structMessage.getAttachment().getSmallThumbnail().getHeight();
                    }
                }

                boolean setDefualtImage = false;

                if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                    if (structMessage.getAttachment().getLocalFilePath() == null && structMessage.getAttachment().getLocalThumbnailPath() == null && _with == 0) {
                        _with = (int) G.context.getResources().getDimension(R.dimen.dp120);
                        _hight = (int) G.context.getResources().getDimension(R.dimen.dp120);
                        setDefualtImage = true;
                    }
                } else {
                    if (structMessage.getAttachment().getLocalThumbnailPath() == null && _with == 0) {
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

                if (holder instanceof VideoWithTextItem.ViewHolder) {
                    ((VideoWithTextItem.ViewHolder) holder).getMoreButton().setOnClickListener(v -> {
                        OnClickRow(((VideoWithTextItem.ViewHolder) holder), v);
                    });
                }

                if (holder instanceof ImageWithTextItem.ViewHolder) {
                    ((ImageWithTextItem.ViewHolder) holder).getMoreButton().setOnClickListener(v -> {
                        OnClickRow(((ImageWithTextItem.ViewHolder) holder), v);
                    });
                }


            } else if (messageType == ProtoGlobal.RoomMessageType.GIF || messageType == ProtoGlobal.RoomMessageType.GIF_TEXT) {
                ReserveSpaceGifImageView imageViewReservedSpace = (ReserveSpaceGifImageView) ((IThumbNailItem) holder).getThumbNailImageView();
                int _with = structMessage.getAttachment().getWidth();
                int _hight = structMessage.getAttachment().getHeight();

                if (_with == 0) {
                    _with = (int) G.context.getResources().getDimension(R.dimen.dp200);
                }

                if (_hight == 0) {
                    _hight = (int) G.context.getResources().getDimension(R.dimen.dp200);
                }

                int[] dimens = imageViewReservedSpace.reserveSpace(_with, _hight, type);
                mHolder.getContentBloke().getLayoutParams().width = dimens[0];
            }


            if (structMessage.getAttachment().isFileExistsOnLocalAndIsImage()) {
                onLoadThumbnailFromLocal(holder, getCacheId(structMessage), structMessage.getAttachment().getLocalFilePath(), LocalFileType.FILE);
            } else if (messageType == ProtoGlobal.RoomMessageType.VOICE || messageType == ProtoGlobal.RoomMessageType.AUDIO || messageType == ProtoGlobal.RoomMessageType.AUDIO_TEXT) {
                onLoadThumbnailFromLocal(holder, getCacheId(structMessage), structMessage.getAttachment().getLocalFilePath(), LocalFileType.FILE);
            } else {
                if (structMessage.getAttachment().isThumbnailExistsOnLocal()) {
                    onLoadThumbnailFromLocal(holder, getCacheId(structMessage), structMessage.getAttachment().getLocalThumbnailPath(), LocalFileType.THUMBNAIL);
                } else {
                    if (messageType != ProtoGlobal.RoomMessageType.CONTACT) {
                        if (mHolder instanceof StickerItem.ViewHolder || mHolder instanceof AnimatedStickerItem.ViewHolder) {

                        } else {
                            downLoadThumbnail(holder);
                        }
                    }
                }
            }

            if (hasProgress(holder)) {
                final MessageProgress _Progress = ((IProgress) holder).getProgress();
                _Progress.setTag(mMessage.getMessageId());
                _Progress.setVisibility(View.GONE);

                if (mHolder instanceof StickerItem.ViewHolder || mHolder instanceof AnimatedStickerItem.ViewHolder)
                    return;

                AppUtils.setProgresColor(_Progress.progressBar);

                _Progress.withOnMessageProgress(new OnMessageProgressClick() {
                    @Override
                    public void onMessageProgressClick(MessageProgress progress) {
                        forOnCLick(holder);
                    }
                });

                if (!structMessage.getAttachment().isFileExistsOnLocal()) {
                    if (HelperCheckInternetConnection.currentConnectivityType == null) {
                        checkAutoDownload(holder, holder.itemView.getContext(), HelperCheckInternetConnection.ConnectivityType.WIFI);
                        checkAutoDownload(holder, holder.itemView.getContext(), HelperCheckInternetConnection.ConnectivityType.MOBILE);
                    } else {
                        checkAutoDownload(holder, holder.itemView.getContext(), HelperCheckInternetConnection.currentConnectivityType);
                    }
                }

                _Progress.withOnProgress(new OnProgress() {
                    @Override
                    public void onProgressFinished() {
                        onProgressFinish(holder, messageType);
                    }
                });

                prepareProgress(holder);
            }

        }
    }

    public void onProgressFinish(VH holder, ProtoGlobal.RoomMessageType messageType) {

        final MessageProgress _Progress = ((IProgress) holder).getProgress();

        if (_Progress.getTag() == null || !_Progress.getTag().equals(mMessage.getMessageId())) {
            return;
        }

        if (Uploader.getInstance().isCompressingOrUploading(mMessage.getMessageId() + "") || Downloader.getInstance(currentAccount).isDownloading(structMessage.getAttachment().getCacheId())) {
            return;
        }

        if (structMessage.getAttachment() == null || !structMessage.getAttachment().isFileExistsOnLocal()) {
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
            case VOICE:
            case AUDIO_TEXT:
                break;
            case FILE:
            case FILE_TEXT:
            case IMAGE:
            case IMAGE_TEXT:
                thumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forOnCLick(holder);
                    }
                });
                break;
            case GIF:
            case GIF_TEXT:
                thumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forOnCLick(holder);
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


    private void autoDownload(VH holder) {
        if (mMessage.getMessageType() == ProtoGlobal.RoomMessageType.FILE || mMessage.getMessageType() == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            ((IThumbNailItem) holder).getThumbNailImageView().setVisibility(View.INVISIBLE);
        }

        downLoadFile(holder, 0);
    }

    public boolean hasFileSize(String filPath) {
        if (filPath != null) {
            File file = new File(filPath);
            return file.exists() && file.length() > 0;
        }
        return false;
    }

    private void forOnCLick(VH holder) {

        if (FragmentChat.isInSelectionMode) {
            holder.itemView.performLongClick();
            return;
        }

        final MessageProgress progress = ((IProgress) holder).getProgress();
        AppUtils.setProgresColor(progress.progressBar);

        View thumbnail = ((IThumbNailItem) holder).getThumbNailImageView();


        if (Uploader.getInstance().isCompressingOrUploading(mMessage.getMessageId() + "")) {
            if (mMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.FAILED.toString()) && hasFileSize(structMessage.getAttachment().getLocalFilePath())) {
                if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
                    messageClickListener.onFailedMessageClick(progress, structMessage, holder.getAdapterPosition());

                } else {
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                }
            } else {
                messageClickListener.onUploadOrCompressCancel(progress, structMessage, holder.getAdapterPosition());
            }
        } else if (Downloader.getInstance(currentAccount).isDownloading(structMessage.getAttachment().getCacheId())) {
            Downloader.getInstance(currentAccount).cancelDownload(structMessage.getAttachment().getCacheId());
        } else {
            thumbnail.setVisibility(View.VISIBLE);


            if (structMessage.getAttachment().isFileExistsOnLocal()) {
                String _status = mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getStatus() : mMessage.getStatus();
                ProtoGlobal.RoomMessageType _type = mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getMessageType() : mMessage.getMessageType();

                if (_status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                    messageClickListener.onFailedMessageClick(progress, structMessage, holder.getAdapterPosition());
                } else {
                    /**
                     * avoid from show GIF in fragment show image
                     */
                    if (_type == ProtoGlobal.RoomMessageType.GIF || _type == ProtoGlobal.RoomMessageType.GIF_TEXT) {
                        try {
                            onPlayPauseGIF(holder, structMessage.getAttachment().getLocalFilePath());
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                    } else {
                        messageClickListener.onOpenClick(progress, structMessage, holder.getAdapterPosition());
                    }
                }
            } else {
                downLoadFile(holder, 2);
            }
        }
    }

    @Override
    @CallSuper
    public void onLoadThumbnailFromLocal(VH holder, String tag, String localPath, LocalFileType fileType) {

    }

    private void downLoadThumbnail(final VH holder) {

        if (structMessage.getAttachment() == null) return;

        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;

        if (structMessage.getAttachment().getCacheId() == null || structMessage.getAttachment().getCacheId().length() == 0) {
            return;
        }

        DownloadObject fileStruct = DownloadObject.createForThumb(mMessage, false);
        if (fileStruct != null) {
            Downloader.getInstance(currentAccount).download(fileStruct, selector, null);
        }
    }

    void downLoadFile(final VH holder, int priority) {
        if (structMessage.getAttachment() == null || structMessage.getAttachment().getCacheId() == null) {
            return;
        }

        boolean _isDownloading = Downloader.getInstance(currentAccount).isDownloading(structMessage.getAttachment().getCacheId());

        final MessageProgress progressBar = ((IProgress) holder).getProgress();
        AppUtils.setProgresColor(progressBar.progressBar);

        final TextView textView = ((IProgress) holder).getProgressTextView();
        final String tempValue = ((IProgress) holder).getTempTextView();

        final String token = structMessage.getAttachment().getToken();
        String name = structMessage.getAttachment().getName();
        long size = structMessage.getAttachment().getSize();
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

        final ProtoGlobal.RoomMessageType messageType = mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getMessageType() : mMessage.getMessageType();


        if (token != null && token.length() > 0 && size > 0) {

            progressBar.setVisibility(View.VISIBLE);
            progressBar.withDrawable(R.drawable.ic_cancel, false);

            DownloadObject struct = DownloadObject.createForRoomMessage(mMessage);

            if (struct == null)
                return;

            progressBar.withProgress(1);

            Downloader.getInstance(currentAccount).download(struct, selector, priority, arg -> {
                if (FragmentChat.canUpdateAfterDownload) {
                    G.handler.post(() -> {
                        switch (arg.status) {
                            case SUCCESS:
                            case LOADING:
                                if (arg.data == null) {
                                    return;
                                }
                                if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.getMessageId())) {
                                    if (structMessage.getAttachment() == null || !structMessage.getAttachment().isFileExistsOnLocal()) {
                                        if (arg.data.getProgress() != 100) {
                                            progressBar.withProgress(arg.data.getProgress());
                                            if (textView != null) {
                                                String percent;
                                                if (G.selectedLanguage.equals("fa")) {
                                                    percent = HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(arg.data.getProgress()));
                                                } else {
                                                    percent = String.valueOf(arg.data.getProgress());
                                                }
                                                textView.setText(String.format(Locale.US, "%s %s", percent, "%" + " " + "—" + " " + AndroidUtils.humanReadableByteCount(size, true)));
                                            }
                                        } else {
                                            progressBar.withProgress(99);
                                        }

                                    }

                                    if (arg.data.getProgress() == 100) {
                                        int position = mAdapter.getPosition(this);
                                        mAdapter.notifyItemChanged(position);
                                        if (messageType == ProtoGlobal.RoomMessageType.AUDIO || messageType == ProtoGlobal.RoomMessageType.AUDIO_TEXT || messageType == ProtoGlobal.RoomMessageType.VOICE) {
                                            if (mMessage.getRoomId() == MusicPlayer.roomId) {
                                                MusicPlayer.downloadNewItem = true;
                                            }
                                        }
                                        if (textView != null) {
                                            textView.setText(AndroidUtils.humanReadableByteCount(size, true));
                                        }
                                    }
                                }
                                break;
                            case ERROR:
                                if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.getMessageId())) {
                                    progressBar.withProgress(1);
                                    progressBar.withDrawable(R.drawable.ic_download, true);
                                }
                                if (textView != null) {
                                    textView.setText(AndroidUtils.humanReadableByteCount(size, true));
                                }
                        }
                    });
                }
            });

            if (!_isDownloading) {
                messageClickListener.onDownloadAllEqualCashId(structMessage.getAttachment().getCacheId(), mMessage.getMessageId() + "");
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
    public void prepareProgress(final VH holder) {
        if (structMessage.getSendType() == MyType.SendType.send) {

            final MessageProgress progressBar = ((IProgress) holder).getProgress();
            AppUtils.setProgresColor(progressBar.progressBar);

            progressBar.setVisibility(View.VISIBLE);
            progressBar.withDrawable(R.drawable.ic_cancel, false);

            /**
             * update progress when user trying to upload or download also if
             * file is compressing do this action for add listener and use later
             */
            if (Uploader.getInstance().isCompressingOrUploading(mMessage.getMessageId() + "") || (mMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString()) /*|| FragmentChat.compressingFiles.containsKey(mMessage.getMessageId())*/)) {
                //(mMessage.status.equals(ProtoGlobal.RoomMessageStatus.SENDING.toString()) this code newly added
                hideThumbnailIf(holder);

                ((IProgress) holder).getProgress().setVisibility(View.VISIBLE);
                progressBar.withProgress(Uploader.getInstance().getUploadProgress(mMessage.getMessageId() + ""));
            } else {
                checkForDownloading(holder);
            }

            String _status = mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getStatus() : mMessage.getStatus();
            if (_status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                onFaildUpload(holder);
            }
        } else {
            checkForDownloading(holder);
        }
    }

    private void onFaildUpload(VH holder) {

        MessageProgress progressBar = ((IProgress) holder).getProgress();
        if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.getMessageId())) {
            AppUtils.setProgresColor(progressBar.progressBar);
            progressBar.withProgress(1);
            progressBar.withDrawable(R.drawable.upload, true);
        }
    }

    private void hideThumbnailIf(VH holder) {
        if (mMessage.getMessageType() == ProtoGlobal.RoomMessageType.FILE || mMessage.getMessageType() == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            ((IThumbNailItem) holder).getThumbNailImageView().setVisibility(View.INVISIBLE);
        }
    }

    private void checkForDownloading(VH holder) {

        MessageProgress progress = ((IProgress) holder).getProgress();
        AppUtils.setProgresColor(progress.progressBar);

        if (Downloader.getInstance(currentAccount).isDownloading(structMessage.getAttachment().getCacheId())) {
            hideThumbnailIf(holder);

            downLoadFile(holder, 0);
        } else {
            if (structMessage.getAttachment().isFileExistsOnLocal()) {
                if (!(mMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString()) && !(mMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.FAILED.toString())))) {
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
        if (mMessage.realmRoomMessage.getForwardMessage() != null && mMessage.realmRoomMessage.getForwardMessage().getAttachment() != null && mMessage.realmRoomMessage.getForwardMessage().getAttachment().getCacheId() != null) {
            return mMessage.realmRoomMessage.getForwardMessage().getAttachment().getCacheId();
        } else if (structMessage.getAttachment() != null && structMessage.getAttachment().getCacheId() != null) {
            return structMessage.getAttachment().getCacheId();
        }

        return "";
    }

    public void onBotBtnClick(View v, ButtonEntity buttonEntity) {
        DbManager.getInstance().doRealmTask(realm -> {
            try {
                if (v.getId() == ButtonActionType.USERNAME_LINK) {
                    //TODO: fixed this and do not use G.currentActivity
                    HelperUrl.checkUsernameAndGoToRoomWithMessageId(G.currentActivity, ((ArrayList<String>) v.getTag()).get(0).substring(1), HelperUrl.ChatEntry.chat, 0);
                } else if (v.getId() == ButtonActionType.BOT_ACTION) {

                    long messageId = System.currentTimeMillis();
                    RealmRoomMessage roomMessage = new RealmRoomMessage();
                    roomMessage.setMessageId(messageId);
                    roomMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                    roomMessage.setRoomId(mMessage.getRoomId());
                    roomMessage.setMessage(((ArrayList<String>) v.getTag()).get(1));
                    roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                    roomMessage.setUserId(AccountManager.getInstance().getCurrentUser().getId());
                    roomMessage.setCreateTime(TimeUtils.currentLocalTime());

                    if (((ArrayList<String>) v.getTag()).get(2) != null) {
                        RealmAdditional additional = new RealmAdditional();
                        additional.setId(AppUtils.makeRandomId());
                        additional.setAdditionalData(((ArrayList<String>) v.getTag()).get(2));
                        additional.setAdditionalType(3);
                        roomMessage.setRealmAdditional(additional);
                    }

                    new Thread(() -> {
                        DbManager.getInstance().doRealmTransaction(realm12 -> {
                            realm12.copyToRealmOrUpdate(roomMessage);
                        });
                    }).start();

                    ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).build(type, mMessage.getRoomId(), roomMessage);
                    messageClickListener.sendFromBot(roomMessage);

                } else if (v.getId() == ButtonActionType.JOIN_LINK) {
                    //TODO: fixed this and do not use G.currentActivity
                    HelperUrl.checkAndJoinToRoom(G.currentActivity, ((ArrayList<String>) v.getTag()).get(0).substring(14));

                } else if (v.getId() == ButtonActionType.WEB_LINK) {
                    HelperUrl.openBrowser(((ArrayList<String>) v.getTag()).get(0));

                } else if (v.getId() == ButtonActionType.WEBVIEW_LINK) {
                    messageClickListener.sendFromBot(((ArrayList<String>) v.getTag()).get(0));
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
                                        RealmRoomMessage realmRoomMessage = RealmRoomMessage.makeAdditionalData(mMessage.getRoomId(), identity, realmUserInfo.getUserInfo().getPhoneNumber(), realmUserInfo.getUserInfo().getPhoneNumber(), 0, realm, ProtoGlobal.RoomMessageType.TEXT);
                                        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).build(type, mMessage.getRoomId(), realmRoomMessage);
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
                    RealmRoom room = realm.where(RealmRoom.class).equalTo("id", mMessage.getRoomId()).findFirst();
                    long peerId;
                    if (room != null && room.getChatRoom() != null) {
                        peerId = room.getChatRoom().getPeerId();
                    } else {
                        peerId = mMessage.getUserId();
                    }
                    new HelperFragment(G.currentActivity.getSupportFragmentManager()).loadPayment(room.getTitle(), jsonObject.getString("token"), null);
                } else if (v.getId() == ProtoGlobal.DiscoveryField.ButtonActionType.CARD_TO_CARD.getNumber()) {
                    JSONObject rootJsonObject = new JSONObject(buttonEntity.getJsonObject());
                    JSONObject valueObject = rootJsonObject.getJSONObject("value");

                    String cardNumber = valueObject.getString("cardNumber");
                    int amount = valueObject.getInt("amount");
                    long userId = valueObject.getLong("userId");

                    CardToCardHelper.NewCallCardToCard(G.currentActivity, userId, amount, cardNumber);

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
        });

        /**
         * The data was sent via the button via the view tag. Right now I only do this for the card due to lack of time with the new object
         * */
    }

    @FunctionalInterface
    public interface OnAllowBotCommand {
        void allow();
    }
}
