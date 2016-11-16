package com.iGap.interfaces;

import android.support.v7.widget.RecyclerView;

import com.iGap.module.enums.LocalFileType;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/28/2016.
 */
public interface IChatItemAttachment<VH extends RecyclerView.ViewHolder> {
    void onLoadThumbnailFromLocal(VH holder, String localPath, LocalFileType fileType);

    void onRequestDownloadFile(long offset, int progress);

    void onRequestDownloadThumbnail(String token, boolean done);
}
