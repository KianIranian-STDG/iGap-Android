package net.iGap.fragments;


import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentChatSettingsBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StartupActions;
import net.iGap.viewmodel.FragmentChatSettingViewModel;

import org.jetbrains.annotations.NotNull;

import yogesh.firzen.mukkiasevaigal.M;

public class FragmentChatSettings extends BaseFragment {

    private final int MAX_TEXT_SIZE = 30;
    private final int MIN_TEXT_SIZE = 11;

    private FragmentChatSettingViewModel fcsViewModel;
    private FragmentChatSettingsBinding fcsBinding;
    private HelperToolbar mHelperToolbar;

    public FragmentChatSettings() {
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fcsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_settings, container, false);
        return attachToSwipeBack(fcsBinding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        setupToolbar();
        setupTextSizeSetting();

        fcsViewModel.goToChatBackgroundPage.observe(this, go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentChatBackground.newInstance()).setReplace(false).load();
            }
        });
    }

    private void setupTextSizeSetting() {

        SeekBar sb = fcsBinding.stSeekbarMessageTextSize;

        sb.setMax(MAX_TEXT_SIZE - MIN_TEXT_SIZE);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                fcsViewModel.setTextSizeToPref(progress + MIN_TEXT_SIZE);
                fcsViewModel.callbackTextSize.setValue((progress + MIN_TEXT_SIZE) + "");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fcsViewModel.callbackTextSize.observe(G.fragmentActivity, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                fcsBinding.stTxtMessageTextSizeNumber.setText(s);

                try {
                    sb.setProgress(Integer.valueOf(s) - MIN_TEXT_SIZE);
                } catch (Exception e) {
                    sb.setProgress(16 - MIN_TEXT_SIZE);//16 - min = 5 -> for skipping setMin that not support on Apis fewer than 26
                }
            }
        });
    }

    private void setupToolbar() {

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(G.context.getResources().getString(R.string.chat_setting))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        fcsBinding.fcsLayoutToolbar.addView(mHelperToolbar.getView());
    }

    private void initDataBinding() {
        fcsViewModel = new FragmentChatSettingViewModel();
        fcsBinding.setChatSettingsVM(fcsViewModel);
    }
}
