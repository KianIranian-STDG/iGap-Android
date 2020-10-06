package net.iGap.module.upload;

import net.iGap.helper.upload.ApiBased.HttpUploader;
import net.iGap.helper.upload.UploadManager;
import net.iGap.module.accountManager.AppConfig;

public class Uploader implements IUpload {
    private static Uploader instance;

    private IUpload uploadThroughProto;
    private IUpload uploadThroughApi;

    private Uploader() {
        uploadThroughProto = new UploadAdapter(UploadManager.getInstance());
        uploadThroughApi = HttpUploader.getInstance();
    }

    public static Uploader getInstance() {
        if (instance == null) {
            synchronized (Uploader.class) {
                if (instance == null) {
                    instance = new Uploader();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean isUploading(String identity) {
        return getCurrentUploader().isUploading(identity);
    }

    @Override
    public boolean isCompressing(String identity) {
        return getCurrentUploader().isCompressing(identity);
    }

    @Override
    public boolean isCompressingOrUploading(String identity) {
        return getCurrentUploader().isCompressingOrUploading(identity);
    }

    @Override
    public boolean cancelUploading(String identity) {
        return getCurrentUploader().cancelUploading(identity);
    }

    @Override
    public boolean cancelCompressing(String identity) {
        return getCurrentUploader().cancelCompressing(identity);
    }

    @Override
    public boolean cancelCompressingAndUploading(String identity) {
        return getCurrentUploader().cancelCompressingAndUploading(identity);
    }

    @Override
    public int getUploadProgress(String identity) {
        return getCurrentUploader().getUploadProgress(identity);
    }

    @Override
    public int getCompressProgress(String identity) {
        return getCurrentUploader().getCompressProgress(identity);
    }

    @Override
    public void upload(UploadObject fileObject) {
        getCurrentUploader().upload(fileObject);
    }

    private IUpload getCurrentUploader() {
        if (AppConfig.fileGateway == 0) {
            return uploadThroughApi;
        } else if (AppConfig.fileGateway == 1) {
            return uploadThroughProto;
        }

        return uploadThroughProto;
    }
}
