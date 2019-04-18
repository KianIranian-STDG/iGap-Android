package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentCustomrtclubProfileBinding;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.viewmodel.CustomerClubViewModel;

public class FragmentCustomerClubProfile extends BaseFragment {

    private FragmentCustomrtclubProfileBinding binding;
    private CustomerClubViewModel viewModel;
    private CircleImageView profileImage;


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
        profileImage = binding.ivCustomerClubProfile;

    }

    @Override
    public void onStart() {
        super.onStart();
        setImage();

    }

    private void setImage() {
        HelperAvatar.getAvatar(G.userId, HelperAvatar.AvatarType.USER, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                if (avatarPath != null) {
                    G.handler.post(() -> {
                        if (G.userId != ownerId)
                            return;
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), profileImage);
                    });
                }
            }

            @Override
            public void onShowInitials(final String initials, final String color, final long ownerId) {
                G.handler.post(() -> {
                    if (G.userId != ownerId)
                        return;
                    profileImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) profileImage.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                });
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.onDestroy();
    }
}
