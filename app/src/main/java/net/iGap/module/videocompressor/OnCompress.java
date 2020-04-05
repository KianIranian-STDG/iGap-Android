package net.iGap.module.videocompressor;

public interface OnCompress {
    void onCompressProgress(String id, int percent);

    void onCompressFinish(String id, boolean compress);
}
