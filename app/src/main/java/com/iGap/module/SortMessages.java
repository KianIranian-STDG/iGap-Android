package com.iGap.module;

import android.os.Build;

import com.iGap.realm.RealmRoomMessage;

import java.util.Comparator;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/24/2016.
 */
public enum SortMessages implements Comparator<RealmRoomMessage> {
    ASC {
        @Override
        public int compare(RealmRoomMessage o1, RealmRoomMessage o2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Long.compare(o1.getMessageId(), o2.getMessageId());
            } else {
                return Long.valueOf(o1.getMessageId()).compareTo(Long.valueOf(o2.getMessageId()));
            }
        }
    },
    DESC {
        @Override
        public int compare(RealmRoomMessage o1, RealmRoomMessage o2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Long.compare(o2.getMessageId(), o1.getMessageId());
            } else {
                return Long.valueOf(o2.getMessageId()).compareTo(Long.valueOf(o1.getMessageId()));
            }
        }
    }
}
