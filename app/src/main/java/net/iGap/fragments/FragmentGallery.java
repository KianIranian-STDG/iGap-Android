package net.iGap.fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.AdapterGallery;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.model.GalleryAlbumModel;
import net.iGap.model.GalleryPhotoModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentGallery extends BaseFragment {

    private int SPAN_GRID_FOLDER = 2;
    private int SPAN_GRID_SUB_FOLDER = 3;
    private AdapterGallery mGalleryAdapter;
    public String folderName ;
    public boolean isSubFolder = false ;

    public FragmentGallery() {
    }

    public static FragmentGallery newInstance(String folder){
        FragmentGallery fragment = new FragmentGallery();
        fragment.folderName = folder ;
        fragment.isSubFolder = true ;
        return fragment ;
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

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setIGapLogoCheck(false)
                .setDefaultTitle(isSubFolder ? folderName : getString(R.string.gallery))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        lytToolbar.addView(toolbar.getView());
    }

    private void initRecyclerView(View view) {

        RecyclerView rvGallery = view.findViewById(R.id.rv_gallery);
        rvGallery.setLayoutManager(new GridLayoutManager(rvGallery.getContext(), isSubFolder ? SPAN_GRID_SUB_FOLDER : SPAN_GRID_FOLDER));
        mGalleryAdapter = new AdapterGallery(isSubFolder);
        rvGallery.setAdapter(mGalleryAdapter);

        mGalleryAdapter.setListener(new AdapterGallery.GalleryItemListener() {
            @Override
            public void onItemClicked(String name) {
                if (name == null || getActivity() == null) return;
                if (isSubFolder){
                    //open Image

                }else {
                    //open sub directory
                    Fragment fragment = FragmentGallery.newInstance(name);
                    new HelperFragment(getActivity().getSupportFragmentManager() , fragment).setReplace(false).load(false);
                }
            }

            @Override
            public void onItemSelected(GalleryPhotoModel item, boolean isCheck) {
                //do multi select
            }
        });

        if (isSubFolder) {
            mGalleryAdapter.setPhotosItem(getAlbumPhotos(folderName));
        }else {
            mGalleryAdapter.setAlbumsItem(getGalleryAlbums());
        }
    }

    private List<GalleryAlbumModel> getGalleryAlbums() {
        List<GalleryAlbumModel> albums = new ArrayList<>();
        if (getContext() == null) return albums;

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        Cursor cursor = getContext().getContentResolver().query(
                uri,
                projection,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN  + " DESC");

        ArrayList<String> ids = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    GalleryAlbumModel album = new GalleryAlbumModel();
                    album.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)));
                    if (!ids.contains(album.getId())) {
                        album.setCaption(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
                        album.setCover(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));
                        if (!album.getCover().contains(".gif")) {
                            //check and add ALL for first item
                            if (albums.size() == 0){
                                albums.add(new GalleryAlbumModel("-1" , getString(R.string.all) , album.getCover()));
                            }
                            albums.add(album);
                            ids.add(album.getId());
                        }
                    }//else could be counter
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return albums;
    }

    private List<GalleryPhotoModel> getAlbumPhotos(String folderName){
        List<GalleryPhotoModel> photos = new ArrayList<>();
        if (getContext() == null) return photos ;

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.MediaColumns.DATA ,
                MediaStore.Images.Media.DATE_TAKEN
        };

        boolean isAllPhoto = folderName.equals(getString(R.string.all));

        Cursor cursor = getContext().getContentResolver().query(
                uri,
                projection,
                isAllPhoto ? null : MediaStore.Images.Media.DATA + " like ? ",
                isAllPhoto ? null : new String[] {folderName},
                MediaStore.Images.ImageColumns.DATE_TAKEN  + " DESC"
        );

        if (cursor != null){
            while (cursor.moveToNext()){
                try{
                    GalleryPhotoModel photo = new GalleryPhotoModel();
                    photo.setId(photos.size());
                    photo.setAddress(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));
                    if (photo.getAddress() != null && !photo.getAddress().contains(".gif")) {
                        photos.add(photo);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return photos;
    }

}
