package net.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentUserProfileBinding;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.module.AndroidUtils;
import net.iGap.module.SHP_SETTING;
import net.iGap.request.RequestUserSessionLogout;
import net.iGap.viewmodel.UserProfileViewModel;

import org.jetbrains.annotations.NotNull;
import org.paygear.wallet.WalletActivity;

import java.io.File;
import java.io.IOException;


public class FragmentUserProfile extends BaseFragment {

    private FragmentUserProfileBinding binding;
    private UserProfileViewModel viewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false);
        viewModel = new UserProfileViewModel(G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE));
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.goToAddMemberPage.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                goToAddMemberPage();
            }
        });

        viewModel.goToWalletAgreementPage.observe(this, phoneNumber -> {
            if (phoneNumber != null) {
                new HelperFragment(FragmentWalletAgrement.newInstance(phoneNumber)).load();
            }
        });

        viewModel.goToWalletPage.observe(this, phoneNumber -> {
            if (phoneNumber != null) {
                Intent intent = new Intent(getActivity(), WalletActivity.class);
                intent.putExtra("Language", "fa");
                intent.putExtra("Mobile", "0" + phoneNumber);
                intent.putExtra("PrimaryColor", G.appBarColor);
                intent.putExtra("DarkPrimaryColor", G.appBarColor);
                intent.putExtra("AccentColor", G.appBarColor);
                intent.putExtra("IS_DARK_THEME", G.isDarkTheme);
                intent.putExtra(WalletActivity.LANGUAGE, G.selectedLanguage);
                intent.putExtra(WalletActivity.PROGRESSBAR, G.progressColor);
                intent.putExtra(WalletActivity.LINE_BORDER, G.lineBorder);
                intent.putExtra(WalletActivity.BACKGROUND, G.backgroundTheme);
                intent.putExtra(WalletActivity.BACKGROUND_2, G.backgroundTheme);
                intent.putExtra(WalletActivity.TEXT_TITLE, G.textTitleTheme);
                intent.putExtra(WalletActivity.TEXT_SUB_TITLE, G.textSubTheme);
                startActivityForResult(intent, ActivityMain.WALLET_REQUEST_CODE);
            }
        });

        viewModel.shareInviteLink.observe(this, link -> {
            if (link != null) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, link);
                sendIntent.setType("text/plain");
                Intent openInChooser = Intent.createChooser(sendIntent, "Open in...");
                startActivity(openInChooser);
            }
        });

        viewModel.goToScannerPage.observe(this, go -> {
            if (go != null && go) {
                try {
                    HelperPermission.getCameraPermission(getActivity(), new OnGetPermission() {
                        @Override
                        public void Allow() throws IllegalStateException {
                            IntentIntegrator integrator = new IntentIntegrator(getActivity());
                            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                            integrator.setRequestCode(ActivityMain.requestCodeQrCode);
                            integrator.setBeepEnabled(false);
                            integrator.setPrompt("");
                            integrator.initiateScan();
                        }

                        @Override
                        public void deny() {
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        viewModel.checkLocationPermission.observe(this, isCheck -> {
            if (isCheck != null && isCheck) {
                try {
                    HelperPermission.getLocationPermission(getActivity(), new OnGetPermission() {
                        @Override
                        public void Allow() {
                            viewModel.haveLocationPermission();
                        }

                        @Override
                        public void deny() {
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        viewModel.goToIGapMapPage.observe(this, isGo -> {
            if (isGo != null && isGo) {
                new HelperFragment(FragmentiGapMap.getInstance()).load();
            }
        });

        viewModel.goToFAQPage.observe(this, link -> {
            if (link != null) {
                HelperUrl.openBrowser(link);
            }
        });

        viewModel.goToShowAvatarPage.observe(this, userId -> {
            if (userId != null) {
                FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.setting);
                new HelperFragment(fragment).setReplace(false).load();
            }
        });

        viewModel.setUserAvatar.observe(this, userId -> {
            if (userId != null) {
                avatarHandler.getAvatar(new ParamWithAvatarType(binding.fupUserImage, userId).avatarType(AvatarHandler.AvatarType.USER).showMain());
            }
        });

        viewModel.setUserAvatarPath.observe(this, changeImageModel -> {
            if (changeImageModel != null) {
                if (changeImageModel.getImagePath() == null || !new File(changeImageModel.getImagePath()).exists()) {
                    //Realm realm1 = Realm.getDefaultInstance();
                    binding.fupUserImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) binding.fupUserImage.getContext().getResources().getDimension(R.dimen.dp100), changeImageModel.getInitials(), changeImageModel.getColor()));
                    //realm1.close();
                } else {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(changeImageModel.getImagePath()), binding.fupUserImage);
                }
            }
        });

        viewModel.goToChatPage.observe(this, roomId -> {
            if (roomId != null) {
                new GoToChatActivity(roomId).startActivity();
            }
        });
    }

    private void goToAddMemberPage() {
        final Fragment fragment = RegisteredContactsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "ADD");
        fragment.setArguments(bundle);
        try {
            new HelperFragment(fragment).load();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void getLogout() {
        new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.log_out))
                .content(R.string.content_log_out)
                .positiveText(getResources().getString(R.string.B_ok))
                .negativeText(getResources().getString(R.string.B_cancel))
                .iconRes(R.mipmap.exit_to_app_button)
                .maxIconSize((int) getResources().getDimension(R.dimen.dp24))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new RequestUserSessionLogout().userSessionLogout(new OnUserSessionLogout() {
                            @Override
                            public void onUserSessionLogout() {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        G.selectedCard = null;
                                        HelperLogout.logout();
                                    }
                                });
                            }

                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onTimeOut() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HelperError.showSnackMessage(getResources().getString(R.string.error), false);

                                    }
                                });
                            }
                        });
                    }
                })
                .show();

    }
}
