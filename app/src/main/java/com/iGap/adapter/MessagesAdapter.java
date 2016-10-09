package com.iGap.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;

import com.iGap.R;
import com.iGap.adapter.items.chat.AbstractChatItem;
import com.iGap.interface_package.OnChatMessageRemove;
import com.iGap.interface_package.OnChatMessageSelectionChanged;
import com.iGap.interface_package.OnMessageViewClick;
import com.iGap.module.StructMessageAttachment;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public class MessagesAdapter<Item extends AbstractChatItem> extends FastItemAdapter<Item> implements FastAdapter.OnLongClickListener<Item> {
    private OnChatMessageSelectionChanged<Item> onChatMessageSelectionChanged;
    private OnMessageViewClick onMessageViewClick;
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

    public void updateDownloadFields(String token, int progress, int offset) {
        for (Item item : getAdapterItems()) {
            if (item.mMessage.downloadAttachment != null && item.mMessage.downloadAttachment.token.equalsIgnoreCase(token)) {
                int pos = getAdapterItems().indexOf(item);
                item.mMessage.downloadAttachment.offset = offset;
                item.mMessage.downloadAttachment.progress = progress;
                item.onRequestDownloadFile(offset, progress);

                notifyItemChanged(pos);
                break;
            }
        }
    }

    public void updateThumbnail(String token) {
        for (Item item : getAdapterItems()) {
            if (item.mMessage.downloadAttachment != null && item.mMessage.downloadAttachment.token.equalsIgnoreCase(token)) {
                int pos = getAdapterItems().indexOf(item);
                notifyItemChanged(pos);
                break;
            }
        }
    }

    public void updateChatAvatar(long userId, StructMessageAttachment avatar) {
        for (Item item : getAdapterItems()) {
            if (!item.mMessage.isSenderMe() && item.mMessage.senderID.equalsIgnoreCase(Long.toString(userId))) {
                int pos = getAdapterItems().indexOf(item);
                item.mMessage.senderAvatar = avatar;
                notifyItemChanged(pos);
            }
        }
    }

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
     * @param messageId
     * @param progress
     */
    public void updateProgress(long messageId, int progress) {
        Item item = getItemByFileIdentity(messageId);
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

    public MessagesAdapter(OnChatMessageSelectionChanged<Item> OnChatMessageSelectionChangedListener, final OnMessageViewClick onMessageViewClickListener, final OnChatMessageRemove chatMessageRemoveListener) {
        onChatMessageSelectionChanged = OnChatMessageSelectionChangedListener;
        onMessageViewClick = onMessageViewClickListener;
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
                    if (onMessageViewClick != null) {
                        onMessageViewClick.onMessageFileClick(v, item.mMessage, position, ProtoGlobal.RoomMessageType.TEXT);
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
}
