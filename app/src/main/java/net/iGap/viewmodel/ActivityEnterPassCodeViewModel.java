package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.helper.HelperBiometricAuthentication;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperPreferences;
import net.iGap.model.PassCode;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.realm.RealmUserInfo;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class ActivityEnterPassCodeViewModel extends ViewModel {

    private ObservableInt isPattern = new ObservableInt(View.GONE);
    private ObservableInt isEditText = new ObservableInt(View.GONE);
    private ObservableInt passwordMaxLength = new ObservableInt(4);
    private ObservableInt passwordInputType = new ObservableInt(TYPE_TEXT_VARIATION_PASSWORD);
    private ObservableInt showCheckPasswordButton = new ObservableInt(View.GONE);
    private ObservableInt showFingerPrintIcon = new ObservableInt(View.GONE);
    private ObservableInt showBiometricPasswordIcon = new ObservableInt(View.GONE);
    private MutableLiveData<Boolean> initialPatternView = new MutableLiveData<>();
    private MutableLiveData<FingerprintManager.CryptoObject> showDialogFingerPrint = new MutableLiveData<>();
    private MutableLiveData<Boolean> showDialogForgetPassword = new MutableLiveData<>();
    private MutableLiveData<Boolean> hideKeyword = new MutableLiveData<>();
    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearPassword = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> goToRegisterPage = new SingleLiveEvent<>();

    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidHive";
    private final int PIN = 0;

    private KeyStore keyStore;
    private Cipher cipher;

    private Activity activity;
    private RealmUserInfo realmUserInfo;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    SharedPreferences sharedPreferences;

    public ActivityEnterPassCodeViewModel(Activity activity, boolean isLinePattern) {
        this.activity = activity;
        initialPatternView.setValue(!isLinePattern);
        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        HelperBiometricAuthentication.canAuthenticate();
        CheckAndSetFingerAndBiometricPasswordIcon();

        realmUserInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class).findFirst();
        });


        if (realmUserInfo != null) {
            isPattern.set(PassCode.getInstance().isPattern() ? View.VISIBLE : View.GONE);
            if (PassCode.getInstance().isPassCode()) {
                if (PassCode.getInstance().isPattern()) {
                    isEditText.set(View.GONE);
                    showCheckPasswordButton.set(View.GONE);
                    isPattern.set(View.VISIBLE);
                } else {
                    isEditText.set(View.VISIBLE);
                    showCheckPasswordButton.set(View.VISIBLE);
                    isPattern.set(View.GONE);
                    if (PassCode.getInstance().getKindPassCode() == PIN) {
                        passwordInputType.set((InputType.TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_PASSWORD));
                        passwordMaxLength.set(4);
                    } else {
                        passwordInputType.set(InputType.TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                        passwordMaxLength.set(20);
                    }
                }
            }
        } else {
            goBack.setValue(true);
        }
    }

    public ObservableInt getShowFingerPrintIcon() {
        return showFingerPrintIcon;
    }

    public ObservableInt getShowBiometricPasswordIcon() {
        return showBiometricPasswordIcon;
    }

    public void setShowBiometricPasswordIcon(ObservableInt showBiometricPasswordIcon) {
        this.showBiometricPasswordIcon = showBiometricPasswordIcon;
    }

    public ObservableInt getIsPattern() {
        return isPattern;
    }

    public ObservableInt getIsEditText() {
        return isEditText;
    }

    public ObservableInt getPasswordMaxLength() {
        return passwordMaxLength;
    }

    public ObservableInt getPasswordInputType() {
        return passwordInputType;
    }

    public ObservableInt getShowCheckPasswordButton() {
        return showCheckPasswordButton;
    }

    public MutableLiveData<Boolean> getInitialPatternView() {
        return initialPatternView;
    }

    public MutableLiveData<FingerprintManager.CryptoObject> getShowDialogFingerPrint() {
        return showDialogFingerPrint;
    }

    public MutableLiveData<Boolean> getShowDialogForgetPassword() {
        return showDialogForgetPassword;
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public MutableLiveData<Boolean> getHideKeyword() {
        return hideKeyword;
    }

    public MutableLiveData<Boolean> getClearPassword() {
        return clearPassword;
    }

    public SingleLiveEvent<Boolean> getGoToRegisterPage() {
        return goToRegisterPage;
    }

    public void afterTextChanged(String s) {
        if (PassCode.getInstance().getKindPassCode() == PIN) {
            if (s.length() == 4) {
                onCheckPasswordButtonClick(s);
            }
        }
    }

    public void onCheckPasswordButtonClick(String password) {
        if (password != null && password.length() > 0) {
            if (password.equals(PassCode.getInstance().getPassCode())) {
                passwordCorrect();
            } else {
                hideKeyword.setValue(true);
                showErrorMessage.setValue(R.string.invalid_password);
                clearPassword.setValue(true);
                G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_INPUT_PASS_CODE_INCORRECT));
            }
        } else {
            hideKeyword.setValue(true);
            showErrorMessage.setValue(R.string.enter_a_password);
            clearPassword.setValue(true);
        }

    }

    public void forgotPassword() {
        showDialogForgetPassword.setValue(true);
    }

    public void passwordCorrect() {
        ActivityMain.isLock = false;
        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
        hideKeyword.setValue(true);
        goBack.setValue(true);
    }

    public void forgetPassword() {
        PassCode.getInstance().setPassCode(false);
        hideKeyword.setValue(true);
        goToRegisterPage.postValue(!new HelperLogout().logoutAllUser());
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }
        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        hideKeyword.setValue(true);
    }

    public void onResume() {
        if (PassCode.getInstance().isFingerPrint()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                generateKey();
                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    showDialogFingerPrint.setValue(cryptoObject);
                }
            }
        }
    }

    public void onBiometricIconClick() {

        if (sharedPreferences.getBoolean(SHP_SETTING.IS_ACTIVE_PHONE_BIOMETRIC_SECURITY, false)) {
            executor = ContextCompat.getMainExecutor(activity);
            biometricPrompt = new BiometricPrompt((FragmentActivity) activity,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);

                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(G.context, R.string.authentication_succeeded, Toast.LENGTH_SHORT).show();
                    passwordCorrect();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(G.context, R.string.authentication_failed,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(G.context.getString(R.string.biometric_login))
                    .setSubtitle(G.context.getString(R.string.use_biometric_authentication))
//                    .setNegativeButtonText("Use account password")
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    .build();

            // Prompt appears.
            // Consider integrating with the keystore to unlock cryptographic operations,
            // if needed by your app.
            biometricPrompt.authenticate(promptInfo);
        }

    }

    private void CheckAndSetFingerAndBiometricPasswordIcon() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            showBiometricPasswordIcon.set(G.currentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE).getBoolean(SHP_SETTING.IS_ACTIVE_PHONE_BIOMETRIC_SECURITY, false) ? View.VISIBLE : View.GONE);
            showFingerPrintIcon.set(View.GONE);
        }else {
            showFingerPrintIcon.set(G.currentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE).getBoolean(SHP_SETTING.IS_ACTIVE_PHONE_BIOMETRIC_SECURITY, false) ? View.VISIBLE : View.GONE);
            showBiometricPasswordIcon.set(View.GONE);
        }

    }
}
