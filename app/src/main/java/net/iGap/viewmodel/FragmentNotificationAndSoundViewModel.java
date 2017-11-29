package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/


import android.databinding.ObservableField;
import android.view.View;
import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentNotificationAndSoundBinding;

public class FragmentNotificationAndSoundViewModel {


    public FragmentNotificationAndSoundBinding fragmentNotificationAndSoundBinding;

    public ObservableField<String> vibrateMessage = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_sound));
    public ObservableField<String> PopUpNotificationMessage = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_sound));
    public ObservableField<String> SoundMessage = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_sound));
    public ObservableField<String> vibrateGroup = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_sound));
    public ObservableField<String> popUpNotificationGroup = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_sound));
    public ObservableField<String> soundGroup = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_sound));




    public void onClickAlert(View view) {

    }


    public void onClickMessagePreView(View view) {

    }

    public void onClickLedColorMessage(View view) {

    }

    public void onClickVibrationMessage(View view) {

    }

    public void onClickPopUpNotiFicationMessage(View view) {

    }

    public void onClickSoundMessage(View view) {

    }

    public void onClickAlertGroup(View view) {

    }

    public void onClickMessagePreViewGroup(View view) {

    }

    public void onClickLedGroup(View view) {

    }

    public void onClickVibrationGroup(View view) {

    }

    public void onClickPopUpNotificationGroup(View view) {

    }

    public void onClickSoundGroup(View view) {

    }

    public void onClickInAppSound(View view) {

    }

    public void onClickInAppVibration(View view) {

    }

    public void onClickInAppPreView(View view) {

    }

    public void onClickSoundInChat(View view) {

    }

    public void onClickKeepService(View view) {

    }

    public void onClickReset(View view) {

    }

}
