/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;

import net.iGap.R;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.adapter.items.chat.CardToCardItem;
import net.iGap.adapter.items.chat.GiftStickerItem;
import net.iGap.adapter.items.chat.LogItem;
import net.iGap.adapter.items.chat.LogWallet;
import net.iGap.adapter.items.chat.LogWalletBill;
import net.iGap.adapter.items.chat.LogWalletCardToCard;
import net.iGap.adapter.items.chat.LogWalletTopup;
import net.iGap.adapter.items.chat.TimeItem;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.module.AppUtils;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.observers.interfaces.OnChatMessageRemove;
import net.iGap.observers.interfaces.OnChatMessageSelectionChanged;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.structs.MessageObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.CompositeDisposable;

public class MessagesAdapter<Item extends AbstractMessage> extends FastItemAdapter<Item> implements OnLongClickListener<Item> {
    private OnChatMessageSelectionChanged<Item> onChatMessageSelectionChanged;
    private IMessageItem iMessageItem;
    private OnChatMessageRemove onChatMessageRemove;
    public AvatarHandler avatarHandler;
    private boolean roomIsCloud;
    private RealmRoom realmRoom;

    public CompositeDisposable compositeDisposable;

    private boolean allowAction = true;
    private Timer timer;

    private OnLongClickListener longClickListener = new OnLongClickListener<Item>() {
        @Override
        public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {
            if (item instanceof TimeItem || item instanceof LogItem || item instanceof LogWallet ||
                    item instanceof LogWalletCardToCard || item instanceof CardToCardItem ||
                    item instanceof GiftStickerItem || item instanceof LogWalletTopup ||
                    item instanceof LogWalletBill) {
                if (item.isSelected()) v.performLongClick();
            } else {
                if (iMessageItem != null && item.messageObject != null && item.messageObject.userId != -1) {
                    if (item.messageObject.status == MessageObject.STATUS_SENDING || item.messageObject.status == MessageObject.STATUS_FAILED) {
                        if (item.isSelected()) v.performLongClick();
                        return true;
                    }

                    if (onChatMessageSelectionChanged != null) {
                        onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
                    }
                }
            }

            return true;
        }
    };

    public MessagesAdapter(RealmRoom realmRoom, OnChatMessageSelectionChanged<Item> OnChatMessageSelectionChangedListener, final IMessageItem iMessageItemListener, final OnChatMessageRemove chatMessageRemoveListener, AvatarHandler avatarHandler, CompositeDisposable compositeDisposable, boolean roomIsCloud) {
        this.realmRoom = realmRoom;
        onChatMessageSelectionChanged = OnChatMessageSelectionChangedListener;
        this.compositeDisposable = compositeDisposable;
        iMessageItem = iMessageItemListener;
        onChatMessageRemove = chatMessageRemoveListener;
        this.avatarHandler = avatarHandler;
        this.roomIsCloud = roomIsCloud;
        // as we provide id's for the items we want the hasStableIds enabled to speed up things
        setHasStableIds(true);

        withSelectable(true);
        withMultiSelect(true);
        withSelectOnLongClick(true);
        withOnPreLongClickListener(this);
        withOnLongClickListener(longClickListener);
        withOnClickListener(new OnClickListener<Item>() {
            @Override
            public boolean onClick(View v, IAdapter<Item> adapter, Item item, int position) {
                new CountDownTimer(300, 100) {

                    public void onTick(long millisUntilFinished) {
                        v.setEnabled(false);
                    }

                    public void onFinish() {
                        v.setEnabled(true);
                    }
                }.start();

                AppUtils.closeKeyboard(v);

                if ((item instanceof LogWallet || item instanceof LogWalletCardToCard)) {
                    return false;
                }

                if (getSelectedItems().size() != 0) {
                    if (!(item instanceof TimeItem)) {
                        if (item.messageObject != null && item.messageObject.status != MessageObject.STATUS_SENDING) {
                            v.performLongClick();
                        }
                    }
                }
                return false;
            }
        });

        timer = new Timer();
    }

