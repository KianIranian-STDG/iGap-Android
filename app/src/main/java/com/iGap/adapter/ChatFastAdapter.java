package com.iGap.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.iGap.R;
import com.iGap.adapter.items.chat.AbstractChatItem;
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
public class ChatFastAdapter<Item extends AbstractChatItem> extends FastItemAdapter<Item> implements FastAdapter.OnLongClickListener<Item> {
    private OnChatMessageSelectionChanged<Item> onChatMessageSelectionChanged;
    private OnMessageClick onMessageClick;

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
                set(pos, messageInfo);
                break;
            }
        }
    }

    public ChatFastAdapter(OnChatMessageSelectionChanged<Item> OnChatMessageSelectionChangedListener, final OnMessageClick onMessageClickListener) {
        onChatMessageSelectionChanged = OnChatMessageSelectionChangedListener;
        onMessageClick = onMessageClickListener;

        // as we provide id's for the items we want the hasStableIds enabled to speed up things
        setHasStableIds(true);

        withSelectable(true);
        withMultiSelect(true);
        withSelectOnLongClick(true);
        withOnPreLongClickListener(this);
        withOnLongClickListener(new OnLongClickListener<Item>() {
            @Override
            public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {
                if (onChatMessageSelectionChanged != null) {
                    onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
                }
                return true;
            }
        });
        withOnClickListener(new OnClickListener<Item>() {
            @Override
            public boolean onClick(View v, IAdapter<Item> adapter, Item item, int position) {
                if (onMessageClickListener != null) {
                    onMessageClickListener.onMessageClick(v, item.mMessage, position);
                }
                return false;
            }
        });
    }

    public void removeMessage(long messageId) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                remove(items.indexOf(messageInfo));
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
    public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {
        if (!item.isSelected()) {
            v.setForeground(new ColorDrawable(v.getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
        } else {
            v.setForeground(new ColorDrawable(Color.TRANSPARENT));
        }
        return false;
    }
}
