package net.iGap.kuknos.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;


public class KuknosGetSupportFrag extends Fragment {


    public static KuknosGetSupportFrag newInstance() {
        KuknosGetSupportFrag fragment = new KuknosGetSupportFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kuknos_get_support, container, false);
    }
}