    public int findPositionByMessageId(long messageId) {
        for (int i = (getAdapterItemCount() - 1); i >= 0; i--) {
            Item item = getItem(i);
            if (item.messageObject != null) {
                if (item.messageObject.id == messageId) {
                    item = null;
                    return i;
                } else if (item.messageObject.isForwarded()) {
                    if (item.messageObject.forwardedMessage.id == messageId) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public List<MessageObject> getFailedMessages() {
        List<MessageObject> failedMessages = new ArrayList<>();
        for (Item item : getAdapterItems()) {
            if (item.messageObject != null && item.messageObject.userId != -1 && item.messageObject.status == MessageObject.STATUS_FAILED) {
                failedMessages.add(item.messageObject);
            }
        }
        return failedMessages;
    }

    /**
     * update message text
     *
     * @param messageId   message id
     * @param updatedText new message text
     */
    public void updateMessageText(long messageId, String updatedText) {

        for (int i = getAdapterItemCount() - 1; i >= 0; i--) {
            Item item = getAdapterItem(i);

            if (item.messageObject != null) {
                if (item.messageObject.id == messageId) {
                    item.messageObject.message = updatedText;
                    item.messageObject.edited = true;// TODO: 12/29/20 MESSAGE_REFACTOR_NEED_TEST

                    if (item.messageObject.isForwarded()) {
                        item.messageObject.forwardedMessage.linkInfo = HelperUrl.getLinkInfo(updatedText);
                    } else {
                        item.messageObject.linkInfo = HelperUrl.getLinkInfo(updatedText);
                    }

                    item.messageObject.hasLink = item.messageObject.linkInfo != null && item.messageObject.linkInfo.length() > 0;
                    // RealmRoomMessage.isEmojiInText(item.messageObject, item.messageObject.message);

                    item.updateMessageText(updatedText);
                    set(i, item);

                    notifyItemChanged(i);

                    break;
                }
            }
        }
    }

    /**
     * update message vote
     *
     * @param forwardedMessageId when forward message from channel to another chats , make new messageId.
     *                           mainMessageId is new messageId that created and messageId is for message
     *                           that forwarded to another chats
     */
    public void updateVote(long roomId, long messageId, String vote, ProtoGlobal.RoomMessageReaction reaction) {

        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.messageObject != null) {
                /**
                 * if not forwarded message update structure otherwise just notify position
                 * mainMessageId == 0 means that this message not forwarded
                 */

                // TODO: 5/16/20 must change vote structure
                if (messageInfo.messageObject.id == messageId && messageInfo.messageObject.roomId == roomId) {
                    int pos = items.indexOf(messageInfo);// TODO: 12/29/20 MESSAGE_REFACTOR_NEED_TEST
                    if (messageInfo.messageObject.channelExtraObject != null) {
                        if (reaction == ProtoGlobal.RoomMessageReaction.THUMBS_UP) {
                            messageInfo.messageObject.channelExtraObject.thumbsUp = vote;
                        } else if (reaction == ProtoGlobal.RoomMessageReaction.THUMBS_DOWN) {
                            messageInfo.messageObject.channelExtraObject.thumbsDown = vote;
                        }
                        set(pos, messageInfo);
                    }
                }
            }
        }
    }

    /**
     * update message state
     */
    public void updateMessageState(long messageId, String voteUp, String voteDown, String
            viewsLabel) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {// TODO: 12/29/20 MESSAGE_REFACTOR_NEED_TEST
            if (messageInfo.messageObject != null) {
                /**
                 * when i add message to RealmRoomMessage(putOrUpdate) set (replyMessageId * (-1))
                 * so i need to (replyMessageId * (-1)) again for use this messageId
                 */
                if (
                        (messageInfo.messageObject.forwardedMessage == null && messageInfo.messageObject.id == messageId)
                                || (messageInfo.messageObject.forwardedMessage != null && (messageInfo.messageObject.forwardedMessage.id * (-1)) == messageId)
                ) {
                    int pos = items.indexOf(messageInfo);
                    if (messageInfo.messageObject.channelExtraObject != null) {
                        messageInfo.messageObject.channelExtraObject.thumbsUp = voteUp;
                        messageInfo.messageObject.channelExtraObject.thumbsDown = voteDown;
                        messageInfo.messageObject.channelExtraObject.viewsLabel = viewsLabel;
                    }
                    set(pos, messageInfo);
                    break;
                }
            }
        }
    }

    public long getItemByPosition(int position) {
        try {
            return getAdapterItem(position).messageObject.getUpdateOrCreateTime();
        } catch (Exception e) {
        }
        return 0;

    }

    public void removeMessage(long messageId) {
        for (int i = getAdapterItemCount() - 1; i >= 0; i--) {
            Item item = getAdapterItem(i);
            if (item.messageObject != null) {
                if (item.messageObject.id == messageId) {
                    remove(i);
                    break;
                }
            }
        }
    }

    /**
     * update message status
     *
     * @param messageId message id
     * @param status    ProtoGlobal.RoomMessageStatus
     */
    public void updateMessageStatus(long messageId, ProtoGlobal.RoomMessageStatus status) {
        List<Item> items = getAdapterItems();

        for (int i = items.size() - 1; i >= 0; i--) {
            Item messageInfo = items.get(i);
            if (messageInfo.messageObject != null) {
                if (messageInfo.messageObject.id == messageId) {
                    messageInfo.messageObject.status = status.getNumber();
                    set(i, messageInfo);
                    break;
                }
            }
        }
    }

    /**
     * update message id and status
     *
     * @param messageId   new message id
     * @param identity    old manually defined as identity id
     * @param status      ProtoGlobal.RoomMessageStatus
     * @param roomMessage
     */
    public void updateMessageIdAndStatus(long messageId, String identity, ProtoGlobal.RoomMessageStatus status, ProtoGlobal.RoomMessage roomMessage) {
        List<Item> items = getAdapterItems();
        for (int i = items.size() - 1; i >= 0; i--) {
            Item messageInfo = items.get(i);
            if (messageInfo.messageObject != null) {
                if ((messageInfo.messageObject.id + "").equals(identity)) {
                    messageInfo.messageObject.status = status.getNumber();
                    messageInfo.messageObject.id = messageId;
                    set(i, messageInfo);
                    break;
                }
            }
        }
    }

    @Override
    public void notifyAdapterItemRemoved(int position) {
        super.notifyAdapterItemRemoved(position);

        if (onChatMessageSelectionChanged != null) {
            onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
        }
    }

    @Override
    public void deselect() {
        super.deselect();
        try {
            if (onChatMessageSelectionChanged != null) {
                onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
            }
        } catch (Exception e) {
        }

    }

    private void makeSelected(View v) {
        //noinspection RedundantCast
        ((FrameLayout) v).setForeground(new ColorDrawable(v.getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
    }

    private void makeDeselected(View v) {
        //noinspection RedundantCast
        ((FrameLayout) v).setForeground(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {
        if (!item.isSelected()) {
            makeSelected(v);
        } else {
            makeDeselected(v);
        }
        return false;
    }

    public void toggleSelection(String messageId, boolean select, RecyclerView recyclerView) {

        List<Item> items = getAdapterItems();
        for (int i = items.size() - 1; i >= 0; i--) {
            Item messageInfo = items.get(i);

            try {

                if ((messageInfo.messageObject.id + "").equals(messageId)) {
                    messageInfo.messageObject.isSelected = select;
                    notifyItemChanged(i);

                    if (select) {
                        recyclerView.scrollToPosition(i);
                    }

                    break;
                }
            } catch (NullPointerException e) {
                // some item can have no messageID
            }
        }
    }

    private void runTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                allowAction = true;
            }
        }, 1000);

    }

    public void onBotButtonClicked(AbstractMessage.OnAllowBotCommand onAllowBotCommand) {
        if (allowAction) {
            allowAction = false;
            runTimer();
            onAllowBotCommand.allow();
        }
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    public boolean roomIsMyCloud() {
        return roomIsCloud;
    }

    public RealmRoom getRealmRoom() {
        return realmRoom;
    }
}
