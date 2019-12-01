package net.iGap.fragments;


import android.content.SharedPreferences;
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
import androidx.lifecycle.Observer;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.databinding.FragmentNotificationAndSoundBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperNotification;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentNotificationAndSoundViewModel;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotificationAndSound extends BaseFragment {

    private SharedPreferences sharedPreferences;

    private FragmentNotificationAndSoundBinding binding;
    private FragmentNotificationAndSoundViewModel viewModel;
    private HelperToolbar mHelperToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_and_sound, container, false);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new FragmentNotificationAndSoundViewModel();
        binding.setFragmentNotificationAndSoundViewModel(viewModel);
        setupToolbar();

        binding.asnToolbar.setBackgroundColor(new Theme().getAccentColor(getContext()));

        binding.stnsRippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) throws IOException {
                popBackStackFragment();
            }
        });

        GradientDrawable bgShapeAlert = (GradientDrawable) binding.stnsImgLedColorMessage.getBackground();
        bgShapeAlert.setColor(viewModel.ledColorMessage);

        GradientDrawable bgShapeGroup = (GradientDrawable) binding.stnsImgLedColorGroup.getBackground();
        bgShapeGroup.setColor(viewModel.ledColorGroup);


        binding.stLayoutResetAllNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                    new MaterialDialog.Builder(getActivity()).title(R.string.st_title_reset).content(R.string.st_dialog_reset_all_notification).positiveText(R.string.st_dialog_reset_all_notification_yes).negativeText(R.string.st_dialog_reset_all_notification_no).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 0);
                            editor.putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
                            editor.putInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
                            editor.putString(SHP_SETTING.KEY_STNS_SOUND_MESSAGE, G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));
                            editor.putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 0);
                            editor.putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
                            editor.putInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);
                            editor.putString(SHP_SETTING.KEY_STNS_SOUND_GROUP, G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));
                            editor.putInt(SHP_SETTING.KEY_STNS_APP_SOUND_NEW, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE_NEW, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW_NEW, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND_NEW, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_SEPARATE_NOTIFICATION, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 1);
                            editor.putInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 1);
                            editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_1_hour));
                            editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
                            editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
                            editor.apply();
                            Toast.makeText(getActivity(), R.string.st_reset_all_notification, Toast.LENGTH_SHORT).show();

                            removeFromBaseFragment(FragmentNotificationAndSound.this);
                            new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentNotificationAndSound()).setReplace(false).load();
                        }
                    }).show();
                }
            }
        });

        viewModel.callLedDirectBackground.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null) {
                    GradientDrawable bgShape = (GradientDrawable) binding.stnsImgLedColorMessage.getBackground();
                    bgShape.setColor(integer);
                    binding.stnsImgLedColorMessage.setBackground(bgShape);
                }

            }
        });
        viewModel.callLedGroupBackground.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                GradientDrawable bgShape = (GradientDrawable) binding.stnsImgLedColorMessage.getBackground();
                bgShape.setColor(integer);
                binding.stnsImgLedColorGroup.setBackground(bgShape);
            }
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

        binding.fnsLayoutToolbar.addView(mHelperToolbar.getView());
    }


    @Override
    public void onPause() {
        super.onPause();
        HelperNotification.getInstance().updateSettingValue();
    }
}
