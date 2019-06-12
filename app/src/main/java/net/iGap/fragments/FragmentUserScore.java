package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import net.iGap.R;
import net.iGap.databinding.FragmentUserScoreBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.UserScoreViewModel;

import org.jetbrains.annotations.NotNull;

public class FragmentUserScore extends BaseFragment {

    private UserScoreViewModel viewModel;
    private FragmentUserScoreBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_score, container, false);
        viewModel = new UserScoreViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar t = HelperToolbar.create().setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.history_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.score))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        // to go history
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentIVandActivities.newInstance()).setReplace(false).load();
                        }
                    }
                });

        binding.toolbar.addView(t.getView());

        binding.scoreView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once
                binding.scoreView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.root);
                set.constrainCircle(binding.scorePointer.getId(), binding.scoreView.getId(), binding.scoreView.getWidth() / 2, 0);
                set.constrainCircle(binding.rankPointer.getId(), binding.rankView.getId(), binding.rankView.getWidth() / 2, 180);
                set.applyTo(binding.root);
            }
        });

        viewModel.userScorePointer.observe(this, integer -> {
            if (integer != null) {
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.root);
                set.constrainCircle(binding.scorePointer.getId(), binding.scoreView.getId(), binding.scoreView.getWidth() / 2, integer);
                set.applyTo(binding.root);
                TransitionManager.beginDelayedTransition(binding.root);
            }
        });
    }
}
