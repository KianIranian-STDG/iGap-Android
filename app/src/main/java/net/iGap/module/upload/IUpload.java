package net.iGap.module.upload;

public interface IUpload {
    boolean isUploading(String messageId);

    boolean isCompressing(String identity);

    boolean isCompressingOrUploading(String identity);

    boolean cancelUploading(String identity);

    boolean cancelCompressing(String identity);

    boolean cancelCompressingAndUploading(String identity);

    int getUploadProgress(String identity);

    int getCompressProgress(String identity);

    void upload(UploadObject fileObject);
}
