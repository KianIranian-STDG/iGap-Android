package net.iGap.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import net.iGap.G;
import net.iGap.module.AndroidUtils;

import java.util.ArrayList;

public class CameraView extends FrameLayout implements TextureView.SurfaceTextureListener {
    private Size previewSize;
    private boolean mirror;
    private TextureView textureView;
    private CameraSession cameraSession;
    private boolean initied;
    private CameraViewDelegate delegate;
    private int clipTop;
    private int clipBottom;
    private boolean isFrontface;
    private Matrix txform = new Matrix();
    private Matrix matrix = new Matrix();
    private int focusAreaSize;

    private boolean useMaxPreview;

    private long lastDrawTime;
    private float focusProgress = 1.0f;
    private float innerAlpha;
    private float outerAlpha;
    private boolean initialFrontface;
    private int cx;
    private int cy;
    private Paint outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean optimizeForBarcode;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();


    public CameraView(Context context, boolean frontface) {
        super(context, null);
        initialFrontface = isFrontface = frontface;
        textureView = new TextureView(context);
        textureView.setSurfaceTextureListener(this);
        addView(textureView);
        focusAreaSize = (int) Math.ceil(context.getResources().getDisplayMetrics().density * 96);
        outerPaint.setColor(0xffffffff);
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setStrokeWidth((int) Math.ceil(context.getResources().getDisplayMetrics().density * 2));
        innerPaint.setColor(0x7fffffff);
    }


    private void checkPreviewMatrix() {
        if (previewSize == null) {
            return;
        }
        WindowManager manager = (WindowManager) G.context.getSystemService(Activity.WINDOW_SERVICE);
        adjustAspectRatio(previewSize.getWidth(), previewSize.getHeight(), manager.getDefaultDisplay().getRotation());
    }
    public boolean isInitied() {
        return initied;
    }
    private void adjustAspectRatio(int previewWidth, int previewHeight, int rotation) {
        txform.reset();

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float viewCenterX = viewWidth / 2;
        float viewCenterY = viewHeight / 2;

        float scale;
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            scale = Math.max((float) (viewHeight + clipTop + clipBottom) / previewWidth, (float) (viewWidth) / previewHeight);
        } else {
            scale = Math.max((float) (viewHeight + clipTop + clipBottom) / previewHeight, (float) (viewWidth) / previewWidth);
        }

        float previewWidthScaled = previewWidth * scale;
        float previewHeightScaled = previewHeight * scale;

        float scaleX = previewHeightScaled / (viewWidth);
        float scaleY = previewWidthScaled / (viewHeight);

