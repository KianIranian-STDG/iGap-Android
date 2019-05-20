package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.databinding.FragmentUserScoreBinding;
import net.iGap.viewmodel.UserScoreViewModel;

import org.jetbrains.annotations.NotNull;

public class FragmentUserScore extends FragmentToolBarBack {

    private UserScoreViewModel viewModel;
    private FragmentUserScoreBinding binding;

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_score, root, true);
        viewModel = new UserScoreViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        titleTextView.setText(R.string.score_title);
        menu_item1.setText(R.string.md_clearHistory);
        menu_item1.setVisibility(View.VISIBLE);
        menu_item1.setOnClickListener(v -> {

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
