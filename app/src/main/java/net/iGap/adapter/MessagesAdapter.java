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
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.observers.interfaces.OnChatMessageRemove;
import net.iGap.observers.interfaces.OnChatMessageSelectionChanged;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

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
                if (iMessageItem != null && item.mMessage != null && item.mMessage.getUserId() != -1) {

                    if (item.mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString()) || item.mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {

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
                        if (item.mMessage != null && item.mMessage.getStatus() != null && !item.mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
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
            if (item.mMessage != null) {
                if (item.mMessage.getMessageId() == messageId) {
                    item = null;
                    return i;
                } else if (item.mMessage.getForwardMessage() != null) {
                    if (item.mMessage.getForwardMessage().getMessageId() == messageId) {
                        item = null; // set null for clear memory, is it true?
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public List<StructMessageInfo> getFailedMessages() {
        List<StructMessageInfo> failedMessages = new ArrayList<>();
        for (Item item : getAdapterItems()) {
            if (item.mMessage != null && item.mMessage.getUserId() != -1 && item.mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                failedMessages.add(item.structMessage);
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

            if (item.mMessage != null) {
                if (item.mMessage.getMessageId() == messageId) {
                    item.mMessage.setMessage(updatedText);
                    item.mMessage.setEdited(true);

                    if (item.mMessage.getForwardMessage() != null) {
                        item.mMessage.getForwardMessage().setLinkInfo(HelperUrl.getLinkInfo(updatedText));
                    } else {
                        item.mMessage.setLinkInfo(HelperUrl.getLinkInfo(updatedText));
                    }
                    item.mMessage.setHasMessageLink(item.mMessage.getLinkInfo() != null && item.mMessage.getLinkInfo().length() > 0);
                    RealmRoomMessage.isEmojiInText(item.mMessage, item.mMessage.getMessage());

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
    public void updateVote(long roomId, long messageId, String vote, ProtoGlobal.RoomMessageReaction reaction, long forwardedMessageId) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage != null) {
                /**
                 * if not forwarded message update structure otherwise just notify position
                 * mainMessageId == 0 means that this message not forwarded
                 */

                // TODO: 5/16/20 must change vote structure
                if (messageInfo.mMessage.getMessageId() == messageId && (forwardedMessageId != 0 || messageInfo.mMessage.getRoomId() == roomId)) {
                    int pos = items.indexOf(messageInfo);
                    if (messageInfo.structMessage.getChannelExtra() != null) {
                        if (reaction == ProtoGlobal.RoomMessageReaction.THUMBS_UP) {
                            messageInfo.structMessage.getChannelExtra().setThumbsUp(vote);
                        } else if (reaction == ProtoGlobal.RoomMessageReaction.THUMBS_DOWN) {
                            messageInfo.structMessage.getChannelExtra().setThumbsDown(vote);
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
    public void updateMessageState(long messageId, String voteUp, String voteDown, String viewsLabel) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage != null) {
                /**
                 * when i add message to RealmRoomMessage(putOrUpdate) set (replyMessageId * (-1))
                 * so i need to (replyMessageId * (-1)) again for use this messageId
                 */
                if (
                        (messageInfo.mMessage.getForwardMessage() == null && messageInfo.mMessage.getMessageId() == messageId)
                                || (messageInfo.mMessage.getForwardMessage() != null && (messageInfo.mMessage.getForwardMessage().getMessageId() * (-1)) == messageId)
                ) {
                    int pos = items.indexOf(messageInfo);
                    if (messageInfo.structMessage.getChannelExtra() != null) {
                        messageInfo.structMessage.getChannelExtra().setThumbsUp(voteUp);
                        messageInfo.structMessage.getChannelExtra().setThumbsDown(voteDown);
                        messageInfo.structMessage.getChannelExtra().setViewsLabel(viewsLabel);
                    }
                    set(pos, messageInfo);
                    break;
                }
            }
        }
    }

    public void updateToken(long messageId, String token) {
        Item item = getItemByFileIdentity(messageId);
        if (item != null) {
            int pos = getAdapterItems().indexOf(item);
            item.structMessage.getAttachment().setToken(token);

            set(pos, item);
        }
    }

    /**
     * get item by its file hash
     * useful for finding item which tries to upload something
     *
     * @param messageId String
     * @return Item
     */
    public Item getItemByFileIdentity(long messageId) {
        for (Item item : getAdapterItems()) {
            if (item != null) {
                if (item.mMessage != null) {
                    if (item.mMessage.getMessageId() == messageId) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public long getItemByPosition(int position) {
        try {
            return getAdapterItem(position).mMessage.getUpdateOrCreateTime();
        } catch (Exception e) {
        }
        return 0;

    }

    public void removeMessage(long messageId) {

        for (int i = getAdapterItemCount() - 1; i >= 0; i--) {
            Item item = getAdapterItem(i);
            if (item.mMessage != null) {
                if (item.mMessage.getMessageId() == messageId) {
                    if (onChatMessageRemove != null) {
                        onChatMessageRemove.onPreChatMessageRemove(item.structMessage, i);
                    }
                    remove(i);
                    break;
                }
            }
        }
    }

    public void removeMessage(int pos) {
        if (onChatMessageRemove != null) {
            AbstractMessage message = getAdapterItem(pos);
            onChatMessageRemove.onPreChatMessageRemove(message.structMessage, pos);
        }
        remove(pos);
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
            if (messageInfo.mMessage != null) {
                if (messageInfo.mMessage.getMessageId() == messageId) {
                    messageInfo.mMessage.setStatus(status.toString());
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
            if (messageInfo.mMessage != null) {
                if ((messageInfo.mMessage.getMessageId() + "").equals(identity)) {
                    messageInfo.mMessage.setStatus(status.toString());
                    messageInfo.mMessage.setMessageId(messageId);
                    set(i, messageInfo);
                    break;
                }
            }
        }
    }

    /**
     * update video message time and name after that compressed file
     *
     * @param messageId    for find message in adapter
     * @param fileDuration new duration for set in item
     * @param fileSize     new size for set in item
     */
    public void updateVideoInfo(long messageId, long fileDuration, long fileSize) {
        List<Item> items = getAdapterItems();
        for (int i = items.size() - 1; i >= 0; i--) {
            Item messageInfo = items.get(i);
            if (messageInfo.mMessage != null) {
                if (messageInfo.mMessage.getMessageId() == messageId) {
                    messageInfo.structMessage.getAttachment().setDuration(fileDuration);
                    messageInfo.structMessage.getAttachment().setSize(fileSize);
                    //messageInfo.mMessage.attachment.compressing = ""; // commented here because in video item we update compress text
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

                if ((messageInfo.mMessage.getMessageId() + "").equals(messageId)) {
                    messageInfo.structMessage.isSelected = select;
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
