package com.iGap.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iGap.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCreateChannel extends Fragment {


    public FragmentCreateChannel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_channel, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup vgRoot = (ViewGroup) view.findViewById(R.id.fch_root);
        vgRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        TextView txtBack = (TextView) view.findViewById(R.id.fch_txt_back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(FragmentCreateChannel.this)
                        .commit();
            }
        });

        TextView txtCancel = (TextView) view.findViewById(R.id.fch_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(FragmentCreateChannel.this)
                        .commit();
            }
        });
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.fch_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i("UUUUUUU", "onCheckedChanged: " + checkedId);
            }
        });


        RadioButton raPublic = (RadioButton) view.findViewById(R.id.fch_radioButton_Public);
        raPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        RadioButton raPrivate = (RadioButton) view.findViewById(R.id.fch_radioButton_private);

        raPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        EditText edtLink = (EditText) view.findViewById(R.id.fch_edt_link);




    }
}
