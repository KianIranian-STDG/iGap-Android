package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import net.iGap.databinding.FragmentIvandProfileBinding;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.CircleImageView;
import net.iGap.viewmodel.FragmentIVandProfileViewModel;


public class FragmentIVandProfile extends FragmentToolBarBack {
    private FragmentIvandProfileBinding binding;
    private FragmentIVandProfileViewModel viewModel;
    private CircleImageView profileImage;

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ivand_profile, root, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews();

    }

    private void setUpViews() {
        viewModel = new FragmentIVandProfileViewModel();
        binding.setViewModel(viewModel);
        profileImage = binding.ivIvandProfile;
        titleTextView.setText(getString(R.string.ivand_profile_title));

        viewModel.goToIVandPage.observe(this, go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentIVandActivities.newInstance()).setReplace(false).load();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        setImage();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    private void setImage() {
        avatarHandler.getAvatar(new ParamWithAvatarType(profileImage, G.userId).avatarSize(R.dimen.dp100).avatarType(AvatarHandler.AvatarType.USER).showMain());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.onDestroy();
    }
}
