package com.iGap.adapter;

import com.iGap.adapter.items.RoomItem;
import com.iGap.module.StructMessageAttachment;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public class RoomsAdapter<Item extends RoomItem> extends FastItemAdapter<Item> {
    public RoomsAdapter() {
        // as we provide id's for the items we want the hasStableIds enabled to speed up things
        setHasStableIds(true);
    }

    public void downloadingAvatar(long peerId, int progress, int offset, StructMessageAttachment avatar) {
        for (Item item : getAdapterItems()) {
            if (item.mInfo.chatId == peerId) {
                int pos = getAdapterItems().indexOf(item);
                item.mInfo.avatar = avatar;
                item.mInfo.downloadAttachment.progress = progress;
                item.mInfo.downloadAttachment.offset = offset;
                item.onRequestDownloadAvatar(offset, progress);
                notifyItemChanged(pos);
                break;
            }
        }
    }

    public void updateChat(long chatId, Item item) {
        List<Item> items = getAdapterItems();
        for (Item chat : items) {
            if (chat.mInfo.chatId == chatId) {
                int pos = items.indexOf(chat);
                remove(pos);
                add(0, item);
                break;
            }
        }
    }

    public void updateChatStatus(long chatId, String status) {
        List<Item> items = getAdapterItems();
        for (Item chat : items) {
            if (chat.mInfo.chatId == chatId) {
                int pos = items.indexOf(chat);
                chat.mInfo.lastMessageStatus = status;
                notifyAdapterItemChanged(pos);
                break;
            }
        }
    }
}
