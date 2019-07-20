package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.beepTunes.PurchaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;

public class BeepTunesProfileFragment extends BottomSheetDialogFragment {
    public AvatarHandler avatarHandler;
    private View rootView;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_beeptunes_user_profile, container);
        avatarHandler = new AvatarHandler();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView userName = rootView.findViewById(R.id.tv_userProfile_userName);
        userName.setText(G.displayName);
        profileImage = rootView.findViewById(R.id.iv_userProfile_userImage);
        TextView perchesList = rootView.findViewById(R.id.tv_userProfile_downloads);
        perchesList.setOnClickListener(v -> {

        });

    }

    @Override
    public void onStart() {
        super.onStart();
        avatarHandler.registerChangeFromOtherAvatarHandler();
        avatarHandler.getAvatar(new ParamWithAvatarType(profileImage, G.userId).avatarType(AvatarHandler.AvatarType.USER).showMain());
    }

    @Override
    public void onStop() {
        super.onStop();
        avatarHandler.unregisterChangeFromOtherAvatarHandler();
    }

    @Override
    public int getTheme() {
        if (G.isDarkTheme) {
            return R.style.BaseBottomSheetDialog;
        } else {
            return R.style.BaseBottomSheetDialogLight;
        }
    }
}