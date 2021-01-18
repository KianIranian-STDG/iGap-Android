package net.iGap.structs;

import net.iGap.helper.HelperMimeType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmThumbnail;

import java.io.File;

public class AttachmentObject {
    public String mime;
    public String cacheId;
    public double duration;
    public int height;
    public int width;
    public String name;
    public String publicUrl;
    public long size;
    public String token;
    public AttachmentObject smallThumbnail;
    public AttachmentObject largeThumbnail;
    public String filePath;
    public String thumbnailPath;

    private AttachmentObject() {

    }

    public static AttachmentObject create(ProtoGlobal.File attachment) {
        AttachmentObject attachmentObject = new AttachmentObject();

        attachmentObject.mime = attachment.getMime();
        attachmentObject.cacheId = attachment.getCacheId();
        attachmentObject.duration = attachment.getDuration();
        attachmentObject.height = attachment.getHeight();
        attachmentObject.width = attachment.getWidth();
        attachmentObject.name = attachment.getName();
        attachmentObject.publicUrl = attachment.getPublicUrl();
        attachmentObject.size = attachment.getSize();
        attachmentObject.token = attachment.getToken();
        attachmentObject.smallThumbnail = createThumb(attachment.getSmallThumbnail(), attachment.getToken());
        attachmentObject.largeThumbnail = createThumb(attachment.getLargeThumbnail(), attachment.getToken());

        return attachmentObject;
    }

    private static AttachmentObject createThumb(ProtoGlobal.Thumbnail thumbnail, String token) {
        AttachmentObject attachmentObject = new AttachmentObject();
        attachmentObject.cacheId = thumbnail.getCacheId();
        attachmentObject.token = token;
        attachmentObject.height = thumbnail.getHeight();
        attachmentObject.width = thumbnail.getWidth();
        attachmentObject.mime = thumbnail.getMime();
        attachmentObject.name = thumbnail.getName();
        attachmentObject.size = thumbnail.getSize();
        return attachmentObject;
    }

    public static AttachmentObject create(RealmAttachment attachment) {
        AttachmentObject attachmentObject = new AttachmentObject();

        attachmentObject.mime = attachment.getMimeType();
        attachmentObject.cacheId = attachment.getCacheId();
        attachmentObject.duration = attachment.getDuration();
        attachmentObject.height = attachment.getHeight();
        attachmentObject.width = attachment.getWidth();
        attachmentObject.name = attachment.getName();
        attachmentObject.publicUrl = attachment.getUrl();
        attachmentObject.size = attachment.getSize();
        attachmentObject.token = attachment.getToken();
        attachmentObject.filePath = attachment.getLocalFilePath();
        attachmentObject.thumbnailPath = attachment.getLocalThumbnailPath();
        attachmentObject.smallThumbnail = createThumb(attachment.getSmallThumbnail(), attachment.getToken());
        attachmentObject.largeThumbnail = createThumb(attachment.getLargeThumbnail(), attachment.getToken());

        return attachmentObject;
    }

    private static AttachmentObject createThumb(RealmThumbnail thumbnail, String token) {
        if (thumbnail != null) {
            AttachmentObject attachmentObject = new AttachmentObject();
            attachmentObject.cacheId = thumbnail.getCacheId();
            attachmentObject.token = token;
            attachmentObject.height = thumbnail.getHeight();
            attachmentObject.width = thumbnail.getWidth();
//        attachmentObject.mime = thumbnail.getMime();
//        attachmentObject.name = thumbnail.getName();
            attachmentObject.size = thumbnail.getSize();
            return attachmentObject;
        }
        return null;
    }

    public boolean isFileExistsOnLocal() {
        return filePath != null && new File(filePath).exists() && new File(filePath).canRead();
    }

    public boolean isFileExistsOnLocalAndIsImage() {
        assert filePath != null;
        return isFileExistsOnLocal() && HelperMimeType.isFileImage(filePath.toLowerCase());
    }

    public boolean isAnimatedSticker() {
        return name != null && HelperMimeType.isFileJson(name);
    }

    public boolean isThumbnailExistsOnLocal() {
        return thumbnailPath != null && new File(thumbnailPath).exists() && new File(thumbnailPath).canRead();
    }
}
