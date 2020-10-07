package net.iGap.module.dialog;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.AdapterCamera;
import net.iGap.adapter.items.AdapterPopupOpenGallery;
import net.iGap.adapter.items.BottomSheetItem;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.FragmentGallery;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.observers.interfaces.OnClickCamera;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnPathAdapterBottomSheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.selector.ResolutionSelectorsKt;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.R.string.item;
import static net.iGap.fragments.FragmentChat.listPathString;

public class ChatAttachmentPopup {

    private final long POPUP_ANIMATION_DURATION = 170;
    private final int MAX_COUNT_OF_IMAGE = 100;
    private final String TAG = "ChatAttachmentPopup";

    public boolean isShowing = false;
    private Context mContext;
    private View mRootView;
    private ChatPopupListener mPopupListener;
    private PopupWindow mPopup;
    private SharedPreferences mSharedPref;
    private FragmentActivity mFrgActivity;
    private Fragment mFragment;
    private AttachFile attachFile;
    private RecyclerView rcvBottomSheet;
    private FastItemAdapter fastItemAdapter;
    private boolean isNewBottomSheet;
    private OnClickCamera onClickCamera;
    private OnPathAdapterBottomSheet onPathAdapterBottomSheet;
    private View btnSend;
    private TextView icoSend;
    private TextView lblSend;
    private boolean isPermissionCamera;
    private View viewRoot;
    private boolean isCameraAttached;
    private Fotoapparat fotoapparatSwitcher;
    private boolean isCameraStart;
    private Animator animation;
    private View contentView;
    private View privacyView;
    private int mChatBoxHeight;
    private int mMessagesLayoutHeight;

    private SharedPreferences emojiSharedPreferences;


    private ChatAttachmentPopup() {
    }

    public static ChatAttachmentPopup create() {
        return new ChatAttachmentPopup();
    }

    public ChatAttachmentPopup setContext(Context context) {
        this.mContext = context;
        return this;
    }

    public ChatAttachmentPopup setRootView(View view) {
        this.mRootView = view;
        return this;
    }

    public ChatAttachmentPopup setListener(ChatPopupListener listener) {
        this.mPopupListener = listener;
        return this;
    }

    public ChatAttachmentPopup setSharedPref(SharedPreferences pref) {
        this.mSharedPref = pref;
        return this;
    }

    public ChatAttachmentPopup setFragmentActivity(FragmentActivity fa) {
        this.mFrgActivity = fa;
        return this;
    }

    public ChatAttachmentPopup setChatBoxHeight(int measuredHeight) {
        this.mChatBoxHeight = measuredHeight;
        return this;
    }

    public ChatAttachmentPopup setMessagesLayoutHeight(int measuredHeight) {
        this.mMessagesLayoutHeight = measuredHeight;
        return this;
    }

    public ChatAttachmentPopup setFragment(Fragment frg) {
        this.mFragment = frg;
        return this;
    }

    public ChatAttachmentPopup build() {

        if (mContext == null)
            throw new IllegalArgumentException(TAG + " : CONTEXT can not be null!");

        if (mRootView == null)
            throw new IllegalArgumentException(TAG + " : set root view!");


        //inflate layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        viewRoot = inflater.inflate(R.layout.attachment_popup_view, null, false);

        attachFile = new AttachFile(mFrgActivity);
        initViews(viewRoot);

        //setup popup
        mPopup = new PopupWindow(mContext);
        mPopup.setContentView(viewRoot);
        mPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopup.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopup.setBackgroundDrawable(new BitmapDrawable());
        mPopup.setFocusable(true);
        mPopup.setOutsideTouchable(true);

        privacyView = viewRoot.findViewById(R.id.fl_attachment_privacyView);

        mPopup.setOnDismissListener(() -> {
            isNewBottomSheet = true;
            isShowing = false;
            disableCamera();
        });

        return this;
    }

    public void setIsNewDialog(boolean isNew) {
        this.isNewBottomSheet = isNew;
    }

    public void show() {

        isShowing = true;
        setupContentView();
        setupAdapterRecyclerImagesAndShowPopup();
    }

