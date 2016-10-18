package com.iGap.fragments;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmChatRoom;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmNotificationSetting;
import com.iGap.realm.RealmRoom;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import io.realm.Realm;

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

    private long roomId;
    private String realmNotification = "Default";
    private String realmVibrate = "Disable";
    private String realmSound = "iGap";
    private int realmIdSound = 0;
    private String realmSmartNotification;
    private int realmMinutes;
    private int realmTimes;
    private int realmLedColor;

    private RealmNotificationSetting realmNotificationSetting;

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
        roomId = getArguments().getLong("ID");
        callObject(view);


        //=================================================Realm


        switch (page) {
            case "GROUP": {


                Realm realm = Realm.getDefaultInstance();

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();

                if (realmGroupRoom.getRealmNotificationSetting() == null) {
                    setRealm(realm, realmGroupRoom, null, null);
                } else {

                    RealmNotificationSetting realmNotificationSetting = realmGroupRoom.getRealmNotificationSetting();
                }
                getRealm();

                realm.close();
            }

            break;
            case "CHANNEL": {
                Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                if (realmChannelRoom.getRealmNotificationSetting() == null) {
                    setRealm(realm, null, realmChannelRoom, null);
                }


                realmNotificationSetting = realmChannelRoom.getRealmNotificationSetting();
                getRealm();

                getRealm();
                realm.close();
                break;
            }
            case "CONTACT": {

                Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                realmNotificationSetting = realmChatRoom.getRealmNotificationSetting();

                if (realmChatRoom.getRealmNotificationSetting() == null) {
                    setRealm(realm, null, null, realmChatRoom);
                }


                realmNotificationSetting = realmChatRoom.getRealmNotificationSetting();
                getRealm();

                getRealm();
                realm.close();

                break;
            }
        }
        //=================================================Realm


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


        txtPopupNotification.setTypeface(G.arial);
        String popupNotification = realmNotification;
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
                                switch (which) {
                                    case 0: {
                                        txtPopupNotification.setText("Default");
                                        Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                                switch (page) {
                                                    case "GROUP": {
                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setNotification("Default");
                                                        break;
                                                    }
                                                    case "CHANNEL": {
                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setNotification("Default");
                                                        break;
                                                    }
                                                    case "CONTACT": {
                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setNotification("Default");
                                                        break;
                                                    }
                                                }

                                            }


                                        });
                                        realm.close();
                                        break;
                                    }
                                    case 1: {
                                        txtPopupNotification.setText("Enable");
                                        Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                                switch (page) {
                                                    case "GROUP": {
                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setNotification("Enable");
                                                        break;
                                                    }
                                                    case "CHANNEL": {
                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setNotification("Enable");
                                                        break;
                                                    }
                                                    case "CONTACT": {
                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setNotification("Enable");
                                                        break;
                                                    }
                                                }

                                            }


                                        });
                                        realm.close();
                                        break;
                                    }
                                    case 2: {
                                        txtPopupNotification.setText("Disable");
                                        Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                                switch (page) {
                                                    case "GROUP": {
                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setNotification("Disable");
                                                        break;
                                                    }
                                                    case "CHANNEL": {
                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setNotification("Disable");
                                                        break;
                                                    }
                                                    case "CONTACT": {
                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setNotification("Disable");
                                                        break;
                                                    }
                                                }

                                            }

                                        });
                                        realm.close();
                                        break;
                                    }
                                }
                            }
                        })
                        .show();
            }
        });


        //========================================================sound
        txtSound.setTypeface(G.arial);
        txtSound.setText(realmSound);
        ltSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(getActivity())
                        .title("Ringtone")
                        .titleGravity(GravityEnum.START)
                        .titleColor(getResources().getColor(android.R.color.black))
                        .items(R.array.sound_message)
                        .alwaysCallSingleChoiceCallback()
                        .itemsCallbackSingleChoice(realmIdSound, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, final int which, final CharSequence text) {

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

                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                        switch (page) {
                                            case "GROUP": {
                                                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                realmGroupRoom.getRealmNotificationSetting().setSound(text.toString());
                                                realmGroupRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                                                break;
                                            }
                                            case "CHANNEL": {
                                                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                realmChannelRoom.getRealmNotificationSetting().setSound(text.toString());
                                                realmChannelRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                                                break;
                                            }
                                            case "CONTACT": {
                                                RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                realmChatRoom.getRealmNotificationSetting().setSound(text.toString());
                                                realmChatRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                                                break;
                                            }
                                        }

                                    }


                                });
                                realm.close();
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
        txtVibrate.setText(realmVibrate);
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
                                switch (which) {
                                    case 0: {
                                        txtVibrate.setText("Disable");
                                        Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                                switch (page) {
                                                    case "GROUP": {
                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setVibrate("Disable");
                                                        break;
                                                    }
                                                    case "CHANNEL": {
                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setVibrate("Disable");
                                                        break;
                                                    }
                                                    case "CONTACT": {
                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setVibrate("Disable");
                                                        break;
                                                    }
                                                }

                                            }


                                        });
                                        realm.close();

                                        break;
                                    }
                                    case 1: {
                                        txtVibrate.setText("Setting default");

                                        Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                                switch (page) {
                                                    case "GROUP": {
                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setVibrate("Setting default");
                                                        break;
                                                    }
                                                    case "CHANNEL": {
                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setVibrate("Setting default");
                                                        break;
                                                    }
                                                    case "CONTACT": {
                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setVibrate("Setting default");
                                                        break;
                                                    }
                                                }

                                            }


                                        });
                                        realm.close();
                                        AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                                        switch (am.getRingerMode()) {
                                            case AudioManager.RINGER_MODE_VIBRATE:
                                                Vibrator vSilent = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                                vSilent.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                                                break;
                                        }
                                        break;
                                    }

                                    case 2: {
                                        txtVibrate.setText("System default");
                                        Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                                switch (page) {
                                                    case "GROUP": {
                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setVibrate("System default");
                                                        break;
                                                    }
                                                    case "CHANNEL": {
                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setVibrate("System default");
                                                        break;
                                                    }
                                                    case "CONTACT": {
                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setVibrate("System default");
                                                        break;
                                                    }
                                                }

                                            }


                                        });
                                        realm.close();


                                        break;
                                    }
                                    case 3: {
                                        txtVibrate.setText("Short");
                                        Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                                switch (page) {
                                                    case "GROUP": {
                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setVibrate("Short");
                                                        break;
                                                    }
                                                    case "CHANNEL": {
                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setVibrate("Short");
                                                        break;
                                                    }
                                                    case "CONTACT": {
                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setVibrate("Short");
                                                        break;
                                                    }
                                                }

                                            }


                                        });
                                        realm.close();
                                        Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                        vShort.vibrate(200);
                                        break;
                                    }
                                    case 4: {
                                        txtVibrate.setText("Long");

                                        Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                                switch (page) {
                                                    case "GROUP": {
                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setVibrate("Long");
                                                        break;
                                                    }
                                                    case "CHANNEL": {
                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setVibrate("DisLongable");
                                                        break;
                                                    }
                                                    case "CONTACT": {
                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setVibrate("Long");
                                                        break;
                                                    }
                                                }

                                            }


                                        });
                                        realm.close();
                                        Vibrator vLong = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                        vLong.vibrate(500);
                                        break;
                                    }
                                }
                            }
                        })
                        .show();
            }
        });


        //==========================================================number pick

        txtSmartNotification.setText("Sound at must " + realmTimes + " times within " + realmMinutes + " minutes");
        ltSmartNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
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
                numberPickerTimes.setMinValue(0);
                numberPickerTimes.setMaxValue(10);
                numberPickerMinutes.setWrapSelectorWheel(true);

                numberPickerMinutes.setValue(realmMinutes);
                numberPickerTimes.setValue(realmTimes);

                numberPickerTimes.setWrapSelectorWheel(true);

                View btnPositive = dialog.getActionButton(DialogAction.POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txtSmartNotification.setText("Sound at must " + numberPickerTimes.getValue() + " times within " + numberPickerMinutes.getValue() + " minutes");
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                switch (page) {
                                    case "GROUP": {
                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                        realmGroupRoom.getRealmNotificationSetting().setMinutes(numberPickerMinutes.getValue());
                                        realmGroupRoom.getRealmNotificationSetting().setTimes(numberPickerTimes.getValue());
                                        break;
                                    }
                                    case "CHANNEL": {
                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                        realmChannelRoom.getRealmNotificationSetting().setMinutes(numberPickerMinutes.getValue());
                                        realmChannelRoom.getRealmNotificationSetting().setTimes(numberPickerTimes.getValue());

                                        break;
                                    }
                                    case "CONTACT": {
                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                        realmChatRoom.getRealmNotificationSetting().setMinutes(numberPickerMinutes.getValue());
                                        realmChatRoom.getRealmNotificationSetting().setTimes(numberPickerTimes.getValue());

                                        break;
                                    }
                                }
                            }
                        });
                        realm.close();

                        dialog.dismiss();
                        Log.i("VVVV", "onClick: " + numberPickerTimes.getValue());
                        Log.i("VVVV", "onClick: " + numberPickerMinutes.getValue());

                    }
                });
                dialog.show();

            }
        });


        //=======================================================================led color

        GradientDrawable bgShape = (GradientDrawable) imgLED.getBackground();
        bgShape.setColor(realmLedColor);
        ltLedColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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


                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                                switch (page) {
                                    case "GROUP": {
                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                        realmGroupRoom.getRealmNotificationSetting().setLedColor(picker.getColor());
                                        break;
                                    }
                                    case "CHANNEL": {
                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                        realmChannelRoom.getRealmNotificationSetting().setLedColor(picker.getColor());
                                        break;
                                    }
                                    case "CONTACT": {
                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                        realmChatRoom.getRealmNotificationSetting().setLedColor(picker.getColor());
                                        break;
                                    }
                                }
                            }
                        });
                        realm.close();
                    }
                });

                dialog.show();
            }
        });
    }

    private void setRealm(Realm realm, final RealmGroupRoom realmGroupRoom, final RealmChannelRoom realmChannelRoom, final RealmChatRoom realmChatRoom) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmNotificationSetting = realm.createObject(RealmNotificationSetting.class);

                realmNotificationSetting.setNotification("default");
                realmNotificationSetting.setVibrate("Disable");
                realmNotificationSetting.setSound("iGap");
                realmNotificationSetting.setIdRadioButtonSound(0);
                realmNotificationSetting.setSmartNotification("default");
                realmNotificationSetting.setTimes(0);
                realmNotificationSetting.setMinutes(0);
                realmNotificationSetting.setSmartNotification("default");
                realmNotificationSetting.setLedColor(-8257792);

                if (realmGroupRoom != null)
                    realmGroupRoom.setRealmNotificationSetting(realmNotificationSetting);

                if (realmChannelRoom != null)
                    realmChannelRoom.setRealmNotificationSetting(realmNotificationSetting);

                if (realmChatRoom != null)
                    realmChatRoom.setRealmNotificationSetting(realmNotificationSetting);
            }
        });

    }

    private void getRealm() {

        realmNotification = realmNotificationSetting.getNotification();
        realmVibrate = realmNotificationSetting.getVibrate();
        realmSound = realmNotificationSetting.getSound();
        realmIdSound = realmNotificationSetting.getIdRadioButtonSound();
        realmSmartNotification = realmNotificationSetting.getSmartNotification();
        realmTimes = realmNotificationSetting.getTimes();
        realmMinutes = realmNotificationSetting.getMinutes();
        realmLedColor = realmNotificationSetting.getLedColor();
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
