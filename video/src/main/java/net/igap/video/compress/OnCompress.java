package net.igap.video.compress;

public interface OnCompress {
    void onCompressProgress(String id, int percent);

    void onCompressFinish(String id, boolean compress, boolean isCancel);
}
