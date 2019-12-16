package net.iGap.helper.upload;

public interface OnUploadListener {
    void onProgress(String id, int progress);

    void onFinish(String id, String token);

    void onError(String id);
}
