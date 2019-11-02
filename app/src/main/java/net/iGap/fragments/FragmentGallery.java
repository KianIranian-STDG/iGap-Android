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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.AdapterGalleryAlbums;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.model.GalleryAlbumModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentGallery extends BaseFragment {

    private int SPAN_GRID_FOLDER = 2;
    private int SPAN_GRID_IMAGE = 3;
    private AdapterGalleryAlbums mGalleryAdapter;

    public FragmentGallery() {
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
                .setDefaultTitle(getString(R.string.gallery))
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
        rvGallery.setLayoutManager(new GridLayoutManager(rvGallery.getContext(), SPAN_GRID_FOLDER));
        mGalleryAdapter = new AdapterGalleryAlbums();
        rvGallery.setAdapter(mGalleryAdapter);

        mGalleryAdapter.getClickListener().observe(getViewLifecycleOwner(), position -> {
            if (position == null) return;
            //do stuff
        });

        mGalleryAdapter.setItems(getGalleryAlbums());
    }

    private List<GalleryAlbumModel> getGalleryAlbums() {

        List<GalleryAlbumModel> albums = new ArrayList<>();
        if (getContext() == null) return albums;

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

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
                            albums.add(album);
                            ids.add(album.getId());
                        }
                    }//else could be counter\
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return albums;
    }

}
