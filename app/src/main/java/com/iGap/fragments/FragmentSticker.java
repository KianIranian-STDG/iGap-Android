package com.iGap.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSticker extends Fragment {

    private ViewGroup layoutCrop;
    private SharedPreferences sharedPreferences;
    private ToggleButton toggleCrop;

    public FragmentSticker() {
        // Required empty public constructor
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sticker, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup root = (ViewGroup) view.findViewById(R.id.stsp_rootLayout);
        root.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

            }
        });

        RippleView rippleBack = (RippleView) view.findViewById(R.id.stsp_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .remove(FragmentSticker.this)
                    .commit();
            }
        });
        sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        layoutCrop = (ViewGroup) view.findViewById(R.id.stsp_layout_crop);
        int checkeCrop = sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 0);
        toggleCrop = (ToggleButton) view.findViewById(R.id.stsp_toggle_crop);
        if (checkeCrop == 1) {
            toggleCrop.setChecked(true);
        } else {
            toggleCrop.setChecked(false);
        }
        layoutCrop.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                sharedPreferences =
                    getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (toggleCrop.isChecked()) {
                    toggleCrop.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_CROP, 0);
                    editor.apply();
                } else {
                    toggleCrop.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_CROP, 1);
                    editor.apply();
                }
            }
        });

        toggleCrop.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                layoutCrop.performClick();
                sharedPreferences =
                    getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (toggleCrop.isChecked()) {
                    toggleCrop.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_CROP, 0);
                    editor.apply();
                } else {
                    toggleCrop.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_CROP, 1);
                    editor.apply();
                }
            }
        });
    }
}
