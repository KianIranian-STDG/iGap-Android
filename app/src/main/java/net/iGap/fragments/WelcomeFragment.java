package net.iGap.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentWelcomeBinding;
import net.iGap.viewmodel.WelcomeFragmentViewModel;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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

        G.handler.postDelayed(() -> {
            if (getArguments() != null) {
                long userId = getArguments().getLong("userId");
                if (getArguments().getBoolean("newUser")) {
                    FragmentRegistrationNickname fragment = new FragmentRegistrationNickname();
                    Bundle bundle = new Bundle();
                    bundle.putLong(FragmentRegistrationNickname.ARG_USER_ID, userId);
                    fragment.setArguments(bundle);
                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(WelcomeFragment.this).commitAllowingStateLoss();
                } else {
                    G.currentActivity.finish();
                    Intent intent = new Intent(getActivity(), ActivityMain.class);
                    intent.putExtra(FragmentRegistrationNickname.ARG_USER_ID, userId);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    G.context.startActivity(intent);
                }
            }
        }, 2000);
    }
}
