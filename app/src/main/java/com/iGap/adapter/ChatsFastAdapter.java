package com.iGap.adapter;

import com.iGap.adapter.items.ChatItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public class ChatsFastAdapter<Item extends ChatItem> extends FastItemAdapter<Item> {
    public void updateChat(long chatId, Item item) {
        List<Item> items = getAdapterItems();
        for (Item chat : items) {
            if (chat.mInfo.chatId == chatId) {
                int pos = items.indexOf(chat);
                set(pos, item);
                break;
            }
        }
    }
}
