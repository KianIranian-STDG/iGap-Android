package com.iGap.interfaces;

import android.support.v7.widget.RecyclerView;
import com.iGap.module.enums.LocalFileType;

public interface IChatItemAttachment<VH extends RecyclerView.ViewHolder> {
    void onLoadThumbnailFromLocal(VH holder, String localPath, LocalFileType fileType);

    void onPlayPauseGIF(VH holder, String localPath);
}