    private void setupContentView() {
        contentView = viewRoot.findViewById(R.id.content);
        contentView.setVisibility(View.INVISIBLE);

        contentView.setOnClickListener(v -> {
            //nothing
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            contentView.setBackgroundColor(new Theme().getRootColor(contentView.getContext()));
            return;
        } else {
            contentView.setElevation(0);
        }


        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) contentView.getLayoutParams();
        lp.bottomMargin = 0;
        lp.leftMargin = 0;
        lp.rightMargin = 0;

        //get height of keyboard if it was gone set wrap content to popup
        int height = getKeyboardHeight();
        if (height == 0 || (mFragment instanceof FragmentChat && !((FragmentChat) mFragment).isKeyboardViewOpen())) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
            contentView.setBackground(new Theme().tintDrawable(ContextCompat.getDrawable(contentView.getContext(), R.drawable.popup_background), contentView.getContext(), R.attr.rootBackgroundColor));
            contentView.setElevation(4);

            if ((contentView.getMeasuredHeight() + mChatBoxHeight) >= getDeviceScreenHeight()) {
                lp.height = getDeviceScreenHeight() - mChatBoxHeight - 16;

            } else {
                lp.height = height;
            }

            lp.leftMargin = 10;
            lp.rightMargin = 10;
            lp.bottomMargin = mChatBoxHeight + 10;

        } else {
            contentView.setBackgroundColor(new Theme().getRootColor(contentView.getContext()));

            if (contentView.getHeight() >= height) {
                contentView.setMinimumHeight(height);
            } else {
                lp.height = height;
            }

            lp.bottomMargin = 0;
            lp.leftMargin = 0;
            lp.rightMargin = 0;
        }
        contentView.setLayoutParams(lp);


    }

    public void updateHeight() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) contentView.getLayoutParams();

        //get height of keyboard if it was gone set wrap content to popup
        int height = getKeyboardHeight();
        if (height == 0) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;

            if ((contentView.getMeasuredHeight() + mChatBoxHeight) >= getDeviceScreenHeight()) {
                lp.height = getDeviceScreenHeight() - mChatBoxHeight - 16;
            } else {
                lp.height = height;
            }

        } else {
            if (contentView.getHeight() >= height) {
                contentView.setMinimumHeight(height);
            } else {
                lp.height = height;
            }

        }

        G.handler.postDelayed(() -> {
            contentView.setLayoutParams(lp);
        }, 60);

    }

    private int getDeviceScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mFrgActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void initViews(View view) {

        View camera, photo, video, music, file, contact, location;
        View root;

        camera = view.findViewById(R.id.camera);
        photo = view.findViewById(R.id.picture);
        video = view.findViewById(R.id.video);
        music = view.findViewById(R.id.music);
        file = view.findViewById(R.id.file);
        location = view.findViewById(R.id.location);
        contact = view.findViewById(R.id.contact);
        btnSend = view.findViewById(R.id.close);
        icoSend = view.findViewById(R.id.txtSend);
        lblSend = view.findViewById(R.id.txtNumberItem);
        root = view.findViewById(R.id.root);


        root.setOnClickListener(v -> {
            dismiss();
        });

        btnSend.setOnClickListener(v -> {

            if (animation != null) animation.cancel();

            if (FragmentEditImage.textImageList.size() > 0) {
                dismiss();
                clearRecyclerAdapter();
                lblSend.setText(mFrgActivity.getString(R.string.close_icon));
                lblSend.setText(mFrgActivity.getString(R.string.navigation_drawer_close));

                mPopupListener.onAttachPopupSendSelected();

            } else {
                dismiss();
            }

        });

        camera.setOnClickListener(v -> {
            dismiss();

            if (mSharedPref.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {
                attachFile.showDialogOpenCamera(v, null, mFragment);
            } else {
                attachFile.showDialogOpenCamera(v, null, mFragment);
            }
        });

        photo.setOnClickListener(v -> {
            dismiss();
            openPhotoGallery();
        });

        video.setOnClickListener(v -> {
            dismiss();
            openVideoGallery();
        });

        music.setOnClickListener(v -> {
            dismiss();
            openMusicGallery();
        });

        file.setOnClickListener(v -> {
            dismiss();
            try {
                attachFile.requestPickFile((selectedPathList, caption) -> {
                    mPopupListener.onAttachPopupFilePicked(selectedPathList, caption);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        contact.setOnClickListener(v -> {
            dismiss();
            try {
                attachFile.requestPickContact(mFragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        location.setOnClickListener(v -> {
            dismiss();
            try {
                attachFile.requestGetPosition((result, messageOne, MessageTow) -> {
                    mPopupListener.onAttachPopupLocation(messageOne);
                }, mFragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //init local pictures
        fastItemAdapter = new FastItemAdapter();


        onClickCamera = () -> {
            try {
                dismiss();
                new AttachFile(mFrgActivity).requestTakePicture(mFragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        onPathAdapterBottomSheet = (path, isCheck, isEdit, mList, id) -> {

            if (isEdit) {
                dismiss();
                new HelperFragment(mFrgActivity.getSupportFragmentManager(), FragmentEditImage.newInstance(null, true, false, id)).setReplace(false).load();
            } else {
                if (isCheck) {
                    StructBottomSheet item = new StructBottomSheet();
                    item.setPath(path);
                    item.setText("");
                    item.setId(id);
                    FragmentEditImage.textImageList.put(path, item);
                } else {
                    FragmentEditImage.textImageList.remove(path);
                }
                if (FragmentEditImage.textImageList.size() > 0) {
                    icoSend.setText(mFrgActivity.getString(R.string.md_send_button));
                    lblSend.setText("" + FragmentEditImage.textImageList.size() + " " + mFrgActivity.getString(item));
                } else {
                    icoSend.setText(mFrgActivity.getString(R.string.close_icon));
                    lblSend.setText(mFrgActivity.getString(R.string.navigation_drawer_close));
                }
            }
        };


        rcvBottomSheet = view.findViewById(R.id.rcvContent);
        rcvBottomSheet.setLayoutManager(new GridLayoutManager(mFrgActivity, 1, GridLayoutManager.HORIZONTAL, false));
        rcvBottomSheet.setAdapter(fastItemAdapter);

        //disable and enable camera when user scroll on recycler view
        rcvBottomSheet.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(final View view) {
                if (isPermissionCamera) {

                    if (rcvBottomSheet.getChildAdapterPosition(view) == 0) {
                        isCameraAttached = true;
                    }
                    if (isCameraAttached) {
                        enableCamera();
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(final View view) {

                if (isPermissionCamera) {
                    if (rcvBottomSheet.getChildAdapterPosition(view) == 0) {
                        isCameraAttached = false;
                    }
                    if (!isCameraAttached) {
                        disableCamera();
                    }
                }
            }
        });
    }

    private void openPhotoGallery() {
        try {

            HelperPermission.getStoragePermision(mContext, new OnGetPermission() {
                @Override
                public void Allow() {
                    //clear at first time to load image gallery
                    FragmentEditImage.itemGalleryList.clear();
                    FragmentEditImage.textImageList.clear();

                    Fragment fragment = FragmentGallery.newInstance(FragmentGallery.GalleryMode.PHOTO, () -> {
                        try {
                            attachFile.requestOpenGalleryForImageMultipleSelect(mFragment);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    new HelperFragment(mFrgActivity.getSupportFragmentManager(), fragment).setReplace(false).load();
                }

                @Override
                public void deny() {
                    showDeniedPermissionMessage();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDeniedPermissionMessage() {
        if (mContext != null)
            HelperError.showSnackMessage(mContext.getString(R.string.you_need_to_allow) + " " + mContext.getString(R.string.permission_storage), false);
    }

    private void openVideoGallery() {
        try {

            HelperPermission.getStoragePermision(mContext, new OnGetPermission() {
                @Override
                public void Allow() {
                    Fragment fragment = FragmentGallery.newInstance(FragmentGallery.GalleryMode.VIDEO, new FragmentGallery.GalleryFragmentListener() {
                        @Override
                        public void openOsGallery() {
                            try {
                                attachFile.requestOpenGalleryForVideoMultipleSelect(mFragment);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onVideoPickerResult(List<String> videos) {
                            mPopupListener.onAttachPopupVideoPickerResult(videos);
                        }
                    });
                    new HelperFragment(mFrgActivity.getSupportFragmentManager(), fragment).setReplace(false).load();
                }

                @Override
                public void deny() {
                    showDeniedPermissionMessage();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openMusicGallery() {
        try {
            HelperPermission.getStoragePermision(mContext, new OnGetPermission() {
                @Override
                public void Allow() {
                    Fragment fragment = FragmentGallery.newInstance(FragmentGallery.GalleryMode.MUSIC, new FragmentGallery.GalleryFragmentListener() {
                        @Override
                        public void openOsGallery() {
                            try {
                                attachFile.requestPickAudio(mFragment);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onMusicPickerResult(String music) {
                            mPopupListener.onAttachPopupMusicPickerResult(music);
                        }
                    });
                    new HelperFragment(mFrgActivity.getSupportFragmentManager(), fragment).setReplace(false).load();
                }

                @Override
                public void deny() {
                    showDeniedPermissionMessage();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enableCamera() {

        if (isCameraStart || !isPermissionCamera) return;
        if (fotoapparatSwitcher == null) buildCameraSwitcher();
        if (fotoapparatSwitcher != null) setCameraState(true);

    }

    public void disableCamera() {

        if (!isCameraStart || !isPermissionCamera) return;
        if (fotoapparatSwitcher == null) buildCameraSwitcher();
        if (fotoapparatSwitcher != null) setCameraState(false);

    }

    public void dismiss() {
        if (mPopup == null) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            hideViewWithCircularReveal(contentView);
        } else {
            isShowing = false;
            mPopup.dismiss();
        }
    }

    public void directDismiss() {
        if (mPopup != null) mPopup.dismiss();
    }

    public void notifyRecyclerView() {
        if (fastItemAdapter == null) return;
        fastItemAdapter.notifyAdapterDataSetChanged();
    }

    public void clearRecyclerAdapter() {
        if (fastItemAdapter == null) return;
        fastItemAdapter.clear();
    }

    public void addItemToRecycler(IItem item) {
        if (fastItemAdapter == null || item == null) return;
        fastItemAdapter.add(item);
    }

    private void setupAdapterRecyclerImagesAndShowPopup() {

        clearRecyclerAdapter();

        if (isNewBottomSheet || FragmentEditImage.itemGalleryList.size() <= 1) {

            if (listPathString != null) {
                listPathString.clear();
            } else {
                listPathString = new ArrayList<>();
            }

            FragmentEditImage.itemGalleryList.clear();
            if (isNewBottomSheet) {
                FragmentEditImage.textImageList.clear();
            }

            try {
                HelperPermission.getStoragePermision(mFrgActivity, new OnGetPermission() {
                    @Override
                    public void Allow() {
                        getAllShownImagesPath(mFrgActivity, new ChatAttachmentPopup.OnImagesGalleryPrepared() {
                            @Override
                            public void imagesList(ArrayList<StructBottomSheet> listOfAllImages) {
                                FragmentEditImage.itemGalleryList = listOfAllImages;
                                if (rcvBottomSheet != null)
                                    rcvBottomSheet.setVisibility(View.VISIBLE);
                                G.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkCameraAndLoadImage();
                                    }
                                });

                            }
                        });
                    }

                    @Override
                    public void deny() {
                        loadImageGallery();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            checkCameraAndLoadImage();
        }


    }

    private void checkCameraAndLoadImage() {
        boolean isCameraButtonSheet = mSharedPref.getBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
        if (isCameraButtonSheet) {
            try {
                HelperPermission.getCameraPermission(mFrgActivity, new OnGetPermission() {
                    @Override
                    public void Allow() {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {

                                addItemToRecycler(new AdapterCamera("", onClickCamera).withIdentifier(99));
                                loadItemsToRecycler();
                                isPermissionCamera = true;
                            }
                        });
                        showPopup();
                    }

                    @Override
                    public void deny() {

                        loadImageGallery();

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadImageGallery();
        }
    }

    private void loadItemsToRecycler() {
        for (int i = 0; i < FragmentEditImage.itemGalleryList.size(); i++) {
            addItemToRecycler(new BottomSheetItem(FragmentEditImage.itemGalleryList.get(i), onPathAdapterBottomSheet).withIdentifier(100 + i));
        }
        if (FragmentEditImage.itemGalleryList.size() >= MAX_COUNT_OF_IMAGE) {
            addItemToRecycler(new AdapterPopupOpenGallery(() -> {
                dismiss();
                openPhotoGallery();
            }).withIdentifier(0));
        }
    }

    private void showPopup() {

        if (FragmentEditImage.textImageList != null && FragmentEditImage.textImageList.size() > 0) {
            if (icoSend != null)
                icoSend.setText(mFrgActivity.getResources().getString(R.string.md_send_button));
            if (lblSend != null)
                lblSend.setText("" + FragmentEditImage.textImageList.size() + " " + mFrgActivity.getResources().getString(item));
        } else {
            if (icoSend != null)
                icoSend.setText(mFrgActivity.getResources().getString(R.string.close_icon));
            if (lblSend != null)
                lblSend.setText(mFrgActivity.getResources().getString(R.string.navigation_drawer_close));
        }

        enableCamera();


        if (HelperPermission.grantedUseStorage()) {
            rcvBottomSheet.setVisibility(View.VISIBLE);
        } else {
            rcvBottomSheet.setVisibility(View.GONE);
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            mPopup.setAnimationStyle(R.style.chatAttachmentAnimation);
        }

        mPopup.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);

        //animate views after popup showed -> delay set base on xml animation
        G.handler.postDelayed(this::animateViews, 10);
    }

    private void loadImageGallery() {

        loadItemsToRecycler();
        showPopup();

    }

    public interface OnImagesGalleryPrepared {
        void imagesList(ArrayList<StructBottomSheet> listOfAllImages);
    }

    /**
     * get images for show in bottom sheet
     */
    private void getAllShownImagesPath(Activity activity, OnImagesGalleryPrepared onImagesGalleryPrepared) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<StructBottomSheet> listOfAllImages = new ArrayList<>();
                Uri uri;
                Cursor cursor;
                int column_index_data;
                String absolutePathOfImage;
                uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                String[] projection = {
                        MediaStore.MediaColumns.DATA
                };

                cursor = activity.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC");

                if (cursor != null) {
                    column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

                    while (cursor.moveToNext()) {
                        absolutePathOfImage = cursor.getString(column_index_data);

                        StructBottomSheet item = new StructBottomSheet();
                        item.setId(listOfAllImages.size());
                        item.setPath(absolutePathOfImage);
                        item.isSelected = true;
                        listOfAllImages.add(item);
                        if (listOfAllImages.size() >= MAX_COUNT_OF_IMAGE) break;
                    }
                    cursor.close();
                    onImagesGalleryPrepared.imagesList(listOfAllImages);

                }
            }
        }).start();
    }

    private void animateViews() {
        try {
            animateViewWithCircularReveal(contentView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getKeyboardHeight() {

        int currentHeight = LayoutCreator.dp(300);

        if (contentView != null) {
            emojiSharedPreferences = mContext.getSharedPreferences(SHP_SETTING.EMOJI, MODE_PRIVATE);
            int keyboardHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, LayoutCreator.dp(300));
            int keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, LayoutCreator.dp(300));

            currentHeight = AndroidUtils.displaySize.x > AndroidUtils.displaySize.y ? keyboardHeightLand : keyboardHeight;

        }

        return currentHeight;
    }

    private void animateViewWithCircularReveal(View myView) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && myView != null) {

            int cx = myView.getMeasuredWidth() / 2;
            int cy = myView.getMeasuredHeight() / 2;

            // get the final radius for the clipping circle
            int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

            // create the animator for this view (the start radius is zero)
            animation = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

            animation.setDuration(POPUP_ANIMATION_DURATION);

            // make the view visible and start the animation
            myView.setVisibility(View.VISIBLE);
            animation.start();

        } else {
            myView.setVisibility(View.VISIBLE);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void hideViewWithCircularReveal(View view) {

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;

        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        animation = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius, 0);
        animation.setDuration(POPUP_ANIMATION_DURATION);
        animation.start();

        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isShowing = false;
                mPopup.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isShowing = false;
                mPopup.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void buildCameraSwitcher() {

        try {
            fotoapparatSwitcher = Fotoapparat.with(mContext)
                    .into(rcvBottomSheet.findViewById(R.id.cameraView))// view which will draw the camera preview
                    .photoResolution(ResolutionSelectorsKt.highestResolution())   // we want to have the biggest photo possible
//                .lensPosition(back())     // we want back camera
                    .cameraErrorCallback(e -> {
                        fotoapparatSwitcher = null;
                        HelperLog.getInstance().setErrorLog(e);
                    })
                    .build();
        } catch (Exception e) {
            fotoapparatSwitcher = null;
            HelperLog.getInstance().setErrorLog(e);
        }

    }

    private void setCameraState(boolean state) {
        isCameraStart = state;
        try {
            G.handler.postDelayed(() -> {
                if (fotoapparatSwitcher != null) {
                    if (state)
                        fotoapparatSwitcher.start();
                    else
                        fotoapparatSwitcher.stop();
                }
            }, 50);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void setMediaPermission(boolean canSendMedia) {
        if (privacyView != null) {
            privacyView.setVisibility(canSendMedia ? View.GONE : View.VISIBLE);
        }
    }

    public interface ChatPopupListener {

        void onAttachPopupVideoPickerResult(List<String> results);

        void onAttachPopupMusicPickerResult(String result);

        void onAttachPopupShowed();

        void onAttachPopupDismiss();

        void onAttachPopupLocation(String message);

        void onAttachPopupFilePicked(List<String> selectedPathList, String caption);

        void onAttachPopupSendSelected();
    }

    public enum ChatPopupAction {
        PHOTO, VIDEO, CAMERA, MUSIC, FILE, CONTACT, LOCATION, CLOSE
    }
}
