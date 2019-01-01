package net.iGap.module.audioManagement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import net.iGap.G;
import net.iGap.R;

public class BluethoothIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            //Device found
        } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            //Device is now connected
            G.isBluetoothConnected = true;

            if (G.speakerControlListener != null) {
                G.speakerControlListener.setOnChangeSpeaker(R.string.md_igap_bluetooth);
            }
            audioManager.setSpeakerphoneOn(false);

        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //Done searching
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            //Device is about to disconnect
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //Device has disconnected
            G.isBluetoothConnected = false;
            if (G.speakerControlListener != null) {
                G.speakerControlListener.setOnChangeSpeaker(R.string.md_unMuted);
            }
            audioManager.setSpeakerphoneOn(true);

        }
/*        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    G.isBluetoothConnected = true;

                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    G.isBluetoothConnected = false;
                } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    G.isBluetoothConnected = false;
                } else {
                    G.isBluetoothConnected = false;
                }

            }

            // Bluetooth is disconnected, do handling here
        }*/
    }
}
