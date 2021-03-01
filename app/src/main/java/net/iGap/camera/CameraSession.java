package net.iGap.camera;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

import net.iGap.G;
import net.iGap.helper.FileLog;

import java.util.ArrayList;
import java.util.List;

public class CameraSession {
    protected CameraInfo cameraInfo;
    private String currentFlashMode;
    private OrientationEventListener orientationEventListener;
    private int lastOrientation = -1;
    private int lastDisplayOrientation = -1;
    private boolean isVideo;
    private final Size pictureSize;
    private final Size previewSize;
    private final int pictureFormat;
    private boolean initied;
    private int maxZoom;
    private boolean meteringAreaSupported;
    private int currentOrientation;
    private int diffOrientation;
    private int jpegOrientation;
    private boolean sameTakePictureOrientation;
    private boolean flipFront = true;
    private float currentZoom;
    private boolean optimizeForBarcode;

    public static final int ORIENTATION_HYSTERESIS = 5;

    public CameraSession(CameraInfo info, Size preview, Size picture, int format) {
        previewSize = preview;
        pictureSize = picture;
        pictureFormat = format;
        cameraInfo = info;

        SharedPreferences sharedPreferences = G.context.getSharedPreferences("camera", Activity.MODE_PRIVATE);
        currentFlashMode = sharedPreferences.getString(cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", Camera.Parameters.FLASH_MODE_OFF);

        orientationEventListener = new OrientationEventListener(G.context) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientationEventListener == null || !initied || orientation == ORIENTATION_UNKNOWN) {
                    return;
                }
                jpegOrientation = roundOrientation(orientation, jpegOrientation);
                WindowManager mgr = (WindowManager) G.context.getSystemService(Context.WINDOW_SERVICE);
                int rotation = mgr.getDefaultDisplay().getRotation();
                if (lastOrientation != jpegOrientation || rotation != lastDisplayOrientation) {
                    if (!isVideo) {
                        configurePhotoCamera();
                    }
                    lastDisplayOrientation = rotation;
                    lastOrientation = jpegOrientation;
                }
            }
        };

        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        } else {
            orientationEventListener.disable();
            orientationEventListener = null;
        }
    }

    public boolean isFlipFront() {
        return flipFront;
    }

    private int roundOrientation(int orientation, int orientationHistory) {
        boolean changeOrientation;
        if (orientationHistory == OrientationEventListener.ORIENTATION_UNKNOWN) {
            changeOrientation = true;
        } else {
            int dist = Math.abs(orientation - orientationHistory);
            dist = Math.min(dist, 360 - dist);
            changeOrientation = (dist >= 45 + ORIENTATION_HYSTERESIS);
        }
        if (changeOrientation) {
            return ((orientation + 45) / 90 * 90) % 360;
        }
        return orientationHistory;
    }

    public void setInitied() {
        initied = true;
    }

    public int getDisplayOrientation() {
        try {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraInfo.getCameraId(), info);
            return getDisplayOrientation(info, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
        return 0;
    }

    public void setCurrentFlashMode(String mode) {
        currentFlashMode = mode;
        configurePhotoCamera();
        SharedPreferences sharedPreferences = G.context.getSharedPreferences("camera", Activity.MODE_PRIVATE);
        sharedPreferences.edit().putString(cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", mode).commit();
    }

    public String getCurrentFlashMode() {
        return currentFlashMode;
    }

    public void checkFlashMode(String mode) {
        ArrayList<String> modes = CameraController.getInstance().availableFlashModes;
        if (modes.contains(currentFlashMode)) {
            return;
        }
        currentFlashMode = mode;
        configurePhotoCamera();
    }

    public String getNextFlashMode() {
        ArrayList<String> modes = CameraController.getInstance().availableFlashModes;
        for (int a = 0; a < modes.size(); a++) {
            String mode = modes.get(a);
            if (mode.equals(currentFlashMode)) {
                if (a < modes.size() - 1) {
                    return modes.get(a + 1);
                } else {
                    return modes.get(0);
                }
            }
        }
        return currentFlashMode;
    }

    public int getDisplayOrientation(Camera.CameraInfo info, boolean isStillCapture) {
        WindowManager mgr = (WindowManager) G.context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = mgr.getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int displayOrientation;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (info.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;

            if (!isStillCapture && displayOrientation == 90) {
                displayOrientation = 270;
            }
            if (!isStillCapture && "Huawei".equals(Build.MANUFACTURER) && "angler".equals(Build.PRODUCT) && displayOrientation == 270) {
                displayOrientation = 90;
            }
        } else {
            displayOrientation = (info.orientation - degrees + 360) % 360;
        }

        return displayOrientation;
    }

    protected void configureRoundCamera() {
        try {
            isVideo = true;
            Camera camera = cameraInfo.camera;
            if (camera != null) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.Parameters params = null;
                try {
                    params = camera.getParameters();
                } catch (Exception e) {
                    FileLog.e(e);
                }

                Camera.getCameraInfo(cameraInfo.getCameraId(), info);

                int displayOrientation = getDisplayOrientation(info, true);
                int cameraDisplayOrientation;

                if ("samsung".equals(Build.MANUFACTURER) && "sf2wifixx".equals(Build.PRODUCT)) {
                    cameraDisplayOrientation = 0;
                } else {
                    int degrees = 0;
                    int temp = displayOrientation;
                    switch (temp) {
                        case Surface.ROTATION_0:
                            degrees = 0;
                            break;
                        case Surface.ROTATION_90:
                            degrees = 90;
                            break;
                        case Surface.ROTATION_180:
                            degrees = 180;
                            break;
                        case Surface.ROTATION_270:
                            degrees = 270;
                            break;
                    }
                    if (info.orientation % 90 != 0) {
                        info.orientation = 0;
                    }
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        temp = (info.orientation + degrees) % 360;
                        temp = (360 - temp) % 360;
                    } else {
                        temp = (info.orientation - degrees + 360) % 360;
                    }
                    cameraDisplayOrientation = temp;
                }
                camera.setDisplayOrientation(currentOrientation = cameraDisplayOrientation);
                diffOrientation = currentOrientation - displayOrientation;

                if (params != null) {

                    params.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());

                    params.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
                    params.setPictureFormat(pictureFormat);
                    params.setRecordingHint(true);

                    String desiredMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
                    if (params.getSupportedFocusModes().contains(desiredMode)) {
                        params.setFocusMode(desiredMode);
                    } else {
                        desiredMode = Camera.Parameters.FOCUS_MODE_AUTO;
                        if (params.getSupportedFocusModes().contains(desiredMode)) {
                            params.setFocusMode(desiredMode);
                        }
                    }

                    int outputOrientation = 0;
                    if (jpegOrientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            outputOrientation = (info.orientation - jpegOrientation + 360) % 360;
                        } else {
                            outputOrientation = (info.orientation + jpegOrientation) % 360;
                        }
                    }
                    try {
                        params.setRotation(outputOrientation);
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            sameTakePictureOrientation = (360 - displayOrientation) % 360 == outputOrientation;
                        } else {
                            sameTakePictureOrientation = displayOrientation == outputOrientation;
                        }
                    } catch (Exception e) {
                        //
                    }
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    try {
                        camera.setParameters(params);
                    } catch (Exception e) {
                        //
                    }

                    if (params.getMaxNumMeteringAreas() > 0) {
                        meteringAreaSupported = true;
                    }
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    protected void configurePhotoCamera() {
        try {
            Camera camera = cameraInfo.camera;
            if (camera != null) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.Parameters params = null;
                try {
                    params = camera.getParameters();
                } catch (Exception e) {
                    Log.e("dfdf", "" + e);
                }

                Camera.getCameraInfo(cameraInfo.getCameraId(), info);

                int displayOrientation = getDisplayOrientation(info, true);
                int cameraDisplayOrientation;

                if ("samsung".equals(Build.MANUFACTURER) && "sf2wifixx".equals(Build.PRODUCT)) {
                    cameraDisplayOrientation = 0;
                } else {
                    int degrees = 0;
                    int temp = displayOrientation;
                    switch (temp) {
                        case Surface.ROTATION_0:
                            degrees = 0;
                            break;
                        case Surface.ROTATION_90:
                            degrees = 90;
                            break;
                        case Surface.ROTATION_180:
                            degrees = 180;
                            break;
                        case Surface.ROTATION_270:
                            degrees = 270;
                            break;
                    }
                    if (info.orientation % 90 != 0) {
                        info.orientation = 0;
                    }
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        temp = (info.orientation + degrees) % 360;
                        temp = (360 - temp) % 360;
                    } else {
                        temp = (info.orientation - degrees + 360) % 360;
                    }
                    cameraDisplayOrientation = temp;
                }
                camera.setDisplayOrientation(currentOrientation = cameraDisplayOrientation);

                if (params != null) {
                    params.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
                    params.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
                    params.setPictureFormat(pictureFormat);
                    params.setJpegQuality(100);
                    params.setJpegThumbnailQuality(100);
                    maxZoom = params.getMaxZoom();
                    params.setZoom((int) (currentZoom * maxZoom));

                    if (optimizeForBarcode) {
                        List<String> modes = params.getSupportedSceneModes();
                        if (modes != null && modes.contains(Camera.Parameters.SCENE_MODE_BARCODE)) {
                            params.setSceneMode(Camera.Parameters.SCENE_MODE_BARCODE);
                        }
                        String desiredMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
                        if (params.getSupportedFocusModes().contains(desiredMode)) {
                            params.setFocusMode(desiredMode);
                        }
                    } else {
                        String desiredMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
                        if (params.getSupportedFocusModes().contains(desiredMode)) {
                            params.setFocusMode(desiredMode);
                        }
                    }

                    int outputOrientation = 0;
                    if (jpegOrientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            outputOrientation = (info.orientation - jpegOrientation + 360) % 360;
                        } else {
                            outputOrientation = (info.orientation + jpegOrientation) % 360;
                        }
                    }
                    try {
                        params.setRotation(outputOrientation);
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            sameTakePictureOrientation = (360 - displayOrientation) % 360 == outputOrientation;
                        } else {
                            sameTakePictureOrientation = displayOrientation == outputOrientation;
                        }
                    } catch (Exception e) {
                        //
                    }
                    params.setFlashMode(currentFlashMode);
                    try {
                        camera.setParameters(params);
                    } catch (Exception e) {
                        //
                    }

                    if (params.getMaxNumMeteringAreas() > 0) {
                        meteringAreaSupported = true;
                    }
                }
            }
        } catch (Throwable e) {
            Log.e("dfdf", "" + e);
        }
    }

    public boolean isSameTakePictureOrientation() {
        return sameTakePictureOrientation;
    }

    public boolean isInitied() {
        return initied;
    }

    public void destroy() {
        initied = false;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            orientationEventListener = null;
        }
    }
}
