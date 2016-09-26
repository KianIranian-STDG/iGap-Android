package com.iGap.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;

import com.iGap.R;
import com.iGap.adapter.items.chat.AbstractChatItem;
import com.iGap.interface_package.OnChatMessageRemove;
import com.iGap.interface_package.OnChatMessageSelectionChanged;
import com.iGap.interface_package.OnMessageClick;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public class ChatMessagesFastAdapter<Item extends AbstractChatItem> extends FastItemAdapter<Item> implements FastAdapter.OnLongClickListener<Item> {
    private OnChatMessageSelectionChanged<Item> onChatMessageSelectionChanged;
    private OnMessageClick onMessageClick;
    private OnChatMessageRemove onChatMessageRemove;

    private OnLongClickListener longClickListener = new OnLongClickListener<Item>() {
        @Override
        public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {
            if (onChatMessageSelectionChanged != null) {
                onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
            }
            return true;
        }
    };

    /**
     * update message text
     *
     * @param messageId   message id
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
     * NOTE: it needs rewriting, because currently updates whole item view not just the progress (almost)
     *
     * @param fileIdentity
     * @param progress
     */
    public void updateProgress(byte[] fileIdentity, int progress) {
        Item item = getItemByFileIdentity(fileIdentity);
        if (item != null && item.mMessage.uploadProgress < progress) {
            int pos = getAdapterItems().indexOf(item);
            item.mMessage.uploadProgress = progress;
            set(pos, item);
        }
    }

    /**
     * get item by its file hash
     * useful for finding item which tries to upload something
     *
     * @param fileIdentity String
     * @return Item
     */
    public Item getItemByFileIdentity(byte[] fileIdentity) {
        for (Item item : getAdapterItems()) {
            if (item.mMessage.needsUpload() && item.mMessage.fileHash == fileIdentity) {
                return item;
            }
        }
        return null;
    }

    public ChatMessagesFastAdapter(OnChatMessageSelectionChanged<Item> OnChatMessageSelectionChangedListener, final OnMessageClick onMessageClickListener, final OnChatMessageRemove chatMessageRemoveListener) {
        onChatMessageSelectionChanged = OnChatMessageSelectionChangedListener;
        onMessageClick = onMessageClickListener;
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
                    if (onMessageClick != null) {
                        onMessageClick.onMessageClick(v, item.mMessage, position);
                    }
                } /*else {
                    select(position);
                    onLongClick(v, adapter, item, position);
                    longClickListener.onLongClick(v, adapter, item, position);
                }*/
                // TODO: 9/17/2016 [Alireza Eskandarpour Shoferi] implement
                return false;
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
     * @param status    ProtoGlobal.RoomMessageStatus
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

    public void updateItemFileHash(long messageId, byte[] fileHash) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                int pos = items.indexOf(messageInfo);
                messageInfo.mMessage.fileHash = fileHash;
                set(pos, messageInfo);
                break;
            }
        }
    }

    /**
     * update message id and its status
     *
     * @param messageId new message id
     * @param identity  old manually defined as identity id
     * @param status    ProtoGlobal.RoomMessageStatus
     */
    public void updateMessageIdAndStatus(long messageId, String identity, ProtoGlobal.RoomMessageStatus status) {
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

        if (onChatMessageSelectionChanged != null) {
            onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
        }
    }

    @Override
    public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {
        // don't remove following casting because FrameLayout has setForeground() from API 1 but
        // View has it from API 23 and Lint doesn't get it correctly!
        if (!item.isSelected()) {
            //noinspection RedundantCast
            ((FrameLayout) v).setForeground(new ColorDrawable(v.getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
        } else {
            //noinspection RedundantCast
            ((FrameLayout) v).setForeground(new ColorDrawable(Color.TRANSPARENT));
        }
        return false;
    }
}
