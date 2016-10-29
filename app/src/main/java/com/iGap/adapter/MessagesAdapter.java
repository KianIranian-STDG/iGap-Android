package com.iGap.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.FrameLayout;
import com.iGap.R;
import com.iGap.adapter.items.chat.AbstractMessage;
import com.iGap.interfaces.IMessageItem;
import com.iGap.interfaces.OnChatMessageRemove;
import com.iGap.interfaces.OnChatMessageSelectionChanged;
import com.iGap.module.StructMessageAttachment;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public class MessagesAdapter<Item extends AbstractMessage> extends FastItemAdapter<Item>
    implements FastAdapter.OnLongClickListener<Item> {
    private OnChatMessageSelectionChanged<Item> onChatMessageSelectionChanged;
    private IMessageItem iMessageItem;
    private OnChatMessageRemove onChatMessageRemove;
    // contain sender id
    public static List<String> avatarsRequested = new ArrayList<>();
    public static List<String> usersInfoRequested = new ArrayList<>();
    public static ArrayMap<Long, Integer> uploading = new ArrayMap<>();
    public static ArrayMap<String, Integer> downloading = new ArrayMap<>();

    private OnLongClickListener longClickListener = new OnLongClickListener<Item>() {
        @Override
        public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {
            if (onChatMessageSelectionChanged != null) {
                onChatMessageSelectionChanged.onChatMessageSelectionChanged(
                    getSelectedItems().size(), getSelectedItems());
            }
            return true;
        }
    };

    public void downloadingAvatar(long peerId, int progress, int offset,
        StructMessageAttachment avatar) {
        for (Item item : getAdapterItems()) {
            if (item.mMessage.downloadAttachment != null
                && Long.parseLong(item.mMessage.senderID) == peerId) {
                int pos = getAdapterItems().indexOf(item);
                item.mMessage.senderAvatar = avatar;
                item.mMessage.downloadAttachment.progress = progress;
                item.mMessage.downloadAttachment.offset = offset;
                item.onRequestDownloadAvatar(offset, progress);
                notifyItemChanged(pos);
            }
        }
    }

    public void updateDownloadFields(String token, int progress, int offset) {
        for (Item item : getAdapterItems()) {
            if (item.mMessage.downloadAttachment != null
                && item.mMessage.downloadAttachment.token.equalsIgnoreCase(token)) {
                int pos = getAdapterItems().indexOf(item);
                item.mMessage.downloadAttachment.offset = offset;
                item.mMessage.downloadAttachment.progress = progress;

                if (!downloading.containsKey(token)) {
                    downloading.put(token, progress);
                } else {
                    int pos2 = downloading.indexOfKey(token);
                    downloading.setValueAt(pos2, progress);
                }

                item.onRequestDownloadFile(offset, progress);

                notifyItemChanged(pos);
            }
        }
    }

    public void updateThumbnail(String token) {
        for (Item item : getAdapterItems()) {
            if (item.mMessage.downloadAttachment != null
                && item.mMessage.downloadAttachment.token.equalsIgnoreCase(token)) {
                int pos = getAdapterItems().indexOf(item);
                notifyItemChanged(pos);
            }
        }
    }

    public void updateChatAvatar(long userId, RealmRegisteredInfo registeredInfo) {
        for (Item item : getAdapterItems()) {
            if (!item.mMessage.isSenderMe() && item.mMessage.senderID.equalsIgnoreCase(
                Long.toString(userId))) {
                int pos = getAdapterItems().indexOf(item);
                item.mMessage.senderAvatar =
                    StructMessageAttachment.convert(registeredInfo.getLastAvatar());
                item.mMessage.initials = registeredInfo.getInitials();
                item.mMessage.senderColor = registeredInfo.getColor();
                notifyItemChanged(pos);
            }
        }
    }

    /**
     * update message text
     *
     * @param messageId message id
     * @param updatedText new message text
     */
    public void updateMessageText(long messageId, String updatedText) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                int pos = items.indexOf(messageInfo);
                messageInfo.mMessage.messageText = updatedText;
                messageInfo.mMessage.isEdited = true;
                set(pos, messageInfo);
                break;
            }
        }
    }

    /**
     * update progress while file uploading
     * NOTE: it needs rewriting, because currently updates whole item view not just the progress
     * (almost)
     */
    public void updateProgress(long messageId, int progress) {
        Item item = getItemByFileIdentity(messageId);
        if (item != null && uploading.get(messageId) < progress) {
            int pos = getAdapterItems().indexOf(item);
            item.mMessage.uploadProgress = progress;

            if (!uploading.containsKey(messageId)) {
                uploading.put(messageId, progress);
            } else {
                int pos2 = uploading.indexOfKey(messageId);
                uploading.setValueAt(pos2, progress);
            }

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
            if (item.mMessage.messageID.equalsIgnoreCase(Long.toString(messageId))) {
                return item;
            }
        }
        return null;
    }

    public MessagesAdapter(
        OnChatMessageSelectionChanged<Item> OnChatMessageSelectionChangedListener,
        final IMessageItem iMessageItemListener,
        final OnChatMessageRemove chatMessageRemoveListener) {
        onChatMessageSelectionChanged = OnChatMessageSelectionChangedListener;
        iMessageItem = iMessageItemListener;
        onChatMessageRemove = chatMessageRemoveListener;

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
                if (getSelectedItems().size() == 0) {
                    if (iMessageItem != null) {
                        iMessageItem.onContainerClick(v, item.mMessage, position);
                    }
                } /*else {
                    if (!item.isSelected()){
                        select(position);
                        makeSelected(v);
                    }
                    else{
                        deselect(position);
                        makeDeselected(v);
                    }
                }*/
                // TODO: 9/17/2016 [Alireza Eskandarpour Shoferi] implement
                return true;
            }
        });
    }

    public void removeMessage(long messageId) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                int pos = items.indexOf(messageInfo);
                if (onChatMessageRemove != null) {
                    onChatMessageRemove.onPreChatMessageRemove(messageInfo.mMessage, pos);
                }
                remove(pos);
                break;
            }
        }
    }

    /**
     * update message status
     *
     * @param messageId message id
     * @param status ProtoGlobal.RoomMessageStatus
     */
    public void updateMessageStatus(long messageId, ProtoGlobal.RoomMessageStatus status) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                int pos = items.indexOf(messageInfo);
                messageInfo.mMessage.status = status.toString();
                set(pos, messageInfo);
                break;
            }
        }
    }

    /**
     * update message id and its status
     *
     * @param messageId new message id
     * @param identity old manually defined as identity id
     * @param status ProtoGlobal.RoomMessageStatus
     */
    public void updateMessageIdAndStatus(long messageId, String identity,
        ProtoGlobal.RoomMessageStatus status) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage.messageID.equals(identity)) {
                int pos = items.indexOf(messageInfo);
                messageInfo.mMessage.status = status.toString();
                messageInfo.mMessage.messageID = Long.toString(messageId);
                set(pos, messageInfo);
                break;
            }
        }
    }

    @Override public void notifyAdapterItemRemoved(int position) {
        super.notifyAdapterItemRemoved(position);

        if (onChatMessageSelectionChanged != null) {
            onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(),
                getSelectedItems());
        }
    }

    @Override public void deselect() {
        super.deselect();

        if (onChatMessageSelectionChanged != null) {
            onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(),
                getSelectedItems());
        }
    }

    private void makeSelected(View v) {
        //noinspection RedundantCast
        ((FrameLayout) v).setForeground(
            new ColorDrawable(v.getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
    }

    private void makeDeselected(View v) {
        //noinspection RedundantCast
        ((FrameLayout) v).setForeground(new ColorDrawable(Color.TRANSPARENT));
    }

    public static boolean hasDownloadRequested(String token) {
        return downloading.containsKey(token);
    }

    public static boolean hasUploadRequested(long messageId) {
        return uploading.containsKey(messageId);
    }

    @Override public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {
        if (!item.isSelected()) {
            makeSelected(v);
        } else {
            makeDeselected(v);
        }
        return false;
    }
}
