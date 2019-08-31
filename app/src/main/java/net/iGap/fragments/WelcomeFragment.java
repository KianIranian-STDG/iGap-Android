package net.iGap.fragments;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.activities.ActivityRegistration;
import net.iGap.databinding.FragmentWelcomeBinding;
import net.iGap.viewmodel.WelcomeFragmentViewModel;

public class WelcomeFragment extends BaseFragment {

    private FragmentWelcomeBinding binding;
    private WelcomeFragmentViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                long userId=0;
                if (getArguments() != null){
                    userId = getArguments().getLong("userId");
                }
                return (T) new WelcomeFragmentViewModel(userId);
            }
        }).get(WelcomeFragmentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getGoToRegistrationNicknamePage().observe(getViewLifecycleOwner(),userId->{
            if (getActivity() instanceof ActivityRegistration && userId != null){
                FragmentRegistrationNickname fragment = new FragmentRegistrationNickname();
                Bundle bundle = new Bundle();
                bundle.putLong(FragmentRegistrationNickname.ARG_USER_ID, userId);
                fragment.setArguments(bundle);
                getActivity().onBackPressed();
                ((ActivityRegistration) getActivity()).loadFragment(fragment, true);
            }
        });
    }
}
