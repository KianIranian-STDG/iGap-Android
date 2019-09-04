package net.iGap.fragments;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
                                viewModel.setAutoDownloadOverWifi(dialog.getSelectedIndices())
                            }
                        })
                        .negativeText(R.string.cancel)
                        .show();
            }
        });

        viewModel.autoDownloadRoamingListener.observe(this, values -> {
            if (values == null) return;

            new MaterialDialog.Builder(ActivityManageSpace.this)
                    .title(R.string.title_auto_download_roaming)
                    .items(R.array.auto_download_data)
                    .itemsCallbackMultiChoice(values, new MaterialDialog.ListCallbackMultiChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                            SharedPreferences.Editor editor = viewModel.getSharedPreferences().edit();
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1);
                            editor.apply();

                            for (Integer aWhich : which) {
                                if (aWhich > -1) {
                                    if ((aWhich == 0)) {
                                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, aWhich);
                                    } else if ((aWhich == 1)) {
                                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, aWhich);
                                    } else if ((aWhich == 2)) {
                                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, aWhich);
                                    } else if ((aWhich == 3)) {
                                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_FILE, aWhich);
                                    } else if ((aWhich == 4)) {
                                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, aWhich);
                                    } else if ((aWhich == 5)) {
                                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_GIF, aWhich);
                                    }
                                    editor.apply();
                                }
                            }
                            return true;
                        }
                    }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
        });
    }
}
