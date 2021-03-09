package net.iGap.camera;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.FragmentUserProfile;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperString;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.Theme;
import net.iGap.module.dialog.ChatAttachmentPopup;
import net.iGap.module.imageLoaderService.GlideImageLoader;
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.observers.interfaces.OnGetPermission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ir.radsense.raadcore.widget.RoundedCornersTransform;


public class CameraStoryFragment extends BaseFragment {
    public static final int request_code_TAKE_PICTURE = 10;
    private Camera mCamera;
    private LinearLayout rootView;
    private ImageView flashModeButton;
    private CameraView cameraPreview;
    private static String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private int REQUEST_CODE_PERMISSIONS = 10;
    private boolean openCamera = true;
    private FrameLayout topToolPanel;
    private FrameLayout preview;
    private FrameLayout cameraContainer;
    private FrameLayout bottomToolPanel;
    private CameraView cameraView;
    private ImageView switchCamera;
    private ImageButton takePicture;
    private CircleImageView galleryIcon;
    private ImageButton closeButton;
    private ImageButton settingButton;
    private OnGalleryIconClicked onGalleryIconClicked;
    private TextView bottomPanelTitle;

    public static CameraStoryFragment newInstance(OnGalleryIconClicked onGalleryIconClicked) {

        Bundle args = new Bundle();

        CameraStoryFragment fragment = new CameraStoryFragment();
        fragment.onGalleryIconClicked = onGalleryIconClicked;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View createView(Context context) {
        fragmentView = new LinearLayout(context);
        rootView = (LinearLayout) fragmentView;
        rootView.setBackgroundColor(Theme.getInstance().getRootColor(context));
        rootView.setOrientation(LinearLayout.VERTICAL);


        cameraContainer = new FrameLayout(context);
        topToolPanel = new FrameLayout(context) {
            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int cx;
                int cy;
                int cx2;
                int cy2;
                int cx3;
                int cy3;

                if (getMeasuredWidth() == AndroidUtils.dp(126)) {
                    cx = getMeasuredWidth() / 2;
                    cy = getMeasuredHeight() / 2;
                    cx3 = cx2 = getMeasuredWidth() / 2;
                    cy2 = cy + cy / 2 + AndroidUtils.dp(17);
                    cy3 = cy / 2 - AndroidUtils.dp(17);
                } else {
                    cx = getMeasuredWidth() / 2;
                    cy = getMeasuredHeight() / 2 - AndroidUtils.dp(13);
                    cx2 = cx + cx / 2 + AndroidUtils.dp(35);
                    cx3 = cx / 2 - AndroidUtils.dp(40);
                    cy3 = cy2 = getMeasuredHeight() / 2 - AndroidUtils.dp(13);
                }

                settingButton.layout(cx3 - settingButton.getMeasuredWidth() / 2, cy3 - settingButton.getMeasuredHeight() / 2, cx3 + settingButton.getMeasuredWidth() / 2, cy3 + settingButton.getMeasuredHeight() / 2);
                closeButton.layout(cx2 - closeButton.getMeasuredWidth() / 2, cy2 - closeButton.getMeasuredHeight() / 2, cx2 + closeButton.getMeasuredWidth() / 2, cy2 + closeButton.getMeasuredHeight() / 2);
                flashModeButton.layout(cx - flashModeButton.getMeasuredWidth() / 2, cy - flashModeButton.getMeasuredHeight() / 2, cx + flashModeButton.getMeasuredWidth() / 2, cy + flashModeButton.getMeasuredHeight() / 2);

            }
        };
        cameraView = new CameraView(getContext(), true);
        LinearLayout.LayoutParams cameraContainerLayoutParams = new LinearLayout.LayoutParams(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER);
        cameraContainerLayoutParams.weight = 1;
        cameraContainer.addView(cameraView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
        rootView.addView(cameraContainer, cameraContainerLayoutParams);


        closeButton = new ImageButton(context);
        closeButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_close));
        topToolPanel.addView(closeButton, LayoutCreator.createFrame(28, 28, Gravity.RIGHT | Gravity.CENTER_VERTICAL));

