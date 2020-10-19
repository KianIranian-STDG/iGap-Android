package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosEditInfoBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.kuknos.viewmodel.KuknosEditInfoVM;


public class KuknosEditInfoFrag extends BaseFragment {
    private static final String TAG = "KuknosEditInfoFrag";
    private Button submit;
    private KuknosEditInfoVM viewModel;
    private FragmentKuknosEditInfoBinding binding;

    public static KuknosEditInfoFrag newInstance() {
        KuknosEditInfoFrag fragment = new KuknosEditInfoFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(KuknosEditInfoVM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_kuknos_edit_info,container,false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        View view = inflater.inflate(R.layout.fragment_kuknos_edit_info, container, false);
        submit = view.findViewById(R.id.fragKuknosEditInfoSubmit);
        /*submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new HelperFragment(getActivity().getSupportFragmentManager(), KuknosBuyAgainFrag.newInstance()).setAddToBackStack(true).setReplace(true).load();
            }
        });*/
        saveAccountInformation();

        return binding.getRoot();
    }

    private void saveAccountInformation(){
        viewModel.getSaveEdit().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.e(TAG, "onChanged: " + aBoolean );
                }
            }
        });
    }
}