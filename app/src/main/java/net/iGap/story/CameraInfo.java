package net.iGap.story;

import android.hardware.Camera;


import java.util.ArrayList;

public class CameraInfo {
    protected int cameraId;
    protected Camera camera;
    protected ArrayList<Size> pictureSizes = new ArrayList<>();
    protected ArrayList<Size> previewSizes = new ArrayList<>();
    protected final int frontCamera;

    public CameraInfo(int id, int frontFace) {
        cameraId = id;
        frontCamera = frontFace;
    }

    public int getCameraId() {
        return cameraId;
    }

    private Camera getCamera() {
        return camera;
    }

    public ArrayList<Size> getPreviewSizes() {
        return previewSizes;
    }

    public ArrayList<Size> getPictureSizes() {
        return pictureSizes;
    }

    public boolean isFrontface() {
        return frontCamera != 0;
    }
}