        settingButton = new ImageButton(context);
        settingButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_setting));
        topToolPanel.addView(settingButton, LayoutCreator.createFrame(28, 28, Gravity.LEFT | Gravity.CENTER_VERTICAL));


        cameraContainer.addView(topToolPanel, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 126, Gravity.LEFT | Gravity.TOP));

        takePicture = new ImageButton(context);
        takePicture.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_shutter));
        takePicture.setScaleType(ImageView.ScaleType.FIT_CENTER);
        cameraContainer.addView(takePicture, LayoutCreator.createFrame(70, 70, Gravity.BOTTOM | Gravity.CENTER, 0, 0, 0, 15));


        bottomToolPanel = new FrameLayout(context) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int top = galleryIcon != null ? getHeight() / 2 - galleryIcon.getMeasuredHeight() / 2 : getHeight() / 2;
                int left = getPaddingLeft();
                int right = galleryIcon != null ? left + galleryIcon.getMeasuredWidth() : left;
                int botttom = galleryIcon != null ? top + galleryIcon.getMeasuredHeight() : top;


                if (galleryIcon != null && galleryIcon.getVisibility() != GONE) {

                    galleryIcon.layout(15, 0, galleryIcon.getMeasuredWidth(), getMeasuredHeight());
                }

                final int iconRightPlusMargin = right;

                if (bottomPanelTitle != null && bottomPanelTitle.getVisibility() != GONE) {
                    top = getHeight() / 2 - bottomPanelTitle.getMeasuredHeight() / 2;
                    left = getWidth() / 2 - bottomPanelTitle.getMeasuredWidth() / 2;
                    right = left + bottomPanelTitle.getMeasuredWidth();
                    botttom = top + bottomPanelTitle.getMeasuredHeight();
                    bottomPanelTitle.layout(left, top, right, botttom);
                }

                if (switchCamera != null && switchCamera.getVisibility() != GONE) {
                    top = switchCamera.getPaddingTop();
                    left = (r - l) - switchCamera.getMeasuredWidth();
                    right = left + switchCamera.getMeasuredWidth();
                    botttom = top + getMeasuredHeight();
                    switchCamera.layout(left, top, right, botttom);
                }

            }
        };
        bottomToolPanel.setBackgroundColor(Color.BLACK);
        switchCamera = new ImageView(context);
        switchCamera.setScaleType(ImageView.ScaleType.FIT_CENTER);
        switchCamera.setImageResource(cameraView.isFrontface() ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
//        switchCamera.setPadding(4, 4, 4, 4);
        bottomToolPanel.addView(switchCamera, LayoutCreator.createFrame(48, 48));

        galleryIcon = new CircleImageView(context);
//        galleryIcon.setPadding(15, 15, 15, 15);
        bottomToolPanel.addView(galleryIcon, LayoutCreator.createFrame(48, 48));

        bottomPanelTitle = new TextView(context);
        bottomPanelTitle.setText("Story");
        bottomPanelTitle.setTypeface(Typeface.DEFAULT_BOLD);
        bottomPanelTitle.setTextColor(Color.WHITE);
        bottomToolPanel.addView(bottomPanelTitle, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 0, 0, 0));

        rootView.addView(bottomToolPanel, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 80));

        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    private void showCamera() {
        AttachFile.getAllShownImagesPath(getActivity(), 1, new ChatAttachmentPopup.OnImagesGalleryPrepared() {
            @Override
            public void imagesList(ArrayList<StructBottomSheet> listOfAllImages) {
                G.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getContext()).asDrawable().load(new File(listOfAllImages.get(0).getPath())).centerCrop().into(galleryIcon);
                        //ImageLoadingServiceInjector.inject().loadImage(galleryIcon, listOfAllImages.get(0).getPath(), true);
                    }
                });
            }
        });
        if (cameraView == null) {
            cameraView = new CameraView(getActivity(), true);
        }
        cameraView.initCamera();
        cameraView.setFocusable(true);
        cameraView.setDelegate(new CameraView.CameraViewDelegate() {
            @Override
            public void onCameraCreated(Camera camera) {
                Log.e("dfdfsdfgds", "onCameraCreated: ");
            }

            @Override
            public void onCameraInit() {
                checkFlashMode();

            }
        });
    }

    public interface OnGalleryIconClicked {
        void onGalleryIconClicked();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        galleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGalleryIconClicked.onGalleryIconClicked();
            }
        });
        checkFlashMode();

        flashModeButton = new ImageView(context);
        flashModeButton.setScaleType(ImageView.ScaleType.CENTER);
        topToolPanel.addView(flashModeButton, LayoutCreator.createFrame(48, 48, Gravity.CENTER));
        flashModeButton.setOnClickListener(currentImage -> {
            if (cameraView == null || !cameraView.isInitied()) {
                return;
            }
            String current = cameraView.getCameraSession().getCurrentFlashMode();
            String next = cameraView.getCameraSession().getNextFlashMode();
            if (current.equals(next)) {
                return;
            }
            cameraView.getCameraSession().setCurrentFlashMode(next);

            setCameraFlashModeIcon(flashModeButton, next);
        });
        flashModeButton.setContentDescription("flash mode ");


        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = "story_" + HelperString.getRandomFileName(3) + ".jpg";
