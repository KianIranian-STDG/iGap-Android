package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.activities.ActivityManageSpace;
import net.iGap.databinding.FragmentStorageDataBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.SHP_SETTING;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.DataStorageViewModel;

import static android.content.Context.MODE_PRIVATE;

public class DataStorageFragment extends BaseFragment {

    private FragmentStorageDataBinding binding;
    private DataStorageViewModel viewModel;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_storage_data, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.data_storage))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                })
                .getView()
        );

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
                        .itemsCallbackMultiChoice(values, (dialog, which, text) -> true).positiveText(getResources().getString(R.string.B_ok))
                        .onPositive((dialog, which) -> viewModel.setAutoDownloadOverData(dialog.getSelectedIndices()))
                        .negativeText(getResources().getString(R.string.B_cancel))
                        .show();
            }
        });

        viewModel.getShowAutoDownloadWifiDialog().observe(getViewLifecycleOwner(), values -> {
            if (getContext() != null && values != null) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.title_auto_download_wifi)
                        .items(R.array.auto_download_data)
                        .itemsCallbackMultiChoice(values, (dialog, which, text) -> true).positiveText(R.string.B_ok)
                        .onPositive((dialog, which) -> viewModel.setAutoDownloadOverWifi(dialog.getSelectedIndices()))
                        .negativeText(R.string.cancel)
                        .show();
            }
        });

        viewModel.getShowAutoDownloadRoamingDialog().observe(this, values -> {
            if (getContext() != null && values != null) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.title_auto_download_roaming)
                        .items(R.array.auto_download_data)
                        .itemsCallbackMultiChoice(values, (dialog, which, text) -> true).positiveText(R.string.B_ok)
                        .onPositive((dialog, which) -> viewModel.setAutoDownloadOverRoaming(dialog.getSelectedIndices()))
                        .negativeText(R.string.B_cancel)
                        .show();
            }
        });

        viewModel.getShowClearCashDialog().observe(getViewLifecycleOwner(), data -> {
            if (getContext() != null && data != null && data.length == 7) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .title(R.string.st_title_Clear_Cache)
                        .customView(R.layout.st_dialog_clear_cach, true)
                        .positiveText(R.string.st_title_Clear_Cache)
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
                MaterialDialog inDialog = new MaterialDialog.Builder(getContext()).customView(R.layout.dialog_content_custom, true).build();
                View dialogView = inDialog.getCustomView();
                inDialog.show();

                assert dialogView != null;
                TextView txtTitle = dialogView.findViewById(R.id.txtDialogTitle);
                txtTitle.setText(R.string.clean_up_chat_rooms);

                TextView iconTitle = dialogView.findViewById(R.id.iconDialogTitle);
                iconTitle.setTypeface(ResourcesCompat.getFont(iconTitle.getContext(), R.font.font_icon_old));
                iconTitle.setText(R.string.md_clean_up);

                TextView txtContent = dialogView.findViewById(R.id.txtDialogContent);
                txtContent.setText(R.string.do_you_want_to_clean_all_data_in_chat_rooms);

                TextView txtCancel = dialogView.findViewById(R.id.txtDialogCancel);
                TextView txtOk = dialogView.findViewById(R.id.txtDialogOk);

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
    }
}
