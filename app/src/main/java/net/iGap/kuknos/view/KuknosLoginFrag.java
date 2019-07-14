package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosLoginBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosLoginVM;
import net.iGap.libs.bottomNavigation.Util.Utils;


public class KuknosLoginFrag extends BaseFragment {

    private FragmentKuknosLoginBinding binding;
    private KuknosLoginVM kuknosLoginVM;
    private HelperToolbar mHelperToolbar;


    public static KuknosLoginFrag newInstance() {
        KuknosLoginFrag kuknosLoginFrag = new KuknosLoginFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosLoginVM = ViewModelProviders.of(this).get(KuknosLoginVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_login, container, false);
        binding.setViewmodel(kuknosLoginVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosIdToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        kuknosLoginVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    //TODO clear Log
                    Log.d("amini", "" + errorM.getTitle());
                    binding.fragKuknosIdUserID.setError("" + getString(errorM.getResID()));
                    binding.fragKuknosIdUserID.requestFocus();
                }
            }
        });

    }
}
