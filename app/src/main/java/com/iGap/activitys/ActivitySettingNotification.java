package com.iGap.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.module.CircleImageView;
import com.iGap.module.SHP_SETTING;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

public class ActivitySettingNotification extends AppCompatActivity {

    private TextView txtBack, txtVibrateMessage, txtPopupNotification, txtVibrateGroup, txtPopupNotificationGroup, txtSoundGroup, txtSoundMessage, txtRepeat_Notifications;
    private CircleImageView imgLedColor_message;
    private ImageView imgLedMessage, imgLedColor_group;

    private int poRbDialogSoundGroup = -1;
    private int poRbDialogSoundMessage = -1;

    private ViewGroup ltAlert, ltMessagePreview,
            ltLedColorMessage, ltPopupNotification,
            ltAlert_group, ltMessagePreview_group,
            ltSoundMessage,
            ltVibrate_message, ltLedColor_group, ltVibrateGroup,
            ltPopupNotificationGroup, ltSoundGroup, ltApp_sound, ltApp_Vibrate, ltApp_preview, ltChat_sound,
            ltContact_joined, ltPinned_message,
            ltKeep_alive_service, ltBackground_connection,
            ltBadge_content, ltRepeat_Notifications, ltReset_all_notification;

    private ToggleButton tgAlert, tgMessagePreview, tgAlert_group, tgMessagePreview_group, tgApp_sound, tgApp_Vibrate, tgApp_preview, tgChat_sound, tgContact_joined, tgPinned_message, tgKeep_alive_service, tgBackground_connection, tgBadge_content;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification);

        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        txtBack = (TextView) findViewById(R.id.stns_txt_back);
        txtBack.setTypeface(G.fontawesome);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivitySettingNotification.this, ActivitySetting.class);
                startActivity(intent);
                finish();
            }
        });

        //=============================================================================Message

        tgAlert = (ToggleButton) findViewById(R.id.stns_toggle_alert);
        int alert_message = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
        if (alert_message == 1) {
            tgAlert.setChecked(true);
        } else {
            tgAlert.setChecked(false);
        }

        ltAlert = (ViewGroup) findViewById(R.id.stns_layout_alert);
        ltAlert.setOnClickListener(new View.OnClickListener() { // alert 1
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgAlert.isChecked()) {
                    tgAlert.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 0);
                    editor.apply();

                } else {
                    tgAlert.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
                    editor.apply();
                }
            }
        });


        tgMessagePreview = (ToggleButton) findViewById(R.id.stns_toggle_messagePreview);
        int preview_message = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
        if (preview_message == 1) {
            tgMessagePreview.setChecked(true);
        } else {
            tgMessagePreview.setChecked(false);
        }
        ltMessagePreview = (ViewGroup) findViewById(R.id.stns_layout_messagePreview);
        ltMessagePreview.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgMessagePreview.isChecked()) {
                    tgMessagePreview.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 0);
                    editor.apply();

                } else {
                    tgMessagePreview.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
                    editor.apply();
                }
            }
        });
        int ledColorMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
        imgLedMessage = (ImageView) findViewById(R.id.stns_img_ledColorMessage);
        imgLedMessage.setBackgroundColor(ledColorMessage);
        ltLedColorMessage = (ViewGroup) findViewById(R.id.stns_layout_ledColorMessage);
        ltLedColorMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySettingNotification.this)
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
                        imgLedMessage.setBackgroundColor(picker.getColor());
                        editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, picker.getColor());
                        Log.i("VVCCVV", "onClick: " + picker.getColor());
                        editor.apply();
                    }
                });


                dialog.show();
            }
        });

        txtVibrateMessage = (TextView) findViewById(R.id.stns_txt_vibrate_message_text);
        txtVibrateMessage.setTypeface(G.arial);
        String vibrateMessage = sharedPreferences.getString(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, "Default");
        txtVibrateMessage.setText(vibrateMessage);
        ltVibrate_message = (ViewGroup) findViewById(R.id.stns_layout_vibrate_message);
        ltVibrate_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySettingNotification.this)
                        .title("Vibrate")
                        .items(R.array.vibrate)
                        .negativeText("CANCEL")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                switch (which) {
                                    case 0:
                                        txtVibrateMessage.setText("Disable");
                                        editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, "Disable");
                                        editor.apply();
                                        break;
                                    case 1:
                                        txtVibrateMessage.setText("Default");
                                        editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, "Default");
                                        editor.apply();
                                        break;
                                    case 2:
                                        txtVibrateMessage.setText("Short");
                                        editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, "Short");
                                        editor.apply();
                                        break;
                                    case 3:
                                        txtVibrateMessage.setText("Long");
                                        editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, "Long");
                                        editor.apply();
                                        break;
                                    case 4:
                                        txtVibrateMessage.setText("Only if silent");
                                        editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, "Only if silent");
                                        editor.apply();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });

        txtPopupNotification = (TextView) findViewById(R.id.stns_txt_popupNotification_message_text);
        txtPopupNotification.setTypeface(G.arial);
        String popupNotification = sharedPreferences.getString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, "Default");
        txtPopupNotification.setText(popupNotification);
        ltPopupNotification = (ViewGroup) findViewById(R.id.stns_layout_popupNotification_message);
        ltPopupNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySettingNotification.this)
                        .title("Vibrate")
                        .items(R.array.popup_Notification)
                        .negativeText("CANCEL")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                switch (which) {
                                    case 0:
                                        txtPopupNotification.setText("No popup");
                                        editor.putString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, "No popup");
                                        editor.apply();
                                        break;
                                    case 1:
                                        txtPopupNotification.setText("Only when screen \"on\"");
                                        editor.putString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, "Only when screen \"on\"");
                                        editor.apply();
                                        break;
                                    case 2:
                                        txtPopupNotification.setText("Only when screen \"off\"");
                                        editor.putString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, "Only when screen \"off\"");
                                        editor.apply();
                                        break;
                                    case 3:
                                        txtPopupNotification.setText("Always show popup");
                                        editor.putString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, "Always show popup");
                                        editor.apply();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });

        poRbDialogSoundMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 3);
        txtSoundMessage = (TextView) findViewById(R.id.stns_txt_sound_text);
        txtSoundMessage.setTypeface(G.arial);
        String soundMessage = sharedPreferences.getString(SHP_SETTING.KEY_STNS_SOUND_MESSAGE, "Arrow");
        txtSoundMessage.setText(soundMessage);
        ltSoundMessage = (ViewGroup) findViewById(R.id.stns_layout_sound_message);
        ltSoundMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySettingNotification.this)
                        .title("Ringtone")
                        .titleGravity(GravityEnum.START)
                        .titleColor(getResources().getColor(android.R.color.black))
                        .items(R.array.sound_message)
                        .itemsCallbackSingleChoice(poRbDialogSoundMessage, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                txtSoundMessage.setText(text.toString());
                                poRbDialogSoundMessage = which;
                                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(SHP_SETTING.KEY_STNS_SOUND_MESSAGE, text.toString());
                                editor.putInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, which);
                                editor.apply();
                                return false;
                            }
                        })
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .show();
                Toast.makeText(ActivitySettingNotification.this, "ltSoundMessage", Toast.LENGTH_SHORT).show();
            }
        });


        //=============================================================================Group

        tgAlert_group = (ToggleButton) findViewById(R.id.stns_toggle_alert_group);
        int alert_group = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1);
        if (alert_group == 1) {
            tgAlert_group.setChecked(true);
        } else {
            tgAlert_group.setChecked(false);
        }
        ltAlert_group = (ViewGroup) findViewById(R.id.stns_layout_alert_group);
        ltAlert_group.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgAlert_group.isChecked()) {
                    tgAlert_group.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 0);
                    editor.apply();

                } else {
                    tgAlert_group.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1);
                    editor.apply();
                }
            }
        });


        tgMessagePreview_group = (ToggleButton) findViewById(R.id.stns_toggle_messagePreview_group);
        tgMessagePreview_group.setTypeface(G.arial);
        int preview_group = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
        if (preview_group == 1) {
            tgMessagePreview_group.setChecked(true);
        } else {
            tgMessagePreview_group.setChecked(false);
        }
        ltMessagePreview_group = (ViewGroup) findViewById(R.id.stns_layout_messagePreview_group);
        ltMessagePreview_group.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgMessagePreview_group.isChecked()) {
                    tgMessagePreview_group.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 0);
                    editor.apply();

                } else {
                    tgMessagePreview_group.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);
                    editor.apply();
                }
            }
        });

        int ledColorGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, 0);
        imgLedColor_group = (ImageView) findViewById(R.id.stns_img_ledColor_group);
        imgLedColor_group.setBackgroundColor(ledColorGroup);
        ltLedColor_group = (ViewGroup) findViewById(R.id.stns_layout_ledColor_group);
        ltLedColor_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPreferences.edit();

                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySettingNotification.this)
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
                        imgLedColor_group.setBackgroundColor(picker.getColor());
                        editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, picker.getColor());
                        editor.apply();
                    }
                });


                dialog.show();
            }
        });


        txtVibrateGroup = (TextView) findViewById(R.id.stns_txt_vibrate_group_text);
        txtVibrateGroup.setTypeface(G.arial);
        String vibrateGroup = sharedPreferences.getString(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, "Default");
        txtVibrateGroup.setText(vibrateGroup);
        ltVibrateGroup = (ViewGroup) findViewById(R.id.stns_layout_vibrate_group);
        ltVibrateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySettingNotification.this)
                        .title("Vibrate")
                        .items(R.array.vibrate)
                        .negativeText("CANCEL")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                switch (which) {
                                    case 0:
                                        txtVibrateGroup.setText("Disable");
                                        editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, "Disable");
                                        editor.apply();
                                        break;
                                    case 1:
                                        txtVibrateGroup.setText("Default");
                                        editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, "Default");
                                        editor.apply();
                                        break;
                                    case 2:
                                        txtVibrateGroup.setText("Short");
                                        editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, "Short");
                                        editor.apply();
                                        break;
                                    case 3:
                                        txtVibrateGroup.setText("Long");
                                        editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, "Long");
                                        editor.apply();
                                        break;
                                    case 4:
                                        txtVibrateGroup.setText("Only if silent");
                                        editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, "Only if silent");
                                        editor.apply();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });

        txtPopupNotificationGroup = (TextView) findViewById(R.id.stns_txt_popupNotification_group_text);
        txtPopupNotificationGroup.setTypeface(G.arial);
        String popupNotificationGroup = sharedPreferences.getString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, "Default1");
        txtPopupNotificationGroup.setText(popupNotificationGroup);
        ltPopupNotificationGroup = (ViewGroup) findViewById(R.id.stns_layout_popupNotification_group);
        ltPopupNotificationGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySettingNotification.this)
                        .title("Vibrate")
                        .items(R.array.popup_Notification)
                        .negativeText("CANCEL")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                switch (which) {
                                    case 0:
                                        txtPopupNotificationGroup.setText("No popup");
                                        editor.putString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, "No popup");
                                        editor.apply();
                                        break;
                                    case 1:
                                        txtPopupNotificationGroup.setText("Only when screen \"on\"");
                                        editor.putString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, "Only when screen \"on\"");
                                        editor.apply();
                                        break;
                                    case 2:
                                        txtPopupNotificationGroup.setText("Only when screen \"off\"");
                                        editor.putString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, "Only when screen \"off\"");
                                        editor.apply();
                                        break;
                                    case 3:
                                        txtPopupNotificationGroup.setText("Always show popup");
                                        editor.putString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, "Always show popup");
                                        editor.apply();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });

        poRbDialogSoundGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 3);
        txtSoundGroup = (TextView) findViewById(R.id.stns_txt_sound_group_text);
        txtSoundGroup.setTypeface(G.arial);
        String soundGroup = sharedPreferences.getString(SHP_SETTING.KEY_STNS_SOUND_GROUP, "Arrow");
        txtSoundGroup.setText(soundGroup);
        ltSoundGroup = (ViewGroup) findViewById(R.id.stns_layout_sound_group);
        ltSoundGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySettingNotification.this)
                        .title("Ringtone")
                        .titleGravity(GravityEnum.START)
                        .titleColor(getResources().getColor(android.R.color.black))
                        .items(R.array.sound_message)
                        .itemsCallbackSingleChoice(poRbDialogSoundGroup, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                txtSoundGroup.setText(text.toString());
                                poRbDialogSoundGroup = which;
                                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(SHP_SETTING.KEY_STNS_SOUND_GROUP, text.toString());
                                editor.putInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, which);
                                editor.apply();
                                return false;
                            }
                        })
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .show();

                Toast.makeText(ActivitySettingNotification.this, "ltSoundMessage", Toast.LENGTH_SHORT).show();
            }
        });

        //============================================================================= In App Notification


        tgApp_sound = (ToggleButton) findViewById(R.id.stns_toggle_app_sound);
        int appSound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_SOUND, 1);
        if (appSound == 1) {
            tgApp_sound.setChecked(true);
        } else {
            tgApp_sound.setChecked(false);
        }
        ltApp_sound = (ViewGroup) findViewById(R.id.stns_layout_app_sound);
        ltApp_sound.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgApp_sound.isChecked()) {
                    tgApp_sound.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_SOUND, 0);
                    editor.apply();

                } else {
                    tgApp_sound.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_SOUND, 1);
                    editor.apply();
                }
            }
        });


        tgApp_Vibrate = (ToggleButton) findViewById(R.id.stns_toggle_app_vibrate);
        int appVibrate = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 1);
        if (appVibrate == 1) {
            tgApp_Vibrate.setChecked(true);
        } else {
            tgApp_Vibrate.setChecked(false);
        }
        ltApp_Vibrate = (ViewGroup) findViewById(R.id.stns_layout_app_vibrate);
        ltApp_Vibrate.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgApp_Vibrate.isChecked()) {
                    tgApp_Vibrate.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 0);
                    editor.apply();

                } else {
                    tgApp_Vibrate.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 1);
                    editor.apply();
                }
            }
        });

        tgApp_preview = (ToggleButton) findViewById(R.id.stns_toggle_app_preview);
        int appPreview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 1);
        if (appPreview == 1) {
            tgApp_preview.setChecked(true);
        } else {
            tgApp_preview.setChecked(false);
        }
        ltApp_preview = (ViewGroup) findViewById(R.id.stns_layout_app_preview);
        ltApp_preview.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgApp_preview.isChecked()) {
                    tgApp_preview.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 0);
                    editor.apply();

                } else {
                    tgApp_preview.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 1);
                    editor.apply();
                }
            }
        });

        tgChat_sound = (ToggleButton) findViewById(R.id.stns_toggle_chat_sound);
        int chat_cound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 1);
        if (chat_cound == 1) {
            tgChat_sound.setChecked(true);
        } else {
            tgChat_sound.setChecked(false);
        }
        ltChat_sound = (ViewGroup) findViewById(R.id.stns_layout_chat_sound);
        ltChat_sound.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgChat_sound.isChecked()) {
                    tgChat_sound.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 0);
                    editor.apply();

                } else {
                    tgChat_sound.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 1);
                    editor.apply();
                }
            }
        });


        //============================================================================= In chat sound

        tgContact_joined = (ToggleButton) findViewById(R.id.stns_toggle_Contact_joined);
        int contact_joined = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 1);
        if (contact_joined == 1) {
            tgContact_joined.setChecked(true);
        } else {
            tgContact_joined.setChecked(false);
        }
        ltContact_joined = (ViewGroup) findViewById(R.id.stns_layout_Contact_joined);
        ltContact_joined.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgContact_joined.isChecked()) {
                    tgContact_joined.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 0);
                    editor.apply();

                } else {
                    tgContact_joined.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 1);
                    editor.apply();
                }
            }
        });


        tgPinned_message = (ToggleButton) findViewById(R.id.stns_toggle_pinned_message);
        int pinnedMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 1);
        if (pinnedMessage == 1) {
            tgPinned_message.setChecked(true);
        } else {
            tgPinned_message.setChecked(false);
        }
        ltPinned_message = (ViewGroup) findViewById(R.id.stns_layout_pinned_message);
        ltPinned_message.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgPinned_message.isChecked()) {
                    tgPinned_message.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 0);
                    editor.apply();

                } else {
                    tgPinned_message.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 1);
                    editor.apply();
                }
            }
        });

        //============================================================================= Other

        tgKeep_alive_service = (ToggleButton) findViewById(R.id.stns_toggle_keep_alive_service);
        int keep_alive_service = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
        if (keep_alive_service == 1) {
            tgKeep_alive_service.setChecked(true);
        } else {
            tgKeep_alive_service.setChecked(false);
        }
        ltKeep_alive_service = (ViewGroup) findViewById(R.id.stns_layout_keep_alive_service);
        ltKeep_alive_service.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgKeep_alive_service.isChecked()) {
                    tgKeep_alive_service.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 0);
                    editor.apply();

                } else {
                    tgKeep_alive_service.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
                    editor.apply();
                }
            }
        });

        tgBackground_connection = (ToggleButton) findViewById(R.id.stns_toggle_background_connection);
        int background_connection = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 1);
        if (background_connection == 1) {
            tgBackground_connection.setChecked(true);
        } else {
            tgBackground_connection.setChecked(false);
        }
        ltBackground_connection = (ViewGroup) findViewById(R.id.stns_layout_background_connection);
        ltBackground_connection.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgBackground_connection.isChecked()) {
                    tgBackground_connection.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 0);
                    editor.apply();

                } else {
                    tgBackground_connection.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 1);
                    editor.apply();
                }
            }
        });
        tgBadge_content = (ToggleButton) findViewById(R.id.stns_badge_counter);
        int badge_content = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 1);
        if (badge_content == 1) {
            tgBadge_content.setChecked(true);
        } else {
            tgBadge_content.setChecked(false);
        }
        ltBadge_content = (ViewGroup) findViewById(R.id.stns_layout_badge_countent);
        ltBadge_content.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgBadge_content.isChecked()) {
                    tgBadge_content.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 0);
                    editor.apply();

                } else {
                    tgBadge_content.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 1);
                    editor.apply();
                }
            }
        });

        txtRepeat_Notifications = (TextView) findViewById(R.id.st_txt_Repeat_Notifications);
        txtRepeat_Notifications.setTypeface(G.arial);
        String repeat_Notifications = sharedPreferences.getString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, "Off");
        txtRepeat_Notifications.setText(repeat_Notifications);
        ltRepeat_Notifications = (ViewGroup) findViewById(R.id.st_layout_Repeat_Notifications);
        ltRepeat_Notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySettingNotification.this)
                        .title("Vibrate")
                        .items(R.array.repeat_notification)
                        .negativeText("CANCEL")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                switch (which) {
                                    case 0:
                                        txtRepeat_Notifications.setText("Off");
                                        editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, "Off");
                                        editor.apply();
                                        break;
                                    case 1:
                                        txtRepeat_Notifications.setText("5 minutes");
                                        editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, "5 minutes");
                                        editor.apply();
                                        break;
                                    case 2:
                                        txtRepeat_Notifications.setText("10 minutes");
                                        editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, "10 minutes");
                                        editor.apply();
                                        break;
                                    case 3:
                                        txtRepeat_Notifications.setText("30 minutes");
                                        editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, "30 minutes");
                                        editor.apply();
                                        break;
                                    case 4:
                                        txtRepeat_Notifications.setText("1 hour");
                                        editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, "1 hour");
                                        editor.apply();
                                        break;
                                    case 5:
                                        txtRepeat_Notifications.setText("2 hour");
                                        editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, "2 hour");
                                        editor.apply();
                                        break;
                                    case 6:
                                        txtRepeat_Notifications.setText("4 hour");
                                        editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, "4 hour");
                                        editor.apply();
                                        break;
                                }
                            }
                        }).show();
            }
        });

        //============================================================================= reset

        ltReset_all_notification = (ViewGroup) findViewById(R.id.st_layout_reset_all_notification);
        ltReset_all_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
                editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
                editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, "Default");
                editor.putString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, "Always show popup");
                editor.putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1);
                editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);
                editor.putString(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, "Default");
                editor.putString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, "Always show popup");
                editor.putInt(SHP_SETTING.KEY_STNS_APP_SOUND, 1);
                editor.putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 1);
                editor.putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 1);
                editor.putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 1);
                editor.putInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 1);
                editor.putInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 1);
                editor.putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
                editor.putInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 1);
                editor.putInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 1);
                editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, "1 hour");
                editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
                editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
                editor.apply();
                Toast.makeText(ActivitySettingNotification.this, "Reset All Notification", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ActivitySettingNotification.this, ActivitySettingNotification.class));
                finish();
            }
        });
    }
}
