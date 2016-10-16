package com.iGap.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.module.SHP_SETTING;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotification extends Fragment {

    private ImageView imgLED;
    private ViewGroup root, ltLedColor, ltVibrate, ltSound, ltPopupNotification, ltSmartNotification;
    private String page, soundName;
    private int ledColor, poRbDialogSound;
    private TextView txtVibrate, txtSound, txtPopupNotification, txtSmartNotification, txtBack;

    private NumberPicker numberPickerMinutes, numberPickerTimes;

    private SharedPreferences sharedPreferences;

    public FragmentNotification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        page = getArguments().getString("PAGE");

        callObject(view);


        txtBack.setTypeface(G.fontawesome);

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNotification.this).commit();
            }
        });
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        txtPopupNotification.setTypeface(G.arial);
        String popupNotification = sharedPreferences.getString(SHP_SETTING.KEY_NTSG_NOTIFICATIONS_GROUP, "Default");
        txtPopupNotification.setText(popupNotification);
        ltPopupNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(getActivity())
                        .title("Popup Notification")
                        .items(R.array.notifications_notification)
                        .negativeText("CANCEL")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                switch (which) {
                                    case 0:
                                        txtPopupNotification.setText("Default");
                                        editor.putString(SHP_SETTING.KEY_NTSG_NOTIFICATIONS_GROUP, "Default");
                                        editor.apply();
                                        break;
                                    case 1:
                                        txtPopupNotification.setText("Enable");
                                        editor.putString(SHP_SETTING.KEY_NTSG_NOTIFICATIONS_GROUP, "Enable");
                                        editor.apply();
                                        break;
                                    case 2:
                                        txtPopupNotification.setText("Disable");
                                        editor.putString(SHP_SETTING.KEY_NTSG_NOTIFICATIONS_GROUP, "Disable");
                                        editor.apply();

                                        break;
                                }
                            }
                        })
                        .show();
            }
        });


        //========================================================sound
        assert page != null;
        if (page.equals("Group")) {
            poRbDialogSound = sharedPreferences.getInt(SHP_SETTING.KEY_NTSG_SOUND_POSITION_GROUP, 3);
            soundName = sharedPreferences.getString(SHP_SETTING.KEY_NTSG_SOUND_GROUP, "Arrow");
        }
        txtSound.setTypeface(G.arial);
        txtSound.setText(soundName);
        ltSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(getActivity())
                        .title("Ringtone")
                        .titleGravity(GravityEnum.START)
                        .titleColor(getResources().getColor(android.R.color.black))
                        .items(R.array.sound_message)
                        .alwaysCallSingleChoiceCallback()
                        .itemsCallbackSingleChoice(poRbDialogSound, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                switch (which) {
                                    case 0:
                                        MediaPlayer.create(getActivity(), R.raw.igap).start();
                                        break;
                                    case 1:
                                        MediaPlayer.create(getActivity(), R.raw.aooow).start();
                                        break;
                                    case 2:
                                        MediaPlayer.create(getActivity(), R.raw.bbalert).start();
                                        break;
                                    case 3:
                                        MediaPlayer.create(getActivity(), R.raw.boom).start();
                                        break;
                                    case 4:
                                        MediaPlayer.create(getActivity(), R.raw.bounce).start();
                                        break;
                                    case 5:
                                        MediaPlayer.create(getActivity(), R.raw.doodoo).start();
                                        break;
                                    case 6:
                                        MediaPlayer.create(getActivity(), R.raw.igap).start();
                                        break;
                                    case 7:
                                        MediaPlayer.create(getActivity(), R.raw.jing).start();
                                        break;
                                    case 8:
                                        MediaPlayer.create(getActivity(), R.raw.lili).start();
                                        break;
                                    case 9:
                                        MediaPlayer.create(getActivity(), R.raw.msg).start();
                                        break;
                                    case 10:
                                        MediaPlayer.create(getActivity(), R.raw.newa).start();
                                        break;
                                    case 11:
                                        MediaPlayer.create(getActivity(), R.raw.none).start();
                                        break;
                                    case 12:
                                        MediaPlayer.create(getActivity(), R.raw.onelime).start();
                                        break;
                                    case 13:
                                        MediaPlayer.create(getActivity(), R.raw.tone).start();
                                        break;
                                    case 14:
                                        MediaPlayer.create(getActivity(), R.raw.woow).start();
                                        break;
                                }

                                txtSound.setText(text.toString());

                                sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                //save to SHP
                                if (page.equals("Group")) {
                                    editor.putString(SHP_SETTING.KEY_NTSG_SOUND_GROUP, text.toString());
                                    editor.putInt(SHP_SETTING.KEY_NTSG_SOUND_POSITION_GROUP, which);
                                }
                                editor.apply();

                                return true;
                            }
                        })
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .show();
            }
        });


        //========================================================= vibrate
        txtVibrate.setTypeface(G.arial);
        String vibrateMessage = sharedPreferences.getString(SHP_SETTING.KEY_NTSG_VIBRATE_GROUP, "Default");
        txtVibrate.setText(vibrateMessage);
        ltVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(getActivity())
                        .title("Vibrate")
                        .items(R.array.notifications_vibrate)
                        .negativeText("CANCEL")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                switch (which) {
                                    case 0:
                                        txtVibrate.setText("Disable");
                                        editor.putString(SHP_SETTING.KEY_NTSG_VIBRATE_GROUP, "Disable");
                                        editor.apply();
                                        break;
                                    case 1:
                                        txtVibrate.setText("Default");
                                        editor.putString(SHP_SETTING.KEY_NTSG_VIBRATE_GROUP, "Setting default");
                                        editor.apply();
                                        AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                                        switch (am.getRingerMode()) {
                                            case AudioManager.RINGER_MODE_VIBRATE:
                                                Vibrator vSilent = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                                vSilent.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                                                break;
                                        }
                                        break;

                                    case 2:
                                        txtVibrate.setText("System default");
                                        editor.putString(SHP_SETTING.KEY_NTSG_VIBRATE_GROUP, "System default");
                                        editor.apply();

                                        break;
                                    case 3:
                                        txtVibrate.setText("Short");
                                        editor.putString(SHP_SETTING.KEY_NTSG_VIBRATE_GROUP, "Short");
                                        editor.apply();
                                        Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                        vShort.vibrate(200);
                                        break;
                                    case 4:
                                        txtVibrate.setText("Long");
                                        editor.putString(SHP_SETTING.KEY_NTSG_VIBRATE_GROUP, "Long");
                                        editor.apply();
                                        Vibrator vLong = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                        vLong.vibrate(500);
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });


        //==========================================================number pick

        ltSmartNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                boolean wrapInScrollView = true;
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title("Smart Notifications")
                        .customView(R.layout.dialog_number_picker, wrapInScrollView)
                        .positiveText(R.string.B_ok)
                        .negativeText(R.string.B_cancel)
                        .build();

                View view1 = dialog.getCustomView();

                assert view1 != null;
                numberPickerMinutes = (NumberPicker) view1.findViewById(R.id.dialog_np_minutes);
                numberPickerTimes = (NumberPicker) view1.findViewById(R.id.dialog_np_times);

                numberPickerMinutes.setMinValue(0);
                numberPickerMinutes.setMaxValue(10);
                numberPickerMinutes.setWrapSelectorWheel(true);

                numberPickerTimes.setMinValue(0);
                numberPickerTimes.setMaxValue(10);
                numberPickerTimes.setWrapSelectorWheel(true);

                dialog.show();

            }
        });


        //=======================================================================led color
        assert page != null;
        if (page.equals("Group")) {

            ledColor = sharedPreferences.getInt(SHP_SETTING.KEY_NTSG_LED_COLOR_GROUP, -8257792);
        }

        GradientDrawable bgShape = (GradientDrawable) imgLED.getBackground();
        bgShape.setColor(ledColor);
        ltLedColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .customView(R.layout.stns_popup_colorpicer, wrapInScrollView)
                        .positiveText("SET")
                        .negativeText("DISCARD")
                        .title("LED Color")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        }).build();

                View view1 = dialog.getCustomView();
                assert view1 != null;
                final ColorPicker picker = (ColorPicker) view1.findViewById(R.id.picker);
                SVBar svBar = (SVBar) view1.findViewById(R.id.svbar);
                OpacityBar opacityBar = (OpacityBar) view1.findViewById(R.id.opacitybar);
                picker.addSVBar(svBar);
                picker.addOpacityBar(opacityBar);

                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        dialog.dismiss();
                        GradientDrawable bgShape = (GradientDrawable) imgLED.getBackground();
                        bgShape.setColor(picker.getColor());

                        //save to share preferences

                        if (page.equals("Group")) {

                            editor.putInt(SHP_SETTING.KEY_NTSG_LED_COLOR_GROUP, picker.getColor());
                        }


                        editor.apply();
                    }
                });


                dialog.show();
            }
        });

    }

    private void callObject(View view) {

        txtBack = (TextView) view.findViewById(R.id.ntg_txt_back);

        txtPopupNotification = (TextView) view.findViewById(R.id.ntg_txt_desc_notifications);
        ltPopupNotification = (ViewGroup) view.findViewById(R.id.ntg_layout_notifications);
        root = (ViewGroup) view.findViewById(R.id.ntg_fragment_root);

        imgLED = (ImageView) view.findViewById(R.id.ntg_img_ledColorMessage);
        ltLedColor = (ViewGroup) view.findViewById(R.id.ntg_layout_ledColorMessage);

        txtVibrate = (TextView) view.findViewById(R.id.ntg_txt_desc_vibrate);
        ltVibrate = (ViewGroup) view.findViewById(R.id.ntg_layout_vibrate);

        txtSmartNotification = (TextView) view.findViewById(R.id.ntg_txt_desc_smartNotifications);
        ltSmartNotification = (ViewGroup) view.findViewById(R.id.ntg_layout_smartNotifications);

        txtSound = (TextView) view.findViewById(R.id.ntg_txt_desc_sound);
        ltSound = (ViewGroup) view.findViewById(R.id.ntg_layout_sound);


    }
}
