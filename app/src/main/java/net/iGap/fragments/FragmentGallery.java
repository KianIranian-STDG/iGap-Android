package net.iGap.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;

import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityTrimVideo;
import net.iGap.adapter.AdapterGalleryMusic;
import net.iGap.adapter.AdapterGalleryPhoto;
import net.iGap.adapter.AdapterGalleryVideo;
import net.iGap.story.camera.PhotoViewer;
import net.iGap.helper.FileManager;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageHelper;
import net.iGap.model.GalleryItemModel;
import net.iGap.model.GalleryVideoModel;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.observers.interfaces.GalleryItemListener;
import net.iGap.observers.interfaces.OnRotateImage;
import net.iGap.observers.interfaces.ToolbarListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FragmentGallery extends BaseFragment {

    private final String MODE_KEY = "MODE";
    private final String FOLDER_KEY = "FOLDER";
    private final String SUB_FOLDER_KEY = "SUB_FOLDER";
    private final String ID_KEY = "ID";
    private final String RETURN_DIRECTLY_KEY = "RETURN_DIRECT";
    private final String LISTENER_KEY = "LISTENER";
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private AdapterGalleryPhoto mGalleryPhotoAdapter;
    private AdapterGalleryVideo mGalleryVideoAdapter;
    private AdapterGalleryMusic mGalleryMusicAdapter;
    private String mFolderName, mFolderId;
    private boolean isSubFolder = false;
    private HelperToolbar mHelperToolbar;
    private GalleryFragmentListener mGalleryListener;
    private OnRVScrolled onRVScrolled;
    private GalleryMode mGalleryMode;
    private boolean isReturnResultDirectly;
    private boolean isMusicSortedByDate = true;
    private static boolean canMultiSelected;
    private View rootView;
    ViewGroup lytToolbar;
    RecyclerView rvGallery;

    public FragmentGallery() {
    }

    public static FragmentGallery newInstance(boolean canMultiSelect, GalleryMode mode, String folder, String id) {
        FragmentGallery fragment = new FragmentGallery();
        Bundle bundle = new Bundle();
        bundle.putString(fragment.FOLDER_KEY, folder);
        bundle.putString(fragment.MODE_KEY, mode.name());
        bundle.putString(fragment.ID_KEY, id);
        bundle.putBoolean(fragment.SUB_FOLDER_KEY, true);
        canMultiSelected = canMultiSelect;
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FragmentGallery newInstance(boolean canMultiSelect, GalleryMode mode, boolean isReturnResultDirectly, String folder, String id, GalleryFragmentListener listener) {
        FragmentGallery fragment = new FragmentGallery();
        Bundle bundle = new Bundle();
        bundle.putString(fragment.FOLDER_KEY, folder);
        bundle.putString(fragment.MODE_KEY, mode.name());
        bundle.putString(fragment.ID_KEY, id);
        bundle.putSerializable(fragment.LISTENER_KEY, listener);
        bundle.putBoolean(fragment.RETURN_DIRECTLY_KEY, isReturnResultDirectly);
        bundle.putBoolean(fragment.SUB_FOLDER_KEY, true);
        canMultiSelected = canMultiSelect;
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FragmentGallery newInstance(boolean canMultiSelect, GalleryMode mode, GalleryFragmentListener listener) {
        FragmentGallery fragment = new FragmentGallery();
        Bundle bundle = new Bundle();
        bundle.putString(fragment.MODE_KEY, mode.name());
        bundle.putSerializable(fragment.LISTENER_KEY, listener);
        canMultiSelected = canMultiSelect;
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FragmentGallery newInstance(boolean canMultiSelect, GalleryMode mode, OnRVScrolled onRVScrolled, GalleryFragmentListener listener) {
        FragmentGallery fragment = new FragmentGallery();
        fragment.onRVScrolled = onRVScrolled;
        Bundle bundle = new Bundle();
        bundle.putString(fragment.MODE_KEY, mode.name());
        bundle.putSerializable(fragment.LISTENER_KEY, listener);
        canMultiSelected = canMultiSelect;
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;
        if (getArguments() != null) {
            mGalleryMode = GalleryMode.valueOf(getArguments().getString(MODE_KEY));
            mFolderName = getArguments().getString(FOLDER_KEY, null);
            isReturnResultDirectly = getArguments().getBoolean(RETURN_DIRECTLY_KEY, false);
            mGalleryListener = (GalleryFragmentListener) getArguments().getSerializable(LISTENER_KEY);
            mFolderId = getArguments().getString(ID_KEY, null);
            isSubFolder = getArguments().getBoolean(SUB_FOLDER_KEY, false);
        }
        if (mGalleryMode == GalleryMode.STORY) {
            ViewGroup lytToolbar = view.findViewById(R.id.toolbar);
            lytToolbar.setVisibility(View.GONE);
        } else {
            initToolbar(view);
        }
        initRecyclerView(view);
    }

    private void initToolbar(View view) {
        lytToolbar = view.findViewById(R.id.toolbar);
        String toolbarTitle = "";
        if (mGalleryMode == GalleryMode.PHOTO || mGalleryMode == GalleryMode.STORY) {
            lytToolbar.setVisibility(View.VISIBLE);
            toolbarTitle = getString(R.string.gallery);
        } else if (mGalleryMode == GalleryMode.VIDEO) {
            toolbarTitle = getString(R.string.videos);
        } else {
            toolbarTitle = getString(R.string.audios);
        }

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setIGapLogoCheck(false)
                .setDefaultTitle(isSubFolder ? mFolderName : toolbarTitle)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        galleryOnBackPressed();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (isSubFolder) {
                            if (mGalleryMode == GalleryMode.PHOTO || mGalleryMode == GalleryMode.STORY) {
                                checkPhotoMultiSelectAndSendToEdit();
                            } else {
                                checkVideoMultiSelectAndSendToEdit();
                            }
                        } else if (mGalleryMode == GalleryMode.MUSIC) {
                            if (mGalleryMusicAdapter.getMusicsItem().size() != 0) {
                                showSortDialog();
                            } else {
                                if (getContext() != null)
                                    Toast.makeText(getContext(), getString(R.string.no_item), Toast.LENGTH_SHORT).show();
                            }
                        } /* else {
                            openAndroidOsGallery();
                        }*/
                    }

                });

        if (!isReturnResultDirectly) {
            if (mGalleryMode == GalleryMode.MUSIC) {
                mHelperToolbar.setRightIcons(R.string.more_icon);
            } else if (isSubFolder) {
                mHelperToolbar.setRightIcons(R.string.edit_icon);
            }
        }


        lytToolbar.addView(mHelperToolbar.getView());
    }

    public interface OnRVScrolled {
        void scrolled(boolean isScrolled);

        void changeItem();
    }

    boolean checked = true;
    private VelocityTracker mVelocityTracker = null;
    private class OnSwipeTouchListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;
        Context context;

        public OnSwipeTouchListener(Context ctx, View mainView) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            mainView.setOnTouchListener(this);
            context = ctx;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        public class GestureListener extends
                GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                float diffY = 0;
                float diffX = 0;
                try {
                    if (e1 != null) {
                        diffY = e2.getY() - e1.getY();
                        diffX = e2.getX() - e1.getX();
                    } else {
                        diffY = e2.getY();
                        diffX = e2.getX();
                    }

                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {

                            } else {

                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            Log.e("dfldsjflsdjf", "onFling: " );
                        } else {
                            rvGallery.getParent().requestDisallowInterceptTouchEvent(true);
                            Log.e("dfldsjflsdjf", "onFling2: " );
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }


    }
    private void initRecyclerView(View view) {

        rvGallery = view.findViewById(R.id.rv_gallery);


    //    OnSwipeTouchListener swwi = new OnSwipeTouchListener(getContext(), rvGallery);


//        rvGallery.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
//                GridLayoutManager layoutManager = GridLayoutManager.class.cast(rv.getLayoutManager());
//                int index = event.getActionIndex();
//                int action = event.getActionMasked();
//                int pointerId = event.getPointerId(index);
//                float diffY = 0;
//                float diffX = 0;
//                boolean result = false;
//                switch (action) {
//                    case MotionEvent.ACTION_DOWN:
//                        if (mVelocityTracker == null) {
//                            // Retrieve a new VelocityTracker object to watch the
//                            // velocity of a motion.
//                            mVelocityTracker = VelocityTracker.obtain();
//                        } else {
//                            // Reset the velocity tracker back to its initial state.
//                            mVelocityTracker.clear();
//                        }
//                        // Add a user's movement to the tracker.
//                        mVelocityTracker.addMovement(event);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        mVelocityTracker.addMovement(event);
//                        // When you want to determine the velocity, call
//                        // computeCurrentVelocity(). Then call getXVelocity()
//                        // and getYVelocity() to retrieve the velocity for each pointer ID.
//                        mVelocityTracker.computeCurrentVelocity(100);
//                        // Log velocity of pixels per second
//                        // Best practice to use VelocityTrackerCompat where possible.
//
//                        diffY = event.getY();
//                        diffX = event.getX();
//
//
//                        if (Math.abs(diffX) > Math.abs(diffY)) {
//                            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(mVelocityTracker.getXVelocity(pointerId)) > SWIPE_VELOCITY_THRESHOLD) {
//                                if (diffX > 0) {
//
//                                } else {
//
//                                }
//                                result = true;
//                            }
//                        } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(mVelocityTracker.getYVelocity(pointerId)) > SWIPE_VELOCITY_THRESHOLD) {
//                            if (diffY > 0) {
//                                //onSwipeBottom();
//
//                                Log.e("fdfsf", "onTouchEvent: " );
//                            } else {
//                                // onSwipeTop();
//                                Log.e("fdfsf", "onToucddhEvent: " );
//                            }
//                            result = true;
//                        }
//
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        // Return a VelocityTracker object back to be re-used by others.
////                        mVelocityTracker.recycle();
//                        break;
//                }
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent event) {
//
////                    case MotionEvent.ACTION_MOVE:
////                    case MotionEvent.ACTION_DOWN:
////                        rv.getParent().requestDisallowInterceptTouchEvent(true);
////                        onRVScrolled.scrolled(false);
////                        break;
////                    case MotionEvent.ACTION_CANCEL:
////                        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
////                            rv.getParent().requestDisallowInterceptTouchEvent(true);
////                            onRVScrolled.scrolled(false);
////                        }
////                        break;
//
//            }
//
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
//        rvGallery.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
//
//                }
//            }
//
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                GridLayoutManager layoutManager = GridLayoutManager.class.cast(recyclerView.getLayoutManager());
//                Log.e("dfasfa", "onScrolled: " + dy);
//                if (dy < 0 && layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
//                    recyclerView.getParent().requestDisallowInterceptTouchEvent(false);
//                    onRVScrolled.scrolled(true);
//                    //   onRVScrolled.changeItem();
//                } else if (dy > 0) {
//                    recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
//                    onRVScrolled.scrolled(false);
//                }
//            }
//        });
        switch (mGalleryMode) {
            case PHOTO:
            case STORY:
                rvGallery.setLayoutManager(new GridLayoutManager(rvGallery.getContext(), isSubFolder ? 3 : 2));
                setupGalleryWithPhotoAdapter(view, rvGallery);
                break;

            case VIDEO:
                rvGallery.setLayoutManager(new GridLayoutManager(rvGallery.getContext(), isSubFolder ? 3 : 2));
                setupGalleryWithVideoAdapter(view, rvGallery);
                break;

            case MUSIC:
                rvGallery.setLayoutManager(new LinearLayoutManager(rvGallery.getContext()));
                setupGalleryWithMusicAdapter(view, rvGallery);
                break;
        }
    }

    private void galleryOnBackPressed() {
        if (mHelperToolbar.getRightButton() != null) {
            if (mGalleryMode == GalleryMode.PHOTO) {
                if (mGalleryPhotoAdapter != null && mGalleryPhotoAdapter.getMultiSelectState()) {
                    mHelperToolbar.getRightButton().setText(R.string.edit_icon);
                    mGalleryPhotoAdapter.setMultiSelectState(!mGalleryPhotoAdapter.getMultiSelectState());
                    return;
                }
            } else if (mGalleryMode == GalleryMode.VIDEO) {
                if (mGalleryVideoAdapter != null && mGalleryVideoAdapter.getMultiSelectState()) {
                    mHelperToolbar.getRightButton().setText(R.string.edit_icon);
                    mGalleryVideoAdapter.setMultiSelectState(!mGalleryVideoAdapter.getMultiSelectState());
                    return;
                }
            }
        }

        popBackStackFragment();
    }

    private void openAndroidOsGallery() {
        if (mGalleryListener != null) {
            popBackStackFragment();
            mGalleryListener.openOsGallery();
        }
    }

    private void checkPhotoMultiSelectAndSendToEdit() {
        if (mGalleryPhotoAdapter == null) return;
        if (mGalleryPhotoAdapter.getMultiSelectState()) {
            mHelperToolbar.getRightButton().setText(R.string.edit_icon);
            if (mGalleryPhotoAdapter.getSelectedPhotos().size() > 0)
                sendSelectedPhotos(mGalleryPhotoAdapter.getSelectedPhotos());
        } else {
            mHelperToolbar.getRightButton().setText(R.string.close_icon);
        }
        mGalleryPhotoAdapter.setMultiSelectState(!mGalleryPhotoAdapter.getMultiSelectState());
    }

    private void checkVideoMultiSelectAndSendToEdit() {
        if (mGalleryVideoAdapter == null) return;
        if (mGalleryVideoAdapter.getMultiSelectState()) {
            mHelperToolbar.getRightButton().setText(R.string.edit_icon);
            if (mGalleryVideoAdapter.getSelectedVideos().size() > 0) {
                List<String> videos = new ArrayList<>();
                for (GalleryVideoModel video : mGalleryVideoAdapter.getSelectedVideos()) {
                    videos.add(video.getPath());
                }
                mGalleryListener.onVideoPickerResult(videos);
                popBackStackFragment();
                if (getActivity() instanceof ActivityMain) {
                    ((ActivityMain) getActivity()).goneDetailFrameInTabletMode();
                }
            }
        } else {
            mHelperToolbar.getRightButton().setText(R.string.close_icon);
        }
        mGalleryVideoAdapter.setMultiSelectState(!mGalleryVideoAdapter.getMultiSelectState());
    }

    private void setupGalleryWithVideoAdapter(View view, RecyclerView rvGallery) {
        mGalleryVideoAdapter = new AdapterGalleryVideo(isSubFolder);
        rvGallery.setAdapter(mGalleryVideoAdapter);
        mGalleryVideoAdapter.setListener(new GalleryItemListener() {
            @Override
            public void onItemClicked(String path, String id) {
                if (path == null) return;
                if (isSubFolder) {
                    //open video
                    openVideoForEdit(path);
                } else {
                    //open sub directory
                    openGallerySubDirectory(GalleryMode.VIDEO, path, id);
                }
            }

            @Override
            public void onMultiSelect(int size) {
                handleUiWithMultiSelect(size);
            }
        });

        if (isSubFolder) {

            FileManager.getFolderVideosById(getContext(), mFolderId, result -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> setVideoGalleryAdapter(result, view, rvGallery));
                }
            });

        } else {

            FileManager.getDeviceVideoFolders(getContext(), result -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> setVideoGalleryAdapter(result, view, rvGallery));
                }
            });

        }
    }

    private void setupGalleryWithPhotoAdapter(View view, RecyclerView rvGallery) {

        mGalleryPhotoAdapter = new AdapterGalleryPhoto(isSubFolder);
        mGalleryPhotoAdapter.setMultiState(canMultiSelected);
        rvGallery.setAdapter(mGalleryPhotoAdapter);
        mGalleryPhotoAdapter.setListener(new GalleryItemListener() {
            @Override
            public void onItemClicked(String path, String id) {
                if (path == null) return;
                if (isSubFolder) {
                    //open Image
                    openImageForEdit(path);
                } else {
                    //open sub directory
                    openGallerySubDirectory(mGalleryMode, path, id);
                }
            }

            @Override
            public void onMultiSelect(int size) {
                initToolbar(rootView);
                handleUiWithMultiSelect(size);
            }
        });


        if (isSubFolder) {
            FileManager.getFolderPhotosById(getContext(), mFolderId, result -> {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    mGalleryPhotoAdapter.setPhotosItem(result);
                    setPhotoGalleryUI(view, rvGallery);
                });
            });
        } else {
            FileManager.getDevicePhotoFolders(getContext(), result -> {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    mGalleryPhotoAdapter.setAlbumsItem(result);
                    setPhotoGalleryUI(view, rvGallery);
                });
            });
        }

    }

    private void setupGalleryWithMusicAdapter(View view, RecyclerView rvGallery) {

        view.findViewById(R.id.loading).setVisibility(View.VISIBLE);

        mGalleryMusicAdapter = new AdapterGalleryMusic();
        rvGallery.setAdapter(mGalleryMusicAdapter);
        mGalleryMusicAdapter.setListener(new GalleryItemListener() {
            @Override
            public void onItemClicked(String path, String id) {
                if (path == null) return;
                if (mGalleryListener != null) mGalleryListener.onMusicPickerResult(path);
                popBackStackFragment();
            }

            @Override
            public void onMultiSelect(int size) {
                //don't support yet

                //handleUiWithMultiSelect(size);
            }
        });

        FileManager.getDeviceMusics(getContext(), isMusicSortedByDate, result -> {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                mGalleryMusicAdapter.setMusicsItem(result);
                setMusicGalleryUI(view, rvGallery);
            });
        });

    }

    private void setVideoGalleryAdapter(List<GalleryVideoModel> result, View view, RecyclerView rvGallery) {
        mGalleryVideoAdapter.setVideosItem(result);

        if (isSubFolder && mGalleryVideoAdapter.getVideosItem().size() < 2) {//disable multi select when photo count was 1 or 0
            mHelperToolbar.getRightButton().setVisibility(View.GONE);
        }

        if (!isSubFolder && (mGalleryVideoAdapter.getVideosItem().size() == 1 || mGalleryVideoAdapter.getVideosItem().size() == 0)) {//check 1 because we add all statically
            showNoItemInGallery(rvGallery, view);
        }

        view.findViewById(R.id.loading).setVisibility(View.GONE);
    }

    private void setPhotoGalleryUI(View view, RecyclerView rvGallery) {
        if (!isReturnResultDirectly && isSubFolder && mGalleryPhotoAdapter.getPhotosItem().size() < 2) {//disable multi select when photo count was 1 or 0
            mHelperToolbar.getRightButton().setVisibility(View.GONE);
        }

        if (!isSubFolder && (mGalleryPhotoAdapter.getAlbumsItem().size() == 1 || mGalleryPhotoAdapter.getAlbumsItem().size() == 0)) {//check 1 because we add all statically
            showNoItemInGallery(rvGallery, view);
        }

        view.findViewById(R.id.loading).setVisibility(View.GONE);
    }

    private void setMusicGalleryUI(View view, RecyclerView rvGallery) {
        if (mGalleryMusicAdapter.getMusicsItem().size() == 0) {
            showNoItemInGallery(rvGallery, view);
        }
        view.findViewById(R.id.loading).setVisibility(View.GONE);
    }

    private void openVideoForEdit(String path) {
        if (getActivity() == null) return;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1) {
            Intent intent = new Intent(getActivity(), ActivityTrimVideo.class);
            intent.putExtra("PATH", path);
            getActivity().startActivityForResult(intent, AttachFile.request_code_trim_video);
            return;
        }
        List<String> videos = new ArrayList<>();
        videos.add(path);
        mGalleryListener.onVideoPickerResult(videos);
        popBackStackFragment();
    }

    private void openImageForEdit(String path) {
        FragmentEditImage.itemGalleryList.clear();
        FragmentEditImage.textImageList.clear();

        if (!isReturnResultDirectly) {

            FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, true, false, 0);
            fragmentEditImage.setIsReOpenChatAttachment(false);
            ImageHelper.correctRotateImage(path, true, new OnRotateImage() {
                @Override
                public void startProcess() {

                }

                @Override
                public void success(String newPath) {
                    G.handler.post(() -> {
                        PhotoViewer photoViewer = PhotoViewer.newInstance(newPath);
                        FragmentEditImage.insertItemList(newPath, "", false);
                        if (getActivity() != null) {
                            if (mGalleryMode == GalleryMode.STORY) {
                                new HelperFragment(getActivity().getSupportFragmentManager(), photoViewer).setReplace(false).load();
                            } else {
                                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                            }

                        }
                    });
                }
            });

            fragmentEditImage.setGalleryListener(() -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack(FragmentGallery.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            });
        } else {
            if (mGalleryListener != null) {
                mGalleryListener.onGalleryResult(path);
            }
        }
    }

    private void sendSelectedPhotos(List<GalleryItemModel> selectedPhotos) {
        if (getActivity() == null || selectedPhotos.size() == 0) return;

        FragmentEditImage.itemGalleryList.clear();
        FragmentEditImage.textImageList.clear();
        for (GalleryItemModel photo : selectedPhotos) {
            FragmentEditImage.insertItemList(photo.getAddress(), "", false);
        }
        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, true, false, selectedPhotos.size() - 1);
        fragmentEditImage.setIsReOpenChatAttachment(false);
        fragmentEditImage.setGalleryListener(() -> {
            popBackStackFragment();
            popBackStackFragment();
        });
        if (mGalleryMode == GalleryMode.STORY) {
            PhotoViewer photoViewer = PhotoViewer.newInstance((ArrayList<GalleryItemModel>) selectedPhotos);
            new HelperFragment(getActivity().getSupportFragmentManager(), photoViewer).setReplace(false).load();
        } else {
            new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
        }

    }

    private void showNoItemInGallery(View rv, View view) {
        rv.setVisibility(View.GONE);
        view.findViewById(R.id.tv_no_item).setVisibility(View.VISIBLE);
    }

    private void openGallerySubDirectory(GalleryMode mode, String path, String id) {
        if (id == null || getActivity() == null) return;
        Fragment fragment = FragmentGallery.newInstance(canMultiSelected, mode, false, path, id, new GalleryFragmentListener() {
            @Override
            public void openOsGallery() {

            }

            @Override
            public void onVideoPickerResult(List<String> videos) {
                if (mGalleryListener != null) mGalleryListener.onVideoPickerResult(videos);
                popBackStackFragment();
            }
        });
        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load(false);

    }

    private void handleUiWithMultiSelect(int size) {
        if (mHelperToolbar.getRightButton() != null) {
            if (mGalleryMode == GalleryMode.PHOTO || mGalleryMode == GalleryMode.VIDEO || mGalleryMode == GalleryMode.STORY) {
                if (size > 0) {
                    mHelperToolbar.getRightButton().setText(R.string.md_send_button);
                } else {
                    mHelperToolbar.getRightButton().setText(R.string.close_icon);
                }
            }
        }
    }

    private void showSortDialog() {
        if (getContext() == null) return;

        List<String> items = new ArrayList<>();
        items.add(getString(R.string.date));
        items.add(getString(R.string.name));

        new TopSheetDialog(getContext()).setListData(items, -1, position -> {
            switch (position) {
                case 0:
                    if (!isMusicSortedByDate) {
                        isMusicSortedByDate = true;
                        mGalleryMusicAdapter.setMusicsItem(new ArrayList<>());
                        initRecyclerView(requireView());
                    }
                    break;

                case 1:
                    if (isMusicSortedByDate) {
                        isMusicSortedByDate = false;
                        mGalleryMusicAdapter.setMusicsItem(new ArrayList<>());
                        initRecyclerView(requireView());
                    }
                    break;
            }
        }).show();
    }

    public interface GalleryFragmentListener extends Serializable {
        void openOsGallery();

        default void onGalleryResult(String path) {
        }

        default void onVideoPickerResult(List<String> videos) {
        }

        default void onMusicPickerResult(String music) {
        }
    }

    public enum GalleryMode {
        PHOTO, VIDEO, MUSIC, STORY
    }

}
