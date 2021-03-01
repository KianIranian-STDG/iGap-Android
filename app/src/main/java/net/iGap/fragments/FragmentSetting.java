package net.iGap.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityManageSpace;
import net.iGap.activities.ActivityRegistration;
import net.iGap.databinding.FragmentSettingBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentSettingViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetting extends BaseFragment {

    private FragmentSettingBinding binding;
    private FragmentSettingViewModel viewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentSettingViewModel(getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE));
            }
        }).get(FragmentSettingViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar t = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.more_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.settings))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        showMenu();
                    }
                });
        binding.toolbar.addView(t.getView());

        viewModel.setCurrentLanguage();

        viewModel.showDialogDeleteAccount.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showDeleteAccountDialog();
            }
        });

        viewModel.goToManageSpacePage.observe(getViewLifecycleOwner(), aBoolean -> {
            if (getActivity() != null && aBoolean != null && aBoolean) {
                startActivity(new Intent(getActivity(), ActivityManageSpace.class));
            }
        });

        viewModel.goToLanguagePage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentLanguage()).setReplace(true).load();
            }
        });

        viewModel.goToNotificationAndSoundPage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentNotificationAndSound()).setReplace(false).load();
            }
        });

        viewModel.goToPrivacyAndSecurityPage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentPrivacyAndSecurity()).setReplace(false).load();
            }
        });

        viewModel.goToChatSettingsPage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentChatSettings())/*.setReplace(false)*/.load();
            }
        });

        viewModel.showDialogLogout.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showDialogLogout();
            }
        });

        viewModel.showError.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                HelperError.showSnackMessage(getString(R.string.error), false);
            }
        });

        viewModel.goBack.observe(getViewLifecycleOwner(), aBoolean -> {
            if (getActivity() != null && aBoolean != null && aBoolean) {
                getActivity().onBackPressed();
            }
        });

        viewModel.getUpdateForOtherAccount().observe(getViewLifecycleOwner(), isNeedUpdate -> {
            if (getActivity() instanceof ActivityMain && isNeedUpdate != null && isNeedUpdate) {
                //ToDO: handel remove notification for logout account
                ((ActivityMain) getActivity()).updateUiForChangeAccount();
            }
        });

        viewModel.getGoToRegisterPage().observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() != null && isGo != null && isGo) {
                try {
                    NotificationManager nMgr = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancelAll();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying()) {
                    MusicPlayer.stopSound();
                    MusicPlayer.closeLayoutMediaPlayer();
                }
                WebSocketClient.getInstance().connect(true);
                startActivity(new Intent(getActivity(), ActivityRegistration.class));
                getActivity().finish();
            }
        });

        AppUtils.setProgresColler(binding.loading);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (getActivity() != null) {
                for (Fragment f : getActivity().getSupportFragmentManager().getFragments()) {
                    if (f == null) {
                        continue;
                    }
                    if (f instanceof MainFragment || f instanceof FragmentCall) {
                        f.onResume();
                    }
                }
            }
        } catch (Exception e) {
            HelperLog.getInstance().setErrorLog(e);
        }
    }

    public void showMenu() {
        if (getContext() != null) {
            List<String> items = new ArrayList<>();
            items.add(getString(R.string.delete_account));

            new TopSheetDialog(getContext()).setListData(items, -1, position -> viewModel.onDeleteAccountClick()).show();
        }
    }

    private void showDialogLogout() {
        showDialog(G.context.getString(R.string.log_out), G.context.getString(R.string.content_log_out), R.string.md_exit_app, v -> viewModel.logout(), null);
    }

    private void showDeleteAccountDialog() {
        showDialog(G.context.getString(R.string.delete_account), G.context.getString(R.string.delete_account_text) + "\n" + G.context.getString(R.string.delete_account_text_desc),
                R.string.md_delete_acc, v -> {
                    if (getActivity() != null) {
                        FragmentDeleteAccount fragmentDeleteAccount = FragmentDeleteAccount.getInstance(AccountManager.getInstance().getCurrentUser().getPhoneNumber());
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragmentDeleteAccount).setReplace(false).load();
                    }
                }, null);
    }

    private void showDialog(String title, String content, @StringRes int icon, View.OnClickListener onOk, View.OnClickListener onCancel) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        MaterialDialog inDialog = new MaterialDialog.Builder(getActivity()).customView(R.layout.dialog_content_custom, true).build();
        View v = inDialog.getCustomView();

        inDialog.show();

        TextView txtTitle = v.findViewById(R.id.txtDialogTitle);
        txtTitle.setText(title);

        TextView iconTitle = v.findViewById(R.id.iconDialogTitle);
        iconTitle.setText(icon);

        TextView txtContent = v.findViewById(R.id.txtDialogContent);
        txtContent.setText(content);

        TextView txtCancel = v.findViewById(R.id.txtDialogCancel);
        TextView txtOk = v.findViewById(R.id.txtDialogOk);


        txtOk.setOnClickListener(v1 -> {
            inDialog.dismiss();
            if (onOk != null) {
                onOk.onClick(v1);
            }
        });

        txtCancel.setOnClickListener(v12 -> {
            inDialog.dismiss();
            if (onCancel != null) {
                onCancel.onClick(v12);
            }
        });
    }
}
