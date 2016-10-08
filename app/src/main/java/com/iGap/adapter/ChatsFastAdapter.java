package com.iGap.adapter;

import com.iGap.adapter.items.ChatItem;
import com.iGap.module.StructMessageAttachment;
import com.iGap.realm.enums.RoomType;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public class ChatsFastAdapter<Item extends ChatItem> extends FastItemAdapter<Item> {
    public ChatsFastAdapter() {
        // as we provide id's for the items we want the hasStableIds enabled to speed up things
        setHasStableIds(true);
    }

    public void updateThumbnail(String token) {
        for (Item item : getAdapterItems()) {
            if (item.mInfo.downloadAttachment != null && item.mInfo.downloadAttachment.token.equalsIgnoreCase(token)) {
                int pos = getAdapterItems().indexOf(item);
                notifyItemChanged(pos);
                break;
            }
        }
    }

    public void updateDownloadFields(String token, int progress, int offset) {
        for (Item item : getAdapterItems()) {
            if (item.mInfo.downloadAttachment != null && item.mInfo.downloadAttachment.token.equalsIgnoreCase(token)) {
                int pos = getAdapterItems().indexOf(item);
                item.mInfo.downloadAttachment.offset = offset;
                item.mInfo.downloadAttachment.progress = progress;
                // i think it doesn't needed
                //item.onRequestDownloadFile(offset, progress);

                notifyItemChanged(pos);
                break;
            }
        }
    }

    public void updateChatAvatar(long peerId, StructMessageAttachment avatar) {
        for (Item item : getAdapterItems()) {
            if (item.mInfo.chatType == RoomType.CHAT && item.mInfo.ownerId == peerId) {
                int pos = getAdapterItems().indexOf(item);
                item.mInfo.avatar = avatar;
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
}