        txform.postScale(scaleX, scaleY, viewCenterX, viewCenterY);

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            txform.postRotate(90 * (rotation - 2), viewCenterX, viewCenterY);
        } else {
            if (Surface.ROTATION_180 == rotation) {
                txform.postRotate(180, viewCenterX, viewCenterY);
            }
        }

        if (mirror) {
            txform.postScale(-1, 1, viewCenterX, viewCenterY);
        }
        if (clipTop != 0) {
            txform.postTranslate(0, -clipTop / 2);
        } else if (clipBottom != 0) {
            txform.postTranslate(0, clipBottom / 2);
        }

        textureView.setTransform(txform);

        Matrix matrix = new Matrix();
        if (cameraSession != null) {
            matrix.postRotate(cameraSession.getDisplayOrientation());
        }
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
        matrix.invert(this.matrix);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        checkPreviewMatrix();
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        initCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        checkPreviewMatrix();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        if (cameraSession != null) {
            CameraController.getInstance().close(cameraSession, null, null);
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        if (!initied && cameraSession != null && cameraSession.isInitied()) {
            if (delegate != null) {
                delegate.onCameraInit();
            }
            initied = true;
        }
    }

    public void switchCamera() {
        if (cameraSession != null) {
            CameraController.getInstance().close(cameraSession, null, null);
            cameraSession = null;
        }
        initied = false;
        isFrontface = !isFrontface;
        initCamera();
    }
    public CameraSession getCameraSession() {
        return cameraSession;
    }
    public void initCamera() {
        CameraInfo info = null;
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos == null) {
            return;
        }
        for (int a = 0; a < cameraInfos.size(); a++) {
            CameraInfo cameraInfo = cameraInfos.get(a);
            if (isFrontface && cameraInfo.frontCamera != 0 || !isFrontface && cameraInfo.frontCamera == 0) {
                info = cameraInfo;
                break;
            }
        }
        if (info == null) {
            return;
        }
        float size4to3 = 4.0f / 3.0f;
        float size16to9 = 16.0f / 9.0f;
        float screenSize = (float) Math.max(AndroidUtils.displaySize.x, AndroidUtils.displaySize.y) / Math.min(AndroidUtils.displaySize.x, AndroidUtils.displaySize.y);
        Size aspectRatio;
        int wantedWidth;
        int wantedHeight;
        if (initialFrontface) {
            aspectRatio = new Size(16, 9);
            wantedWidth = 480;
            wantedHeight = 270;
        } else {
            if (Math.abs(screenSize - size4to3) < 0.1f) {
                aspectRatio = new Size(4, 3);
                wantedWidth = 1280;
                wantedHeight = 960;
            } else {
                aspectRatio = new Size(16, 9);
                wantedWidth = 1280;
                wantedHeight = 720;
            }
        }
        if (textureView.getWidth() > 0 && textureView.getHeight() > 0) {
            int width;
            if (useMaxPreview) {
                width = Math.max(AndroidUtils.displaySize.x, AndroidUtils.displaySize.y);
            } else {
                width = Math.min(AndroidUtils.displaySize.x, AndroidUtils.displaySize.y);
            }
            int height = width * aspectRatio.getHeight() / aspectRatio.getWidth();
            previewSize = CameraController.chooseOptimalSize(info.getPreviewSizes(), width, height, aspectRatio);
        }
        Size pictureSize = CameraController.chooseOptimalSize(info.getPictureSizes(), wantedWidth, wantedHeight, aspectRatio);
        if (pictureSize.getWidth() >= 1280 && pictureSize.getHeight() >= 1280) {
            if (Math.abs(screenSize - size4to3) < 0.1f) {
                aspectRatio = new Size(3, 4);
            } else {
                aspectRatio = new Size(9, 16);
            }
            Size pictureSize2 = CameraController.chooseOptimalSize(info.getPictureSizes(), wantedHeight, wantedWidth, aspectRatio);
            if (pictureSize2.getWidth() < 1280 || pictureSize2.getHeight() < 1280) {
                pictureSize = pictureSize2;
            }
        }
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        if (previewSize != null && surfaceTexture != null) {
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            cameraSession = new CameraSession(info, previewSize, pictureSize, ImageFormat.JPEG);

            CameraController.getInstance().open(cameraSession, surfaceTexture, () -> {
                if (cameraSession != null) {
                    cameraSession.setInitied();
                }
                checkPreviewMatrix();
            }, () -> {
                if (delegate != null) {
                    delegate.onCameraCreated(cameraSession.cameraInfo.camera);
                }
            });
        }
    }

    public boolean hasFrontFaceCamera() {
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        for (int a = 0; a < cameraInfos.size(); a++) {
            if (cameraInfos.get(a).frontCamera != 0) {
                return true;
            }
        }
        return false;
    }

    public interface CameraViewDelegate {
        void onCameraCreated(Camera camera);

        void onCameraInit();
    }

    public boolean isFrontface() {
        return isFrontface;
    }

    public TextureView getTextureView() {
        return textureView;
    }

    public void setUseMaxPreview(boolean value) {
        useMaxPreview = value;
    }

    public void setDelegate(CameraViewDelegate cameraViewDelegate) {
        delegate = cameraViewDelegate;
    }
}
