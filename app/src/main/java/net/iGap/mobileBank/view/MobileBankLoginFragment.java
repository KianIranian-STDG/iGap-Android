package net.iGap.mobileBank.view;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.MobileBankLoginFragmentBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.mobileBank.viewmoedel.MobileBankLoginViewModel;

public class MobileBankLoginFragment extends BaseAPIViewFrag<MobileBankLoginViewModel> {

    private MobileBankLoginFragmentBinding binding ;

    public static MobileBankLoginFragment newInstance() {
        return new MobileBankLoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater ,R.layout.mobile_bank_login_fragment, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankLoginViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupListeners();
    }

    private void setupListeners() {

        binding.btnLogin.setOnClickListener(v -> {
            if (getActivity() == null) return;
            new HelperFragment(getActivity().getSupportFragmentManager() , new MobileBankHomeFragment()).setReplace(false).load();
        });

    }

    private void setupToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setRoundBackground(false)
                .setLeftIcon(R.string.back_icon)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        binding.toolbar.addView(toolbar.getView());
    }
}
