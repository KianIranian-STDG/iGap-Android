package com.iGap.adapter;

import android.util.Log;

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

    public void downloadingAvatar(long peerId, int progress, int offset,
                                  StructMessageAttachment avatar) {
        for (Item item : getAdapterItems()) {
            if (item.mInfo.chatId == peerId) {
                int pos = getAdapterItems().indexOf(item);
                item.mInfo.avatar = avatar;
                if (item.mInfo.downloadAttachment != null) {
                    item.mInfo.downloadAttachment.progress = progress;
                    item.mInfo.downloadAttachment.offset = offset;
                    item.onRequestDownloadAvatar(offset, progress);
                }
                notifyItemChanged(pos);
                break;
            }
        }
    }

    public void downloadingAvatarThumbnail(String token) {
        for (Item item : getAdapterItems()) {
            if (item.mInfo.avatar.token != null && item.mInfo.avatar.token.equalsIgnoreCase(token)) {
                item.onRequestDownloadAvatarThumbnail(token, true);
                notifyAdapterDataSetChanged();
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

    public void notifyRoomItem(long chatId) {
        notifyAdapterItemChanged(getItemPosition(chatId));
    }

    public void goToTop(long chatId) {
        Item item = null;
        List<Item> items = getAdapterItems();
        for (Item chat : items) {
            if (chat.mInfo.chatId == chatId) {
                item = chat;
                break;
            }
        }
        updateChat(chatId, item);
    }

    public void notifyDraft(long chatId, String draftMessage) {
        List<Item> items = getAdapterItems();
        for (Item chat : items) {
            if (chat.mInfo.chatId == chatId) {

                int position = items.indexOf(chat);
                Log.i("BBB", "chat.mInfo.chatTitle : " + chat.mInfo.chatTitle);
                Log.i("BBB", "position : " + position);
                chat.mInfo.draftMessage = draftMessage;

                //notifyAdapterItemChanged(position);
                notifyItemChanged(position);
            }
        }
    }

    private int getItemPosition(long chatId) {
        List<Item> items = getAdapterItems();
        for (Item chat : items) {
            if (chat.mInfo.chatId == chatId) {
                return items.indexOf(chat);
            }
        }
        return -1;
    }
}