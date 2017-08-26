package net.iGap.fragments;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLogout;
import net.iGap.interfaces.FingerPrint;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.FingerprintHandler;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserSessionLogout;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEnterPassCode extends Fragment {

    private FragmentActivity mActivity;
    private Realm realm;
    private String password;
    private boolean isFingerPrint;

    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidHive";
    private Cipher cipher;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private int kindPassCode;
    private final int PIN = 0;
    private final int PASSWORD = 1;
    private MaterialDialog dialog;
    private TextView iconFingerPrint;
    private TextView textFingerPrint;
    private RealmUserInfo realmUserInfo;

    public FragmentEnterPassCode() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_pass_code, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) mActivity.getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) mActivity.getSystemService(KEYGUARD_SERVICE);
        }

        ViewGroup rootEnterPassword = (ViewGroup) view.findViewById(R.id.mainRootEnterPassword);
        RippleView txtOk = (RippleView) view.findViewById(R.id.enterPassword_rippleOk);
        final EditText edtPassword = (EditText) view.findViewById(R.id.enterPassword_edtSetPassword);

        ActivityMain.lockNavigation();
        rootEnterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        realm = Realm.getDefaultInstance();

        realmUserInfo = realm.where(RealmUserInfo.class).findFirst();

        if (realmUserInfo != null) {
            password = realmUserInfo.getPassCode();
            isFingerPrint = realmUserInfo.isFingerPrint();
            kindPassCode = realmUserInfo.getKindPassCode();
        }

        if (kindPassCode == PIN) {
            edtPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        if (isFingerPrint) {

            dialog = new MaterialDialog.Builder(mActivity).title(getString(R.string.FingerPrint)).customView(R.layout.dialog_finger_print, true).negativeText(getResources().getString(R.string.B_cancel)).build();

            View viewDialog = dialog.getView();

            iconFingerPrint = (TextView) viewDialog.findViewById(R.id.iconDialogTitle);
            textFingerPrint = (TextView) viewDialog.findViewById(R.id.txtDialogTitle);

            dialog.show();

            generateKey();
            if (cipherInit()) {
                FingerprintManager.CryptoObject cryptoObject = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                }
                FingerprintHandler helper = new FingerprintHandler(mActivity);
                helper.startAuth(fingerprintManager, cryptoObject);

            }

            G.fingerPrint = new FingerPrint() {
                @Override
                public void success() {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (dialog != null) dialog.dismiss();
                            //  mActivity.getSupportFragmentManager().popBackStack();

                            HelperFragment.removeFreagment(FragmentEnterPassCode.this);

                        }
                    });
                }

                @Override
                public void error() {
                    if (dialog != null) {
                        if (dialog.isShowing()) {
                            if (iconFingerPrint != null && textFingerPrint != null) {
                                iconFingerPrint.setTextColor(getResources().getColor(R.color.red));
                                textFingerPrint.setTextColor(getResources().getColor(R.color.red));
                                textFingerPrint.setText(getResources().getString(R.string.Fingerprint_not_recognized));
                            }
                        }
                    }
                }
            };
        }

        TextView txtForgotPassword = (TextView) view.findViewById(R.id.setPassword_forgotPassword);
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new MaterialDialog.Builder(mActivity).title(R.string.forgot_password).content(R.string.forgot_password).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        logout(v);


                    }
                }).negativeText(R.string.cancel).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                }).show();
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterPassword = edtPassword.getText().toString();
                if (enterPassword.length() > 0) {

                    if (enterPassword.equals(password)) {
                        //  mActivity.getSupportFragmentManager().popBackStack();

                        HelperFragment.removeFreagment(FragmentEnterPassCode.this);

                        ActivityMain.openNavigation();
                        //G.isPassCode = false;
                        closeKeyboard(v);
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.invalid_password));
                    }
                } else {
                    closeKeyboard(v);
                    error(getString(R.string.enter_a_password));
                }
            }
        });
    }

    private void logout(final View v) {
        G.onUserSessionLogout = new OnUserSessionLogout() {
            @Override
            public void onUserSessionLogout() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HelperLogout.logout();
                    }
                });
            }

            @Override
            public void onError() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack = Snackbar.make(v.findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack = Snackbar.make(v.findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            }
        };

        new RequestUserSessionLogout().userSessionLogout();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {

            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        getActivity().finish();
                        System.exit(0);
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
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
    public boolean cipherInit() {
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
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private void closeKeyboard(View v) {
        if (isAdded()) {
            try {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    private void error(String error) {
        if (isAdded()) {
            try {
                Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                vShort.vibrate(200);
                final Snackbar snack = Snackbar.make(mActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }
}
