package com.iGap.module;

import com.iGap.realm.RealmRoomMessage;

import java.util.Comparator;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/24/2016.
 */
public enum SortMessages implements Comparator<RealmRoomMessage> {
    ASC {
        @Override
        public int compare(RealmRoomMessage o1, RealmRoomMessage o2) {
            return Long.compare(o1.getUpdateTime(), o2.getUpdateTime());
        }
    },
    DESC {
        @Override
        public int compare(RealmRoomMessage o1, RealmRoomMessage o2) {
            return Long.compare(o2.getUpdateTime(), o1.getUpdateTime());
        }
    }
}
