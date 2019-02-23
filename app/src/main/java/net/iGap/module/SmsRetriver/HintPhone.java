package net.iGap.module.SmsRetriver;

import android.app.PendingIntent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class HintPhone {

    private final String TAG = HintPhone.class.getSimpleName();
    private static final int RC_HINT = 1000;

    private void showHint(AppCompatActivity context) {

        GoogleApiClient mCredentialsApiClient =new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "Connected");
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "GoogleApiClient is suspended with cause code: " + cause);
                    }
                })
                .enableAutoManage(context, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "GoogleApiClient failed to connect: " + connectionResult);
                    }
                })
                .addApi(Auth.CREDENTIALS_API)
                .build();


        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient, hintRequest);
        try {
            context.startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Could not start hint picker Intent", e);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_HINT) {
//            if (resultCode == RESULT_OK) {
//                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
//                Log.e(TAG, "cred.getId()"+cred.getId());
//
//            } else {
//                Log.e(TAG, "focusPhoneNumber   select ");
//
//            }
//        }
//    }

}