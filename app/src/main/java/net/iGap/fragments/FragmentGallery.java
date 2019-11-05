package net.iGap.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterGalleryPhoto;
import net.iGap.helper.FileManager;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.GalleryItemListener;
import net.iGap.interfaces.OnRotateImage;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.model.GalleryItemModel;

import java.util.List;

public class FragmentGallery extends BaseFragment {

    private AdapterGalleryPhoto mGalleryPhotoAdapter;
    private String mFolderName, mFolderId;
    private boolean isSubFolder = false;
    private HelperToolbar mHelperToolbar;
    private GalleryFragmentListener mGalleryListener;
    private GalleryMode mGalleryMode;

    public FragmentGallery() {
    }

    public static FragmentGallery newInstance(GalleryMode mode, String folder, String id) {
        FragmentGallery fragment = new FragmentGallery();
        fragment.mFolderName = folder;
        fragment.mFolderId = id;
        fragment.mGalleryMode = mode;
        fragment.isSubFolder = true;
        return fragment;
    }

    public static FragmentGallery newInstance(GalleryMode mode, GalleryFragmentListener listener) {
        FragmentGallery fragment = new FragmentGallery();
        fragment.mGalleryListener = listener;
        fragment.mGalleryMode = mode;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
        initRecyclerView(view);
    }

    private void initToolbar(View view) {
        ViewGroup lytToolbar = view.findViewById(R.id.toolbar);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(isSubFolder ? R.string.edit_icon : R.string.more_icon)
                .setLogoShown(true)
                .setIGapLogoCheck(false)
                .setDefaultTitle(isSubFolder ? mFolderName : getString(R.string.gallery))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        galleryOnBackPressed();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (isSubFolder && mGalleryMode == GalleryMode.PHOTO) {
                            checkPhotoMultiSelectAndSendToEdit();
                        } else {
                            openAndroidOsGallery();
                        }
                    }
                });

        lytToolbar.addView(mHelperToolbar.getView());
    }

    private void galleryOnBackPressed() {
        if (mGalleryMode == GalleryMode.PHOTO && mGalleryPhotoAdapter != null && mGalleryPhotoAdapter.getMultiSelectState()) {
            mHelperToolbar.getRightButton().setText(R.string.edit_icon);
            mGalleryPhotoAdapter.setMultiSelectState(!mGalleryPhotoAdapter.getMultiSelectState());
            return;
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

    private void initRecyclerView(View view) {

        RecyclerView rvGallery = view.findViewById(R.id.rv_gallery);
        rvGallery.setLayoutManager(new GridLayoutManager(rvGallery.getContext(), isSubFolder ? 3 : 2));
        if (mGalleryMode == GalleryMode.PHOTO) {
            setupGalleryWithPhotoAdapter(view, rvGallery);
        } else if (mGalleryMode == GalleryMode.VIDEO) {

        }
    }

    private void setupGalleryWithPhotoAdapter(View view, RecyclerView rvGallery) {

        mGalleryPhotoAdapter = new AdapterGalleryPhoto(isSubFolder);
        rvGallery.setAdapter(mGalleryPhotoAdapter);
        mGalleryPhotoAdapter.setListener(new GalleryItemListener() {
            @Override
            public void onItemClicked(String path, String id) {
                if (path == null || getActivity() == null) return;
                if (isSubFolder) {
                    //open Image
                    openImageForEdit(path);
                } else {
                    //open sub directory
                    if (id == null) return;
                    Fragment fragment = FragmentGallery.newInstance(GalleryMode.PHOTO, path, id);
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load(false);
                }
            }

            @Override
            public void onMultiSelect(int size) {
                if (size > 0) {
                    mHelperToolbar.getRightButton().setText(R.string.md_send_button);
                } else {
                    mHelperToolbar.getRightButton().setText(R.string.close_icon);
                }
            }
        });

        if (isSubFolder) {
            mGalleryPhotoAdapter.setPhotosItem(FileManager.getFolderPhotosById(getContext() ,mFolderId));
        } else {
            mGalleryPhotoAdapter.setAlbumsItem(FileManager.getDevicePhotoFolders(getContext()));
        }

        if (isSubFolder && mGalleryPhotoAdapter.getPhotosItem().size() < 2) {//disable multi select when photo count was 1 or 0
            mHelperToolbar.getRightButton().setVisibility(View.GONE);
        }

        if (!isSubFolder && (mGalleryPhotoAdapter.getAlbumsItem().size() == 1 || mGalleryPhotoAdapter.getAlbumsItem().size() == 0)) {//check 1 because we add all statically
            rvGallery.setVisibility(View.GONE);
            view.findViewById(R.id.tv_no_item).setVisibility(View.VISIBLE);
        }
    }

    private void openImageForEdit(String path) {
        FragmentEditImage.itemGalleryList.clear();
        FragmentEditImage.textImageList.clear();

        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, true, false, 0);
        fragmentEditImage.setIsReOpenChatAttachment(false);

        //rotate and send image for edit
        ImageHelper.correctRotateImage(path, true, new OnRotateImage() {
            @Override
            public void startProcess() {
                //nothing
            }

            @Override
            public void success(String newPath) {
                FragmentEditImage.insertItemList(newPath, "", false);
                G.handler.post(() -> {
                    if (getActivity() == null) return;
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                });
            }
        });

        fragmentEditImage.setGalleryListener(() -> {
            popBackStackFragment();
            popBackStackFragment();
        });
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
        new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
    }

    public interface GalleryFragmentListener {
        void openOsGallery();
    }

    public enum GalleryMode {
        PHOTO, VIDEO
    }

}
