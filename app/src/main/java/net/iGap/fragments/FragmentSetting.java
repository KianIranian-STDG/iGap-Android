package net.iGap.fragments;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityManageSpace;
import net.iGap.activities.ActivityRegisteration;
import net.iGap.databinding.FragmentSettingBinding;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.AppUtils;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentSettingViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetting extends BaseFragment {

    public static DateType dateType;

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

        viewModel.showDialogDeleteAccount.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showDeleteAccountDialog();
            }
        });

        viewModel.goToManageSpacePage.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startActivity(new Intent(G.fragmentActivity, ActivityManageSpace.class));
            }
        });

        viewModel.goToLanguagePage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentLanguage()).setReplace(false).load();
            }
        });

        viewModel.goToNotificationAndSoundPage.observe(this, go -> {
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
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentChatSettings()).setReplace(false).load();
            }
        });

        viewModel.showDialogLogout.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showDialogLogout();
            }
        });

        viewModel.showError.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.error), false);
            }
        });

        viewModel.goBack.observe(getViewLifecycleOwner(), aBoolean -> {
            if (getActivity() != null && aBoolean != null && aBoolean) {
                getActivity().onBackPressed();
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
                    if (f instanceof FragmentMain || f instanceof FragmentCall) {
                        f.onResume();
                    }
                }
            }
        } catch (Exception e) {
            HelperLog.setErrorLog(e);
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
        if (getActivity() != null) {
            MaterialDialog inDialog = new MaterialDialog.Builder(getActivity()).customView(R.layout.dialog_content_custom, true).build();
            View v = inDialog.getCustomView();

            inDialog.show();

            TextView txtTitle = v.findViewById(R.id.txtDialogTitle);
            txtTitle.setText(getString(R.string.log_out));

            TextView iconTitle = v.findViewById(R.id.iconDialogTitle);
            iconTitle.setText(R.string.md_exit_app);

            TextView txtContent = v.findViewById(R.id.txtDialogContent);
            txtContent.setText(R.string.content_log_out);

            TextView txtCancel = v.findViewById(R.id.txtDialogCancel);
            TextView txtOk = v.findViewById(R.id.txtDialogOk);

            txtOk.setOnClickListener(v1 -> {
                inDialog.dismiss();
                viewModel.logout();
            });

            txtCancel.setOnClickListener(v12 -> inDialog.dismiss());
        }
    }

    private void showDeleteAccountDialog() {
        if (getContext() != null) {
            MaterialDialog inDialog = new MaterialDialog.Builder(getContext()).customView(R.layout.dialog_content_custom, true).build();
            View v = inDialog.getCustomView();

            inDialog.show();

            TextView txtTitle = v.findViewById(R.id.txtDialogTitle);
            txtTitle.setText(getString(R.string.delete_account));

            TextView iconTitle = v.findViewById(R.id.iconDialogTitle);
            iconTitle.setText(R.string.md_delete_acc);

            TextView txtContent = v.findViewById(R.id.txtDialogContent);
            String text = getString(R.string.delete_account_text) + "\n" + getString(R.string.delete_account_text_desc);
            txtContent.setText(text);

            TextView txtCancel = v.findViewById(R.id.txtDialogCancel);
            TextView txtOk = v.findViewById(R.id.txtDialogOk);


            txtOk.setOnClickListener(v1 -> {
                inDialog.dismiss();
                if (getActivity() != null) {
                    FragmentDeleteAccount fragmentDeleteAccount = new FragmentDeleteAccount();
                    Bundle bundle = new Bundle();
                    bundle.putString("PHONE", viewModel.phoneNumber);
                    fragmentDeleteAccount.setArguments(bundle);
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragmentDeleteAccount).setReplace(false).load();
                }
            });

            txtCancel.setOnClickListener(v12 -> inDialog.dismiss());
        }
    }

    public interface DateType {

        void dataName(String type);
    }
}
