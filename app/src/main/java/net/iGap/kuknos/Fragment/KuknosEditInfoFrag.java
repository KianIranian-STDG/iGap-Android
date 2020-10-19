package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;


public class KuknosEditInfoFrag extends BaseFragment {

    private Button submit;

    public static KuknosEditInfoFrag newInstance() {
        KuknosEditInfoFrag fragment = new KuknosEditInfoFrag();
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
        View view = inflater.inflate(R.layout.fragment_kuknos_edit_info, container, false);
        submit = view.findViewById(R.id.fragKuknosEditInfoSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(getActivity().getSupportFragmentManager(), KuknosBuyAgainFrag.newInstance()).setAddToBackStack(true).setReplace(true).load();
            }
        });


        return view;
    }
}