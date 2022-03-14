package net.iGap.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.SHP_SETTING;

import java.util.Objects;

public class HelperBiometricAuthentication {

    public static final int BIOMETRIC_ENRROL_REQUEST_CODE = 100;
    public static SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);

    public static void canAuthenticate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricManager biometricManager = BiometricManager.from(G.context);
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL) != BiometricManager.BIOMETRIC_SUCCESS) {
                sharedPreferences.edit().putBoolean(SHP_SETTING.IS_ACTIVE_PHONE_BIOMETRIC_SECURITY, false).apply();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isHardwareSupported(G.context) || !isFingerprintAvailable(G.context)) {
                sharedPreferences.edit().putBoolean(SHP_SETTING.IS_ACTIVE_PHONE_BIOMETRIC_SECURITY, false).apply();
            }
        }
    }

    /**for check finger print sensor hardware existing in android 10 and lower*/
    public static boolean isHardwareSupported(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }

    /**for check finger print enrollment in android 10 and lower*/
    public static boolean isFingerprintAvailable(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.hasEnrolledFingerprints();
    }

    public static void showEnrollBiometricPasswordDialog(Fragment fragment){
        MaterialDialog materialDialog = new MaterialDialog.Builder(fragment.requireContext())
                .content(R.string.no_biometric_password_set_on_your_phone)
                .contentGravity(GravityEnum.START)
                .contentColorAttr(R.attr.iGapTitleTextColor)
                .positiveText(R.string.set_now)
                .positiveColorAttr(R.attr.colorAccent)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        /**The result of this intent is checked in onActivityResult of
                         * input fragment (FragmentPassCode)*/
                        // Prompts the user to create credentials that your app accepts.
                        final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                        fragment.startActivityForResult(enrollIntent, BIOMETRIC_ENRROL_REQUEST_CODE);
                    }
                })
                .negativeText(R.string.cancel)
                .negativeColorAttr(R.attr.iGapSubtitleTextColor)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();
        materialDialog.show();
    }

}