//
//                File dir = new File(G.DIR_IMAGES);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
                File cameraFile = null;
                try {
                    cameraFile = new AttachFile(getActivity()).createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File finalCameraFile = cameraFile;
                CameraController.getInstance().takePicture(cameraFile, cameraView.getCameraSession(), () -> {
                    if (finalCameraFile == null || getActivity() == null) {
                        return;
                    }
                    int orientation = 0;
                    try {
                        ExifInterface ei = new ExifInterface(finalCameraFile.getAbsolutePath());
                        int exif = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        switch (exif) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                orientation = 90;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                orientation = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                orientation = 270;
                                break;
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (FragmentEditImage.itemGalleryList == null) {
                        FragmentEditImage.itemGalleryList = new ArrayList<>();
                    }
                    //CameraController.getInstance().initCamera(null);
                    cameraView.initCamera();
                    FragmentEditImage.itemGalleryList.clear();
                    FragmentEditImage.textImageList.clear();
                    ImageHelper.correctRotateImage(finalCameraFile.getAbsolutePath(), true);
                    FragmentEditImage.insertItemList(finalCameraFile.getAbsolutePath(), true);
                    if (getActivity() != null) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, true, false, 0)).setReplace(false).load();
                    }
                });


            }
        });

        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.switchCamera();
                ObjectAnimator animator = ObjectAnimator.ofFloat(switchCamera, View.SCALE_X, 0.0f).setDuration(100);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        switchCamera.setImageResource(cameraView != null && cameraView.isFrontface() ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
                        ObjectAnimator.ofFloat(switchCamera, View.SCALE_X, 1.0f).setDuration(100).start();
                    }
                });
                animator.start();
            }
        });
        if (checkCameraHardware(getContext())) {
            if (allPermissionsGranted()) {
                CameraController.getInstance().initCamera(null);
                // switchCamera.setVisibility(cameraView.hasFrontFaceCamera() ? View.VISIBLE : View.INVISIBLE);
                showCamera();
            } else {
                try {
                    HelperPermission.getCameraPermission(getContext(), new OnGetPermission() {

                        @Override
                        public void Allow() throws IOException {
                            HelperPermission.getStoragePermision(getContext(), new OnGetPermission() {
                                @Override
                                public void Allow() throws IOException {
                                    if (checkCameraHardware(getContext())) {
                                        CameraController.getInstance().initCamera(null);
//                                        switchCamera.setVisibility(cameraView.hasFrontFaceCamera() ? View.VISIBLE : View.INVISIBLE);
                                        showCamera();
                                    }
                                }

                                @Override
                                public void deny() {

                                }
                            });
                        }

                        @Override
                        public void deny() {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
            Toast.makeText(getContext(), "دستگاه شما دارای دوربین نمی باشد!", Toast.LENGTH_LONG).show();
        }

    }

    private void checkFlashMode() {

        if (cameraView == null || !cameraView.isInitied()) {
            return;
        }
        String current = cameraView.getCameraSession().getCurrentFlashMode();


        cameraView.getCameraSession().setCurrentFlashMode(current);

        flashModeButton.setVisibility(View.VISIBLE);
        setCameraFlashModeIcon(flashModeButton, current);

    }

    private void setCameraFlashModeIcon(ImageView imageView, String mode) {
        switch (mode) {
            case Camera.Parameters.FLASH_MODE_OFF:
                imageView.setImageResource(R.drawable.flash_off);
                break;
            case Camera.Parameters.FLASH_MODE_ON:
                imageView.setImageResource(R.drawable.flash_on);
                break;
        }
    }

    public static class PhotoEntry {
        public int bucketId;
        public int imageId;
        public long dateTaken;
        public int duration;
        public int width;
        public int height;
        public long size;
        public String path;
        public int orientation;
        public boolean isVideo;
        public boolean isMuted;
        public boolean canDeleteAfter;

        public PhotoEntry(int bucketId, int imageId, long dateTaken, String path, int orientation, boolean isVideo, int width, int height, long size) {
            this.bucketId = bucketId;
            this.imageId = imageId;
            this.dateTaken = dateTaken;
            this.path = path;
            this.width = width;
            this.height = height;
            this.size = size;
            if (isVideo) {
                this.duration = orientation;
            } else {
                this.orientation = orientation;
            }
            this.isVideo = isVideo;
        }


    }

    private void initCamera() {
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
//        cameraPreview = new CameraPreview(getContext(), mCamera);

        preview.addView(cameraPreview);

        setCameraDisplayOrientation(getActivity(), Camera.CameraInfo.CAMERA_FACING_FRONT, mCamera);
        mCamera.setFaceDetectionListener(new MyFaceDetectionListener());
    }

    //    private void startCamera() {
//        mCamera = getCameraInstance();
//        mCamera.startPreview();
//        cameraPreview.setCamera(mCamera);
//        setCameraDisplayOrientation(getActivity(), Camera.CameraInfo.CAMERA_FACING_FRONT, mCamera);
//        mCamera.setFaceDetectionListener(new MyFaceDetectionListener());
//    }


//    @Override
//    public void onPause() {
//        if (mCamera != null) {
////            mCamera.stopPreview();
//            //  mPreview.setCamera(null);
//            preview.removeView(cameraPreview);
//            mCamera.release();
//            mCamera = null;
//            openCamera = false;
//
//        }
//
//        super.onPause();
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        int numCams = Camera.getNumberOfCameras();
//        if (!openCamera) {
//            initCamera();
//        }
//    }

    class MyFaceDetectionListener implements Camera.FaceDetectionListener {

        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (faces.length > 0) {
                Log.d("FaceDetection", "face detected: " + faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        "Y: " + faces[0].rect.centerY());
            }
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
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

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            // attempt to get a Camera instance
            openCamera = true;
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e("dfkdjfkdjf", "getCameraInstance: ");
        }
        return c; // returns null if camera is unavailable
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private boolean allPermissionsGranted() {
        if (ContextCompat.checkSelfPermission(getContext(), REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), REQUIRED_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), REQUIRED_PERMISSIONS[2]) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

}
