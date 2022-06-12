package net.iGap.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.collect.EvictingQueue;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityManageSpace;
import net.iGap.databinding.FragmentStorageDataBinding;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.FileUtils;
import net.iGap.module.SHP_SETTING;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.viewmodel.DataStorageViewModel;

import java.io.IOException;
import java.security.acl.Permission;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class DataStorageFragment extends BaseFragment {

    private FragmentStorageDataBinding binding;
    private DataStorageViewModel viewModel;
    private Toolbar dataStorageToolbar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new DataStorageViewModel(getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE));
            }
        }).get(DataStorageViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_storage_data, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        try {
            getPermission();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataStorageToolbar = new Toolbar(getContext());
        dataStorageToolbar.setBackIcon(new BackDrawable(false));
        dataStorageToolbar.setTitle(getString(R.string.data_storage));
        dataStorageToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                    popBackStackFragment();
                    break;
            }
        });
        binding.toolbar.addView(dataStorageToolbar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.dp(56), Gravity.TOP));

        viewModel.getGoToDataUsagePage().observe(getViewLifecycleOwner(), isWifiData -> {
            if (getActivity() instanceof ActivityManageSpace && isWifiData != null) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("TYPE", isWifiData);
                FragmentDataUsage fragmentDataUsage = new FragmentDataUsage();
                fragmentDataUsage.setArguments(bundle);
                ((ActivityManageSpace) getActivity()).loadFragment(fragmentDataUsage);
            }
        });

        viewModel.getShowDialogKeepMedia().observe(getViewLifecycleOwner(), selectedPosition -> {
            if (getContext() != null && selectedPosition != null) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.st_keepMedia)
                        .titleGravity(GravityEnum.START)
                        .titleColor(getResources().getColor(android.R.color.black))
                        .items(R.array.keepMedia)
                        .negativeColor(Theme.getColor(Theme.key_button_background))
                        .positiveColor(Theme.getColor(Theme.key_button_background))
                        .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                        .itemsCallbackSingleChoice(selectedPosition, (dialog, itemView, which, text) -> false).positiveText(R.string.B_ok)
                        .negativeText(R.string.B_cancel)
                        .onPositive((dialog, which) -> viewModel.setKeepMediaTime(dialog.getSelectedIndex())).show();
            }
        });

        viewModel.getShowAutoDownloadDataDialog().observe(getViewLifecycleOwner(), values -> {
            if (getContext() != null && values != null) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.title_auto_download_data)
                        .items(R.array.auto_download_data)
                        .negativeColor(Theme.getColor(Theme.key_button_background))
                        .positiveColor(Theme.getColor(Theme.key_button_background))
                        .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                        .itemsCallbackMultiChoice(values, (dialog, which, text) -> true).positiveText(getResources().getString(R.string.B_ok))
                        .onPositive((dialog, which) -> viewModel.setAutoDownloadOverData(Objects.requireNonNull(dialog.getSelectedIndices())))
                        .negativeText(getResources().getString(R.string.B_cancel))
                        .show();
            }
        });

        viewModel.getShowAutoDownloadWifiDialog().observe(getViewLifecycleOwner(), values -> {
            if (getContext() != null && values != null) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.title_auto_download_wifi)
                        .items(R.array.auto_download_data)
                        .negativeColor(Theme.getColor(Theme.key_button_background))
                        .positiveColor(Theme.getColor(Theme.key_button_background))
                        .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                        .itemsCallbackMultiChoice(values, (dialog, which, text) -> true).positiveText(R.string.B_ok)
                        .onPositive((dialog, which) -> viewModel.setAutoDownloadOverWifi(Objects.requireNonNull(dialog.getSelectedIndices())))
                        .negativeText(R.string.cancel)
                        .show();
            }
        });

        viewModel.getShowAutoDownloadRoamingDialog().observe(getViewLifecycleOwner(), values -> {
            if (getContext() != null && values != null) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.title_auto_download_roaming)
                        .items(R.array.auto_download_data)
                        .negativeColor(Theme.getColor(Theme.key_button_background))
                        .positiveColor(Theme.getColor(Theme.key_button_background))
                        .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                        .itemsCallbackMultiChoice(values, (dialog, which, text) -> true).positiveText(R.string.B_ok)
                        .onPositive((dialog, which) -> viewModel.setAutoDownloadOverRoaming(Objects.requireNonNull(dialog.getSelectedIndices())))
                        .negativeText(R.string.B_cancel)
                        .show();
            }
        });

        viewModel.getShowClearCashDialog().observe(getViewLifecycleOwner(), data -> {
            if (getContext() != null && data != null && data.length == 7) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .backgroundColor(Theme.getColor(Theme.key_popup_background))
                        .title(R.string.st_title_Clear_Cache)
                        .customView(R.layout.st_dialog_clear_cach, true)
                        .positiveText(R.string.st_title_Clear_Cache)
                        .negativeColor(Theme.getColor(Theme.key_button_background))
                        .positiveColor(Theme.getColor(Theme.key_button_background))
                        .show();

                View dialogView = dialog.getCustomView();
                assert dialogView != null;
                AppCompatCheckBox checkBoxAll = dialogView.findViewById(R.id.all);
                AppCompatCheckBox checkBoxPhoto = dialogView.findViewById(R.id.photo);
                AppCompatCheckBox checkBoxVideo = dialogView.findViewById(R.id.video);
                AppCompatCheckBox checkBoxDocument = dialogView.findViewById(R.id.document);
                AppCompatCheckBox checkBoxAudio = dialogView.findViewById(R.id.audio);
                AppCompatCheckBox checkBoxMap = dialogView.findViewById(R.id.map);
                AppCompatCheckBox checkBoxOtherFiles = dialogView.findViewById(R.id.other);

                AppCompatTextView txtTotalSize = dialogView.findViewById(R.id.allFileSize);
                AppCompatTextView photo = dialogView.findViewById(R.id.photoFileSize);
                AppCompatTextView video = dialogView.findViewById(R.id.videoFileSize);
                AppCompatTextView document = dialogView.findViewById(R.id.documentFileSize);
                AppCompatTextView txtAudio = dialogView.findViewById(R.id.audioFileSize);
                AppCompatTextView txtMap = dialogView.findViewById(R.id.mapFileSize);
                AppCompatTextView txtOtherFiles = dialogView.findViewById(R.id.otherFileSize);
                txtTotalSize.setText(data[0]);
                photo.setText(data[1]);
                video.setText(data[2]);
                document.setText(data[3]);
                txtAudio.setText(data[4]);
                txtMap.setText(data[5]);
                txtOtherFiles.setText(data[6]);

                checkBoxAll.setOnClickListener(v -> {
                    boolean tmp = ((AppCompatCheckBox) v).isChecked();
                    checkBoxPhoto.setChecked(tmp);
                    checkBoxVideo.setChecked(tmp);
                    checkBoxDocument.setChecked(tmp);
                    checkBoxAudio.setChecked(tmp);
                    checkBoxMap.setChecked(tmp);
                    checkBoxOtherFiles.setChecked(tmp);
                });

                checkBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {

                });
                checkBoxPhoto.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                    checkBoxAll.setChecked(state);
                });
                checkBoxVideo.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                    checkBoxAll.setChecked(state);
                });
                checkBoxDocument.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                    checkBoxAll.setChecked(state);
                });
                checkBoxAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                    checkBoxAll.setChecked(state);
                });
                checkBoxMap.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                    checkBoxAll.setChecked(state);
                });
                checkBoxOtherFiles.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                    checkBoxAll.setChecked(state);
                });

                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewModel.setClearCashData(
                                checkBoxPhoto.isChecked(),
                                checkBoxVideo.isChecked(),
                                checkBoxDocument.isChecked(),
                                checkBoxAudio.isChecked(),
                                checkBoxMap.isChecked(),
                                checkBoxOtherFiles.isChecked());

                        dialog.dismiss();
                    }
                });

                /*dialog.setOnDismissListener(dialog1 -> onCacheCheckedChanged = null);*/
            }
        });

        viewModel.getShowClearAllDialog().observe(getViewLifecycleOwner(), isShow -> {
            if (getContext() != null && isShow != null && isShow) {
                MaterialDialog inDialog = new MaterialDialog.Builder(getContext())
                        .backgroundColor(Theme.getColor(Theme.key_popup_background))
                        .customView(R.layout.dialog_content_custom, true)
                        .negativeColor(Theme.getColor(Theme.key_button_background))
                        .positiveColor(Theme.getColor(Theme.key_button_background))
                        .build();
                View dialogView = inDialog.getCustomView();
                inDialog.show();

                assert dialogView != null;
                TextView txtTitle = dialogView.findViewById(R.id.txtDialogTitle);
                txtTitle.setText(R.string.clean_up_chat_rooms);

                TextView iconTitle = dialogView.findViewById(R.id.iconDialogTitle);
                iconTitle.setTypeface(ResourcesCompat.getFont(iconTitle.getContext(), R.font.font_icons));
                iconTitle.setText(R.string.icon_clearing);

                TextView txtContent = dialogView.findViewById(R.id.txtDialogContent);
                txtContent.setText(R.string.do_you_want_to_clean_all_data_in_chat_rooms);

                TextView txtCancel = dialogView.findViewById(R.id.txtDialogCancel);
                txtCancel.setBackgroundColor(Theme.getColor(Theme.key_button_background));
                TextView txtOk = dialogView.findViewById(R.id.txtDialogOk);
                txtOk.setBackgroundColor(Theme.getColor(Theme.key_button_background));

                txtOk.setOnClickListener(v -> {
                    viewModel.clearAll();
                    inDialog.dismiss();
                });

                txtCancel.setOnClickListener(v -> inDialog.dismiss());
            }
        });

        viewModel.getShowActiveSDCardDialog().observe(getViewLifecycleOwner(), isShow -> {
            if (getContext() != null && isShow != null && isShow) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.are_you_sure)
                        .negativeText(R.string.B_cancel)
                        .content(R.string.change_storage_place)
                        .positiveText(R.string.B_ok)
                        .onPositive((dialog, which) -> viewModel.setActiveSDCard()).show();
            }
        });

        viewModel.getNeedToGetStoragePermission().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onChanged(Boolean aBoolean) {
                try {
                    getPermission();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (Environment.isExternalStorageManager()) {
                new FileUtils().getFileTotalSize(size -> viewModel.getClearCacheSize().set(size));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void getPermission() throws IOException {

        HelperPermission.getStoragePermision(getActivity(), new OnGetPermission() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void Allow() throws IOException {
            }

            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void deny() {
                HelperPermission.showDeniedPermissionMessage(G.context.getString(R.string.permission_storage));
            }
        });
    }
}
