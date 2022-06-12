package net.iGap.fragments.beepTunes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.messenger.theme.Theme;
import net.iGap.module.FontIconTextView;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.R;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;

public class BeepTunesProfileFragment extends BaseBottomSheet {
    public static final String SYNC_FRAGMENT = "syncSong";
    public static final String PERCHES_FRAGMENT = "perchesSong";
    public static final String FAVORITE_FRAGMENT = "favoriteSong";

    public AvatarHandler avatarHandler;

    private View rootView;
    private ImageView profileImage;

    private OnProfileCallBack callBack;

    public void setCallBack(OnProfileCallBack callBack) {
        this.callBack = callBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_beeptunes_user_profile, container);
        avatarHandler = new AvatarHandler();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView userName = rootView.findViewById(R.id.tv_userProfile_userName);
        userName.setTextColor(Theme.getColor(Theme.key_title_text));
        profileImage = rootView.findViewById(R.id.iv_userProfile_userImage);
        ViewGroup perchesSong = rootView.findViewById(R.id.cl_beepTunesProfile_perchesSong);
        ViewGroup syncSong = rootView.findViewById(R.id.cl_beepTunesProfile_syncSong);
        ViewGroup favoriteSong = rootView.findViewById(R.id.cl_beepTunesProfile_favoriteSong);
        FontIconTextView fontIconTextView2 = rootView.findViewById(R.id.fontIconTextView2);
        fontIconTextView2.setTextColor(Theme.getColor(Theme.key_icon));
        userName.setText(AccountManager.getInstance().getCurrentUser().getName());
        TextView purchase = rootView.findViewById(R.id.purchase);
        purchase.setTextColor(Theme.getColor(Theme.key_title_text));
        FontIconTextView fontIconTextView3 = rootView.findViewById(R.id.fontIconTextView3);
        fontIconTextView3.setTextColor(Theme.getColor(Theme.key_icon));
        TextView sync = rootView.findViewById(R.id.sync);
        sync.setTextColor(Theme.getColor(Theme.key_title_text));
        FontIconTextView fontIconTextView4 = rootView.findViewById(R.id.fontIconTextView4);
        fontIconTextView4.setTextColor(Theme.getColor(Theme.key_icon));
        TextView favorite = rootView.findViewById(R.id.favorite);
        favorite.setTextColor(Theme.getColor(Theme.key_title_text));

        syncSong.setOnClickListener(v -> {
            callBack.onClick(SYNC_FRAGMENT);
            dismiss();
        });

        perchesSong.setOnClickListener(v -> {
            callBack.onClick(PERCHES_FRAGMENT);
        });

        favoriteSong.setOnClickListener(v -> {
            callBack.onClick(FAVORITE_FRAGMENT);
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        avatarHandler.registerChangeFromOtherAvatarHandler();
        avatarHandler.getAvatar(new ParamWithAvatarType(profileImage, AccountManager.getInstance().getCurrentUser().getId()).avatarType(AvatarHandler.AvatarType.USER).showMain());
    }

    @Override
    public void onStop() {
        super.onStop();
        avatarHandler.unregisterChangeFromOtherAvatarHandler();
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    @FunctionalInterface
    public interface OnProfileCallBack {
        void onClick(String type);
    }
}
