package net.iGap.activities;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityManageSpaceBinding;
import net.iGap.fragments.FragmentDataUsage;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.SHP_SETTING;
import net.iGap.proto.ProtoGlobal;
import net.iGap.viewmodel.ActivityManageSpaceViewModel;

public class ActivityManageSpace extends ActivityEnhanced implements ToolbarListener {

    ActivityManageSpaceBinding activityManageSpaceBinding;
    ActivityManageSpaceViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityManageSpaceBinding = DataBindingUtil.setContentView(this, R.layout.activity_manage_space);
        viewModel = new ActivityManageSpaceViewModel(this);
        activityManageSpaceBinding.setActivityManageSpaceViewModel(viewModel);

        setupToolbar();

        activityManageSpaceBinding.vgMobileDataUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("TYPE", true);
                FragmentDataUsage fragmentDataUsage = new FragmentDataUsage();
                fragmentDataUsage.setArguments(bundle);
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left);
                fragmentTransaction.add(activityManageSpaceBinding.dataUsageContainer.getId(), fragmentDataUsage, fragmentDataUsage.getClass().getName());

                fragmentTransaction.addToBackStack(fragmentDataUsage.getClass().getName());
                fragmentTransaction.commit();
            }
        });
        activityManageSpaceBinding.vgWifiDataUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("TYPE", false);
                FragmentDataUsage fragmentDataUsage = new FragmentDataUsage();
                fragmentDataUsage.setArguments(bundle);
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left);
                fragmentTransaction.add(activityManageSpaceBinding.dataUsageContainer.getId(), fragmentDataUsage);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        initViewModelCallBacks();
    }

    private void initViewModelCallBacks() {

        viewModel.autoDownloadDataListener.observe(this , values -> {
            if (values == null) return;

            new MaterialDialog.Builder(ActivityManageSpace.this)
                    .title(R.string.title_auto_download_data)
                    .items(R.array.auto_download_data)
                    .itemsCallbackMultiChoice( values , new MaterialDialog.ListCallbackMultiChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                            SharedPreferences.Editor editor = viewModel.getSharedPreferences().edit();
                            editor.putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_DATA_FILE, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_DATA_GIF, -1);
                            editor.apply();

                            for (Integer aWhich : which) {

                                if (aWhich == 0) {
                                    editor.putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, aWhich);
                                } else if (aWhich == 1) {
                                    editor.putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, aWhich);
                                } else if (aWhich == 2) {
                                    editor.putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, aWhich);
                                } else if (aWhich == 3) {
                                    editor.putInt(SHP_SETTING.KEY_AD_DATA_FILE, aWhich);
                                } else if (aWhich == 4) {
                                    editor.putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, aWhich);
                                } else if (aWhich == 5) {
                                    editor.putInt(SHP_SETTING.KEY_AD_DATA_GIF, aWhich);
                                }
                                editor.apply();
                            }

                            return true;
                        }
                    }).positiveText(getResources().getString(R.string.B_ok))
                    .negativeText(getResources().getString(R.string.B_cancel))
                    .show();

        });

        viewModel.autoDownloadWifiListener.observe(this , values -> {

            if (values == null) return;

            new MaterialDialog.Builder(ActivityManageSpace.this)
                    .title(R.string.title_auto_download_wifi)
                    .items(R.array.auto_download_data)
                    .itemsCallbackMultiChoice(values, new MaterialDialog.ListCallbackMultiChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                            SharedPreferences.Editor editor = viewModel.getSharedPreferences().edit();

                            editor.putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1);
                            editor.putInt(SHP_SETTING.KEY_AD_WIFI_GIF, -1);
                            editor.apply();

                            for (Integer aWhich : which) {

                                if (aWhich == 0) {
                                    editor.putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, aWhich);
                                } else if (aWhich == 1) {
                                    editor.putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, aWhich);
                                } else if (aWhich == 2) {
                                    editor.putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, aWhich);
                                } else if (aWhich == 3) {
                                    editor.putInt(SHP_SETTING.KEY_AD_WIFI_FILE, aWhich);
                                } else if (aWhich == 4) {

                                    editor.putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, aWhich);
                                } else if (aWhich == 5) {
                                    editor.putInt(SHP_SETTING.KEY_AD_WIFI_GIF, aWhich);
                                }
                                editor.apply();
                            }

                            return true;
                        }
                    }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.cancel)).show();

        });

        viewModel.autoDownloadRoamingListener.observe(this , values ->{
            if (values == null) return;

            new MaterialDialog.Builder(ActivityManageSpace.this)
                    .title(R.string.title_auto_download_roaming)
                    .items(R.array.auto_download_data)
                    .itemsCallbackMultiChoice( values, new MaterialDialog.ListCallbackMultiChoice() {
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

    private void setupToolbar() {

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(this)
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(G.context.getResources().getString(R.string.data_storage))
                .setListener(this);

        activityManageSpaceBinding.amsLayoutToolbar.addView(mHelperToolbar.getView());

    }

    @Override
    public void onLeftIconClickListener(View view) {
        finish();
    }

}
