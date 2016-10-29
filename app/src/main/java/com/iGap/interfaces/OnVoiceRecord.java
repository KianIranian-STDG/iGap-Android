package com.iGap.interfaces;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/3/2016.
 */
public interface OnVoiceRecord {
    void onVoiceRecordDone(String savedPath);

    void onVoiceRecordCancel();
}
