package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentCustomrtclubProfileBinding;
import net.iGap.viewmodel.CustomerClubViewModel;

public class FragmentCustomerClubProfile extends BaseFragment {

    private FragmentCustomrtclubProfileBinding binding;
    private CustomerClubViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customrtclub_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews();
    }

    private void setUpViews() {
        viewModel = new CustomerClubViewModel(this);
        binding.setViewModel(viewModel);
    }

    @Override
    public void onStart() {
        super.onStart();


    }
}
