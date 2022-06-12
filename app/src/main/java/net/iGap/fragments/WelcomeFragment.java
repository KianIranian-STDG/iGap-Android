package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.activities.ActivityRegistration;
import net.iGap.databinding.FragmentWelcomeBinding;
import net.iGap.messenger.theme.Theme;
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
                return (T) new WelcomeFragmentViewModel();
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
        ConstraintLayout root = view.findViewById(R.id.root);
        root.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(context, R.drawable.shape_toolbar_background_rect), context, Theme.getColor(Theme.key_theme_color)));
        viewModel.getGoToRegistrationNicknamePage().observe(getViewLifecycleOwner(), isShow -> {
            if (getActivity() instanceof ActivityRegistration && isShow != null && isShow) {
                ((ActivityRegistration) getActivity()).loadFragment(new FragmentRegistrationNickname(), true);
            }
        });
    }
}
