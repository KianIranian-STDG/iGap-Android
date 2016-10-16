package com.iGap.module;

import com.iGap.proto.ProtoGlobal;

import java.io.Serializable;

/**
 * Created by android3 on 9/4/2016.
 */
public class StructSharedMedia implements Serializable {

    public boolean isSelected = false;
    public boolean isDownloading = false;

    public ProtoGlobal.RoomMessageType messageType = ProtoGlobal.RoomMessageType.IMAGE;
    public String thumbnail = "";
    public String filePath = "";
    public String fileUrl = "";
    public String fileName = "";
    public String fileInfo = "";
    public long id;
    public StructMessageAttachment attachment;
    public long time;
    public StructDownloadAttachment downloadAttachment;

    public boolean isFileExists() {
        return attachment != null && attachment.isFileExistsOnLocal();
    }

    public boolean isThumbnailExists() {
        return attachment != null && attachment.isThumbnailExistsOnLocal();
    }

}

