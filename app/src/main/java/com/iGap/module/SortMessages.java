package com.iGap.module;

import android.os.Build;
import android.util.Log;

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
                Log.i("LLL", "o1 : " + o1);
                Log.i("LLL", "o2 : " + o2);
                Log.i("LLL", "o2 getUpdateTime : " + o2.getUpdateTime());
                Log.i("LLL", "o1 getUpdateTime : " + o1.getUpdateTime());
                return Long.compare(o1.getUpdateTime(), o2.getUpdateTime());
            } else {
                return Long.valueOf(o1.getUpdateTime()).compareTo(Long.valueOf(o2.getUpdateTime()));
            }
        }
    },
    DESC {
        @Override
        public int compare(RealmRoomMessage o1, RealmRoomMessage o2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.i("LLL", "o1 : " + o1);
                Log.i("LLL", "o2 : " + o2);
                Log.i("LLL", "o2 getUpdateTime : " + o2.getUpdateTime());
                Log.i("LLL", "o1 getUpdateTime : " + o1.getUpdateTime());
                return Long.compare(o2.getUpdateTime(), o1.getUpdateTime());
            } else {
                return Long.valueOf(o2.getUpdateTime()).compareTo(Long.valueOf(o1.getUpdateTime()));
            }
        }
    }
}
