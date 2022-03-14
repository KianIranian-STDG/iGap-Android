package net.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentProfileBinding;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUrl;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.viewmodel.UserProfileViewModel;

import java.io.IOException;

import static net.iGap.activities.ActivityMain.WALLET_REQUEST_CODE;
import static net.iGap.helper.HelperPermission.showDeniedPermissionMessage;

public class FragmentProfile extends BaseFragment {
    private FragmentProfileBinding binding;
    private UserProfileViewModel viewModel;

    public static FragmentProfile newInstance() {

        Bundle args = new Bundle();

        FragmentProfile fragment = new FragmentProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getParentFragment(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserProfileViewModel(avatarHandler);
            }
        }).get(UserProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        /*viewModel.init();*/
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getGoToWalletPage().observe(getViewLifecycleOwner(), phoneNumber -> {
            if (getActivity() != null && phoneNumber != null) {
//                getActivity().startActivityForResult(new HelperWallet().goToWallet(getActivity(), new Intent(getActivity(), WalletActivity.class), phoneNumber, false), WALLET_REQUEST_CODE);
            }
        });

        viewModel.getGoToAddMemberPage().observe(getViewLifecycleOwner(), aBoolean -> {
            if (getActivity() != null && aBoolean != null && aBoolean) {
                try {
                    Fragment fragment = RegisteredContactsFragment.newInstance(true, false, RegisteredContactsFragment.ADD);
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });

        viewModel.goToChatPage.observe(getViewLifecycleOwner(), data -> {
            if (getActivity() != null && data != null) {
                new GoToChatActivity(data.getRoomId()).setPeerID(data.getPeerId()).startActivity(getActivity());
            }
        });

        viewModel.goToScannerPage.observe(getViewLifecycleOwner(), go -> {
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
                            showDeniedPermissionMessage(G.context.getString(R.string.permission_camera));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        viewModel.checkLocationPermission.observe(getViewLifecycleOwner(), isCheck -> {
            if (isCheck != null && isCheck) {
                try {
                    HelperPermission.getLocationPermission(getActivity(), new OnGetPermission() {
                        @Override
                        public void Allow() {
                            viewModel.haveLocationPermission();
                        }

                        @Override
                        public void deny() {
                            showDeniedPermissionMessage(G.context.getString(R.string.permission_location));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        viewModel.shareInviteLink.observe(getViewLifecycleOwner(), link -> {
            if (link != null) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, link);
                sendIntent.setType("text/plain");
                Intent openInChooser = Intent.createChooser(sendIntent, "Open in...");
                startActivity(openInChooser);
            }
        });


        viewModel.goToContactsPage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), RegisteredContactsFragment.newInstance(false, false, RegisteredContactsFragment.CONTACTS)).setReplace(false).load();
            }
        });


        viewModel.goToUserScorePage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentUserScore()).setReplace(false).load();
            }
        });

        viewModel.goToIGapMapPage.observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() != null && isGo != null && isGo) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentiGapMap.getInstance()).setReplace(false).load();
            }
        });

        viewModel.goToFAQPage.observe(getViewLifecycleOwner(), link -> {
            if (link != null) {
                HelperUrl.openBrowser(link);
            }
        });

        viewModel.goToSettingPage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentSetting()).setReplace(false).load();
            }
        });
        viewModel.getUpdateNewTheme().observe(getViewLifecycleOwner(), isUpdate -> {
            if (getActivity() != null && isUpdate != null && isUpdate) {
                Fragment frg;
                frg = getActivity().getSupportFragmentManager().findFragmentByTag(BottomNavigationFragment.class.getName());
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
            }
        });

        viewModel.showDialogBeLastVersion.observe(getViewLifecycleOwner(), isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                new MaterialDialog.Builder(getActivity())
                        .cancelable(false)
                        .title(R.string.app_version_change_log).titleGravity(GravityEnum.CENTER)
                        .titleColor(new Theme().getPrimaryColor(getActivity()))
                        .content(R.string.updated_version_title)
                        .contentGravity(GravityEnum.CENTER)
                        .positiveText(R.string.ok).itemsGravity(GravityEnum.START).show();
            }
        });

        viewModel.showDialogUpdate.observe(getViewLifecycleOwner(), body -> {
            if (getActivity() != null && body != null) {
                new MaterialDialog.Builder(getActivity())
                        .cancelable(false)
                        .title(R.string.app_version_change_log).titleGravity(GravityEnum.CENTER)
                        .titleColor(new Theme().getPrimaryColor(getActivity()))
                        .content(body)
                        .contentGravity(GravityEnum.CENTER)
                        .positiveText(R.string.startUpdate).itemsGravity(GravityEnum.START).onPositive((dialog, which) -> {
                    String url = BuildConfig.UPDATE_LINK;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }).negativeText(R.string.cancel).show();
            }
        });
    }
}
