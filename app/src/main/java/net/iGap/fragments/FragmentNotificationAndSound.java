package net.iGap.fragments;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
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
        showMessageLedDialog();
        showGroupLedDialog();
        showMessageVibrationDialog();
        showGroupVibrationDialog();
        showMessagePopupNotification();
        showGroupPopupNotification();
        showMessageSound();
        showGroupSound();
        setupResetNotification();
    }


    private void showMessagePopupNotification() {
        viewModel.showMessagePopupNotification.observe(getViewLifecycleOwner(), isShow -> {
            int po = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
            new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_popupNotification)).items(R.array.popup_Notification).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(po, new MaterialDialog.ListCallbackSingleChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                    viewModel.callbackPopUpNotificationMessage.set(text.toString());
                    viewModel.setMessagePop(which);
                    return false;
                }
            }).show();
        });
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

    private void showMessageLedDialog() {
        GradientDrawable gradientDrawable = (GradientDrawable) binding.ivLedMessage.getBackground();
        gradientDrawable.setColor(viewModel.ledColorMessage);
        viewModel.showMessageLedDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_colorpicker, true).positiveText(R.string.set).negativeText(R.string.DISCARD).title(R.string.st_led_color)
                        .onNegative((dialog1, which) -> dialog1.dismiss()).build();
                View view = dialog.getCustomView();
                ColorPicker picker = view.findViewById(R.id.picker);
                SVBar svBar = view.findViewById(R.id.svBar);
                OpacityBar opacityBar = view.findViewById(R.id.opacityBar);
                picker.addSVBar(svBar);
                picker.addOpacityBar(opacityBar);
                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
                    viewModel.setMessagePickerColor(picker.getColor());
                    dialog.dismiss();
                });
                dialog.show();
            }
        });
        viewModel.messageLedColor.observe(getViewLifecycleOwner(), integer -> {
            gradientDrawable.setColor(integer);
            binding.ivLedMessage.setBackground(gradientDrawable);

        });
    }

    private void showGroupLedDialog() {
        GradientDrawable gradientDrawable = (GradientDrawable) binding.ivLedGroup.getBackground();
        gradientDrawable.setColor(viewModel.ledColorGroup);
        viewModel.showGroupLedDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_colorpicker, true).positiveText(R.string.set).negativeText(R.string.DISCARD).title(R.string.st_led_color)
                        .onNegative((dialog1, which) -> dialog1.dismiss()).build();
                View view = dialog.getCustomView();
                ColorPicker picker = view.findViewById(R.id.picker);
                SVBar svBar = view.findViewById(R.id.svBar);
                OpacityBar opacityBar = view.findViewById(R.id.opacityBar);
                picker.addSVBar(svBar);
                picker.addOpacityBar(opacityBar);
                picker.setOldCenterColor(picker.getColor());
                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
                    viewModel.setGroupPickerColor(picker.getColor());
                    dialog.dismiss();
                });
                dialog.show();
            }
        });
        viewModel.groupLedColor.observe(getViewLifecycleOwner(), integer -> {
            gradientDrawable.setColor(integer);
            binding.ivLedGroup.setBackground(gradientDrawable);
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

    private void showMessageVibrationDialog() {
        viewModel.showMessageVibrationDialog.observe(getViewLifecycleOwner(), isShow -> {
            new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_vibrate)).items(R.array.vibrate).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                    switch (which) {
                        case 0:
                            getResources().getString(R.string.array_Default);
                            Vibrator vDefault = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                            vDefault.vibrate(350);
                            break;
                        case 1:
                            getResources().getString(R.string.array_Short);
                            Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                            vShort.vibrate(200);

                            break;
                        case 2:
                            getResources().getString(R.string.array_Long);
                            Vibrator vLong = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                            vLong.vibrate(500);
                            break;
                        case 3:
                            getResources().getString(R.string.array_Only_if_silent);
                            AudioManager am2 = (AudioManager) G.fragmentActivity.getSystemService(Context.AUDIO_SERVICE);
                            switch (am2.getRingerMode()) {
                                case AudioManager.RINGER_MODE_SILENT:
                                    Vibrator vSilent = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                    vSilent.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                                    break;
                            }
                            break;
                        case 4:
                            getResources().getString(R.string.array_Disable);
                            break;
                    }
                    viewModel.setMessageVibration(which);
                }
            }).show();

        });
    }

    private void showMessageSound() {
        viewModel.showMessageSound.observe(getViewLifecycleOwner(), isShow -> {
            new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.Ringtone)).titleGravity(GravityEnum.START).items(R.array.sound_message).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(viewModel.messageDialogSoundMessage, (dialog, view, which, text) -> {
                viewModel.playSound(which);
                viewModel.messageSoundMessageSelected = text.toString();
                viewModel.soundMessageWhich = which;
                return true;
            }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).onPositive((dialog, which) -> {
                viewModel.callbackSoundMessage.set(viewModel.messageSoundMessageSelected);
                viewModel.messageDialogSoundMessage = viewModel.soundMessageWhich;
            }).show();
        });
    }

    private void showGroupSound() {
        viewModel.showGroupSound.observe(getViewLifecycleOwner(), isShow -> {
            new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.Ringtone)).titleGravity(GravityEnum.START).items(R.array.sound_message).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(viewModel.groupDialogSoundMessage, (dialog, view, which, text) -> {
                viewModel.playSound(which);
                viewModel.groupSoundMessageSelected = text.toString();
                viewModel.soundMessageGroupWhich = which;
                return true;
            }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    viewModel.callBackSoundGroup.set(viewModel.groupSoundMessageSelected);
                    viewModel.groupDialogSoundMessage = viewModel.soundMessageGroupWhich;
                }
            }).show();
        });
    }

    private void showGroupVibrationDialog() {
        viewModel.showGroupVibrationDialog.observe(getViewLifecycleOwner(), isShow -> {
            new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_vibrate)).items(R.array.vibrate).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    switch (which) {
                        case 0:
                            getResources().getString(R.string.array_Default);
                            Vibrator vDefault = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                            vDefault.vibrate(350);
                            break;
                        case 1:
                            getResources().getString(R.string.array_Short);
                            Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                            vShort.vibrate(200);

                            break;
                        case 2:
                            getResources().getString(R.string.array_Long);
                            Vibrator vLong = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                            vLong.vibrate(500);
                            break;
                        case 3:
                            getResources().getString(R.string.array_Only_if_silent);
                            AudioManager am2 = (AudioManager) G.fragmentActivity.getSystemService(Context.AUDIO_SERVICE);

                            switch (am2.getRingerMode()) {
                                case AudioManager.RINGER_MODE_SILENT:
                                    Vibrator vSilent = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                    vSilent.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                                    break;
                            }
                            break;
                        case 4:
                            getResources().getString(R.string.array_Disable);
                            break;
                    }
                    viewModel.setGroupVibration(which);

                }
            }).show();
        });
    }

    private void showGroupPopupNotification() {
        viewModel.showGroupPopupNotification.observe(getViewLifecycleOwner(), isShow -> {
            int po = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
            new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_popupNotification)).items(R.array.popup_Notification).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(po, new MaterialDialog.ListCallbackSingleChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                    viewModel.callbackPopUpNotificationGroup.set(text.toString());
                    viewModel.setGroupPop(which);
                    return false;

                }
            }).show();
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        HelperNotification.getInstance().updateSettingValue();
    }
}
