package net.iGap.fragments;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentNotificationAndSoundBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperNotification;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.ColorPiker;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentNotificationAndSoundViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotificationAndSound extends BaseFragment {

    private FragmentNotificationAndSoundBinding binding;
    private FragmentNotificationAndSoundViewModel viewModel;
    private HelperToolbar mHelperToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_and_sound, container, false);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new FragmentNotificationAndSoundViewModel();
        binding.setFragmentNotificationAndSoundViewModel(viewModel);
        setupToolbar();
        setupLedColor();
        setupResetNotification();
    }

    private void setupToolbar() {

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setDefaultTitle(getString(R.string.notificaion_and_sound))
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        binding.toolbar.addView(mHelperToolbar.getView());
    }

    private void setupLedColor() {
        viewModel.setDirectLedColor(new FragmentNotificationAndSoundViewModel.onChangeLEDColor() {
            @Override
            public void onChenge(int color) {
                    GradientDrawable gradientDrawable = (GradientDrawable) binding.ivLedDirect.getBackground();
                    gradientDrawable.setColor(color);
                    binding.ivLedDirect.setBackground(gradientDrawable);
            }
        });
        /*viewModel.directLedColor.observe(getViewLifecycleOwner(), integer -> {


        });*/
        viewModel.showMaterialDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.stns_popup_colorpicer, true).positiveText(R.string.set)
                        .negativeText(R.string.DISCARD).title(R.string.st_led_color)
                        .onNegative((dialog12, which) -> {
                            dialog12.dismiss();
                        })
                        .onPositive((dialog1, which) -> {

                        }).build();
                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
                    View view = dialog.getCustomView();
                    if (view != null) {
                        ColorPicker picker = view.findViewById(R.id.picker);
                        SVBar svBar = view.findViewById(R.id.svBar);
                        OpacityBar opacityBar = view.findViewById(R.id.opacityBar);
                        picker.setOldCenterColor(viewModel.getLedColorMessage());
                        picker.addSVBar(svBar);
                        picker.addOpacityBar(opacityBar);
                        viewModel.setNewColor(picker.getColor());
                    }
                });
                dialog.show();
            }
        });


    }


    private void setupResetNotification() {

        binding.llResetNotifications.setOnClickListener(v -> {
            if (getActivity() != null) {
                new MaterialDialog.Builder(getActivity()).title(R.string.st_title_reset).content(R.string.st_dialog_reset_all_notification).positiveText(R.string.st_dialog_reset_all_notification_yes).negativeText(R.string.st_dialog_reset_all_notification_no).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        viewModel.onResetDataInSharedPreference();
                        Toast.makeText(getActivity(), R.string.st_reset_all_notification, Toast.LENGTH_SHORT).show();
                        removeFromBaseFragment(FragmentNotificationAndSound.this);
                        new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentNotificationAndSound()).setReplace(false).load();
                    }
                }).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        HelperNotification.getInstance().updateSettingValue();
    }
}
