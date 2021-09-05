package net.iGap.fragments;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentNotificationAndSoundViewModel;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotificationAndSound extends BaseFragment {
    private FragmentNotificationAndSoundBinding binding;
    private FragmentNotificationAndSoundViewModel viewModel;
    private Toolbar notificationAndSoundToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentNotificationAndSoundViewModel(getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE), getResources().getStringArray(R.array.sound_message));
            }
        }).get(FragmentNotificationAndSoundViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_and_sound, container, false);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setFragmentNotificationAndSoundViewModel(viewModel);

        notificationAndSoundToolbar = new Toolbar(getContext());
        notificationAndSoundToolbar.setBackIcon(new BackDrawable(false));
        notificationAndSoundToolbar.setTitle(getString(R.string.notificaion_and_sound));
        notificationAndSoundToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    popBackStackFragment();
                    break;
            }
        });
        binding.toolbar.addView(notificationAndSoundToolbar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.dp(56), Gravity.TOP));

        setupResetNotification();
        showLedDialog();
        showVibrationDialog();
        showPopupNotification();
        showMessageSound();
        showGroupSound();
    }

    @Override
    public void onPause() {
        super.onPause();
        HelperNotification.getInstance().updateSettingValue();
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

    private void showLedDialog() {
        GradientDrawable gradientDrawable = (GradientDrawable) binding.ivLedMessage.getBackground();
        gradientDrawable.setColor(viewModel.ledColorMessage);
        viewModel.showMessageLedDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_colorpicker, true)
                        .title(R.string.st_led_color).titleGravity(GravityEnum.START)
                        .positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                        .onNegative((dialog1, which) -> dialog1.dismiss()).build();
                View view = dialog.getCustomView();
                ColorPicker picker = view.findViewById(R.id.picker);
                SVBar svBar = view.findViewById(R.id.svBar);
                OpacityBar opacityBar = view.findViewById(R.id.opacityBar);
                picker.addSVBar(svBar);
                picker.addOpacityBar(opacityBar);
                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
                    viewModel.setMessagePickerColor(picker.getColor());
                    viewModel.messageLedColor.observe(getViewLifecycleOwner(), integer -> {
                        gradientDrawable.setColor(integer);
                        binding.ivLedMessage.setBackground(gradientDrawable);
                    });
                    dialog.dismiss();
                });
                dialog.show();
            }
        });

        GradientDrawable gradientDrawableGroup = (GradientDrawable) binding.ivLedGroup.getBackground();
        gradientDrawableGroup.setColor(viewModel.ledColorGroup);
        viewModel.showGroupLedDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_colorpicker, true)
                        .title(R.string.st_led_color).titleGravity(GravityEnum.START)
                        .positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
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
                    viewModel.groupLedColor.observe(getViewLifecycleOwner(), integer -> {
                        gradientDrawableGroup.setColor(integer);
                        binding.ivLedGroup.setBackground(gradientDrawableGroup);
                    });
                    dialog.dismiss();
                });
                dialog.show();
            }
        });
    }

    private void showVibrationDialog() {
        AudioManager audioManager = (AudioManager) getContext().getSystemService(getContext().AUDIO_SERVICE);
        viewModel.showMessageVibrationDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null & isShow) {
                int dialogVibrateMessage = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 0);
                new MaterialDialog.Builder(getContext()).title(R.string.st_vibrate).titleGravity(GravityEnum.START).items(R.array.vibrate).alwaysCallSingleChoiceCallback()
                        .itemsCallbackSingleChoice(dialogVibrateMessage, (dialog, view, which, text) -> {
                            viewModel.setMessageVibrateTime(which, true);
                            return true;
                        }).positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                        .onPositive((dialog, which) -> {
                            viewModel.chooseVibrate();
                        }).show();
            }

        });
        viewModel.showGroupVibrationDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null & isShow) {
                int dialogVibrateGroup = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 0);
                new MaterialDialog.Builder(getContext()).title(R.string.st_vibrate).titleGravity(GravityEnum.START).items(R.array.vibrate).alwaysCallSingleChoiceCallback()
                        .itemsCallbackSingleChoice(dialogVibrateGroup, (dialog, view, which, text) -> {
                            viewModel.setGroupVibrateTime(which, true);
                            return true;
                        }).positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                        .onPositive((dialog, which) -> {
                            viewModel.groupChooseVibrate();
                        }).show();
            }

        });
        viewModel.startVibration.observe(getViewLifecycleOwner(), vibrationTime -> {
            if (getContext() != null && vibrationTime != null) {
                if (vibrationTime == -1) {
                    switch (audioManager.getRingerMode()) {
                        case AudioManager.RINGER_MODE_SILENT:
                            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                            break;
                    }
                } else {
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(vibrationTime);
                }
            }
        });
    }

    private void showPopupNotification() {
        viewModel.showMessagePopupNotification.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null & isShow) {
                int settingPopupCondition = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
                new MaterialDialog.Builder(getContext()).title(R.string.st_popupNotification).titleGravity(GravityEnum.START).items(R.array.popup_Notification).alwaysCallSingleChoiceCallback()
                        .itemsCallbackSingleChoice(settingPopupCondition, (dialog, itemView, which, text) -> {
                            viewModel.saveSettingPopupConditionMessage(which);
                            return true;
                        }).positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                        .onPositive((dialog, which) -> {
                            viewModel.setChooseMessagePopup();
                        }).show();
            }

        });
        viewModel.showGroupPopupNotification.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null & isShow) {
                int PopupCondition = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
                new MaterialDialog.Builder(getContext()).title(R.string.st_popupNotification).titleGravity(GravityEnum.START).items(R.array.popup_Notification).alwaysCallSingleChoiceCallback()
                        .itemsCallbackSingleChoice(PopupCondition, (dialog, itemView, which, text) -> {
                            viewModel.saveSettingPopupConditionGroup(which);
                            return true;
                        }).positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                        .onPositive((dialog, which) -> {
                            viewModel.setChooseGroupPopup();
                        }).show();
            }
        });

    }

    private void showMessageSound() {
        viewModel.showMessageSound.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null & isShow) {
                int messageDialogSoundMessage = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
                new MaterialDialog.Builder(getContext()).title(R.string.Ringtone).titleGravity(GravityEnum.START).items(R.array.sound_message).alwaysCallSingleChoiceCallback()
                        .itemsCallbackSingleChoice(messageDialogSoundMessage, (dialog, view, which, text) -> {
                            viewModel.getSoundMessagePosition(which, true);
                            return true;
                        }).positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                        .onPositive((dialog, which) -> {
                            viewModel.setChooseMessageSound();
                        }).show();
            }
        });

        viewModel.playSound.observe(getViewLifecycleOwner(), soundRes -> {
            if (getContext() != null && soundRes != null) {
                MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), soundRes);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> mp.release());
            }
        });

    }

    private void showGroupSound() {
        viewModel.showGroupSound.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null & isShow) {
                int getGroupSoundSelected = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);
                new MaterialDialog.Builder(getContext()).title(R.string.Ringtone).titleGravity(GravityEnum.START).items(R.array.sound_message).alwaysCallSingleChoiceCallback()
                        .itemsCallbackSingleChoice(getGroupSoundSelected, (dialog, view, which, text) -> {
                            viewModel.getSoundGroupPosition(which, true);
                            return true;
                        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                        .onPositive((dialog, which) -> {
                            viewModel.setChooseGroupSound();
                        }).show();
            }

        });
        viewModel.playSound.observe(getViewLifecycleOwner(), musicId -> {
            if (getContext() != null && musicId != null) {
                MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), musicId);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                });
            }
        });
    }

}
