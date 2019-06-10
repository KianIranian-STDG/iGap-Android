package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentWelcomeBinding;
import net.iGap.viewmodel.WelcomeFragmentViewModel;

public class WelcomeFragment extends BaseFragment {

    private FragmentWelcomeBinding binding;
    private WelcomeFragmentViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false);
        viewModel = new WelcomeFragmentViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().postDelayed(() -> {
            if (getArguments() != null && getActivity() != null) {
                long userId = getArguments().getLong("userId");
                FragmentRegistrationNickname fragment = new FragmentRegistrationNickname();
                Bundle bundle = new Bundle();
                bundle.putLong(FragmentRegistrationNickname.ARG_USER_ID, userId);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                getActivity().getSupportFragmentManager().beginTransaction().remove(WelcomeFragment.this).commitAllowingStateLoss();
            }
        }, 2000);
    }
}
