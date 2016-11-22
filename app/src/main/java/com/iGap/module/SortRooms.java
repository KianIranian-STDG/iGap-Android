package com.iGap.module;

import android.os.Build;

import com.iGap.adapter.items.RoomItem;

import java.util.Comparator;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/24/2016.
 */
public enum SortRooms implements Comparator<RoomItem> {
    ASC {
        @Override
        public int compare(RoomItem o1, RoomItem o2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Long.compare(o1.getInfo().getLastMessage() != null ? o1.getInfo().getLastMessage().getUpdateOrCreateTime() : o1.getInfo().getUpdatedTime(), o2.getInfo().getLastMessage() != null ? o2.getInfo().getLastMessage().getUpdateOrCreateTime() : o2.getInfo().getUpdatedTime());
            } else {
                return Long.valueOf(o1.getInfo().getLastMessage() != null ? o1.getInfo().getLastMessage().getUpdateOrCreateTime() : o1.getInfo().getUpdatedTime()).compareTo(Long.valueOf(o2.getInfo().getLastMessage() != null ? o2.getInfo().getLastMessage().getUpdateOrCreateTime() : o2.getInfo().getUpdatedTime()));
            }
        }
    },
    DESC {
        @Override
        public int compare(RoomItem o1, RoomItem o2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Long.compare(o2.getInfo().getLastMessage() != null ? o2.getInfo().getLastMessage().getUpdateOrCreateTime() : o2.getInfo().getUpdatedTime(), o1.getInfo().getLastMessage() != null ? o1.getInfo().getLastMessage().getUpdateOrCreateTime() : o1.getInfo().getUpdatedTime());
            } else {
                return Long.valueOf(o2.getInfo().getLastMessage() != null ? o2.getInfo().getLastMessage().getUpdateOrCreateTime() : o2.getInfo().getUpdatedTime()).compareTo(Long.valueOf(o1.getInfo().getLastMessage() != null ? o1.getInfo().getLastMessage().getUpdateOrCreateTime() : o1.getInfo().getUpdatedTime()));
            }
        }
    }
}
