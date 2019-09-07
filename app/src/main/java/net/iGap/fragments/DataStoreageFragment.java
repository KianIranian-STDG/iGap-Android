package net.iGap.fragments;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.activities.ActivityManageSpace;
import net.iGap.databinding.FragmentStorageDataBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.DataStorageViewModel;

import static android.content.Context.MODE_PRIVATE;

public class DataStoreageFragment extends BaseFragment {

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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
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

        viewModel.getGoToDataUsagePage().observe(getViewLifecycleOwner(), isMobileData -> {
            if (getActivity() instanceof ActivityManageSpace && isMobileData != null) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("TYPE", isMobileData);
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
                        .itemsCallbackSingleChoice(selectedPosition, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                /*switch (which) {
                                    case 0: {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 0);
                                        editor.apply();
                                        callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_forever));
                                        break;
                                    }
                                    case 1: {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 30);
                                        editor.apply();
                                        callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_1month));
                                        break;
                                    }
                                    case 2: {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 180);
                                        editor.apply();
                                        callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_6month));
                                        break;
                                    }
                                }*/
                                return false;
                            }
                        }).positiveText(R.string.B_ok)
                        .negativeText(R.string.B_cancel)
                        .onPositive((dialog, which) -> viewModel.setKeepMediaTime(dialog.getSelectedIndex())).show();
            }
        });

        viewModel.getShowAutoDownloadDataDialog().observe(getViewLifecycleOwner(), values -> {
            if (getContext() != null && values != null) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.title_auto_download_data)
                        .items(R.array.auto_download_data)
                        .itemsCallbackMultiChoice(values, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                return true;
                            }
                        }).positiveText(getResources().getString(R.string.B_ok))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                viewModel.setAutoDownloadOverData(dialog.getSelectedIndices());
                            }
                        })
                        .negativeText(getResources().getString(R.string.B_cancel))
                        .show();
            }
        });

        viewModel.getShowAutoDownloadWifiDialog().observe(getViewLifecycleOwner(), values -> {
            if (getContext() != null && values != null) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.title_auto_download_wifi)
                        .items(R.array.auto_download_data)
                        .itemsCallbackMultiChoice(values, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                                return true;
                            }
                        }).positiveText(R.string.B_ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                viewModel.setAutoDownloadOverWifi(dialog.getSelectedIndices());
                            }
                        })
                        .negativeText(R.string.cancel)
                        .show();
            }
        });

        viewModel.getShowAutoDownloadRoamingDialog().observe(this, values -> {
            if (getContext() != null && values != null) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.title_auto_download_roaming)
                        .items(R.array.auto_download_data)
                        .itemsCallbackMultiChoice(values, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                                return true;
                            }
                        }).positiveText(R.string.B_ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                viewModel.setAutoDownloadOverRoaming(dialog.getSelectedIndices());
                            }
                        })
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

                final CompoundButton.OnCheckedChangeListener onCacheCheckedChanged = (buttonView, isChecked) -> {
                    if (buttonView.getId() == R.id.all) {
                        checkBoxPhoto.setChecked(isChecked);
                        checkBoxVideo.setChecked(isChecked);
                        checkBoxDocument.setChecked(isChecked);
                        checkBoxAudio.setChecked(isChecked);
                        checkBoxMap.setChecked(isChecked);
                        checkBoxOtherFiles.setChecked(isChecked);
                    }else {
                        boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                        checkBoxAll.setChecked(state);
                    }
                };

                checkBoxAll.setOnCheckedChangeListener(onCacheCheckedChanged);
                checkBoxPhoto.setOnCheckedChangeListener(onCacheCheckedChanged);
                checkBoxVideo.setOnCheckedChangeListener(onCacheCheckedChanged);
                checkBoxDocument.setOnCheckedChangeListener(onCacheCheckedChanged);
                checkBoxAudio.setOnCheckedChangeListener(onCacheCheckedChanged);
                checkBoxMap.setOnCheckedChangeListener(onCacheCheckedChanged);
                checkBoxOtherFiles.setOnCheckedChangeListener(onCacheCheckedChanged);

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

                dialog.setOnDismissListener(dialog1 -> onCacheCheckedChanged = null);
            }
        });

        viewModel.getShowClearAllDialog().observe(getViewLifecycleOwner(),isShow->{
            if (getContext()!= null && isShow != null && isShow){
                MaterialDialog inDialog = new MaterialDialog.Builder(getContext()).customView(R.layout.dialog_content_custom, true).build();
                View dialogView = inDialog.getCustomView();
                inDialog.show();

                assert dialogView != null;
                TextView txtTitle = dialogView.findViewById(R.id.txtDialogTitle);
                txtTitle.setText(R.string.clean_up_chat_rooms);

                TextView iconTitle = dialogView.findViewById(R.id.iconDialogTitle);
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

        viewModel.getShowActiveSDCardDialog().observe(getViewLifecycleOwner(),isShow->{
            if (getContext()!= null && isShow!= null && isShow){
                new MaterialDialog.Builder(getContext())
                        .title(R.string.are_you_sure)
                        .negativeText(R.string.B_cancel)
                        .content(R.string.change_storage_place)
                        .positiveText(R.string.B_ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                viewModel.setActiveSDCard();
                            }
                        }).show();
            }
        });
    }
}
