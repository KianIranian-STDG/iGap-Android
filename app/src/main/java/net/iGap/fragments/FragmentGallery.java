package net.iGap.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.R;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

public class FragmentGallery extends BaseFragment {


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

    }
}
