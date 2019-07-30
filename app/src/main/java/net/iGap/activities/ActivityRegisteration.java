package net.iGap.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import net.iGap.G;
import net.iGap.R;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.FragmentIntroduce;
import net.iGap.fragments.FragmentRegistrationNickname;
import net.iGap.fragments.WelcomeFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPermission;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.module.StartupActions;

import java.io.IOException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ActivityRegisteration extends ActivityEnhanced {

    public static final String showProfile = "showProfile";

    private static final String KEY_SAVE_CODE_NUMBER = "SAVE_CODE_NUMBER";
    private static final String KEY_SAVE_PHONE_NUMBER_MASK = "SAVE_PHONE_NUMBER_MASK";
    private static final String KEY_SAVE_PHONE_NUMBER_NUMBER = "SAVE_PHONE_NUMBER_NUMBER";
    private static final String KEY_SAVE_NAME_COUNTRY = "SAVE_NAME_COUNTRY";
    private static final String KEY_SAVE_REGEX = "KEY_SAVE_REGEX";
    private static final String KEY_SAVE_AGREEMENT = "KEY_SAVE_REGISTER";

    private RegisterRepository repository;

    // FrameLayout layoutRoot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        isOnGetPermission = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        try {
            HelperPermission.getStoragePermision(this, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {
                    startApp();
                }

                @Override
                public void deny() {
                    //finish();
                    startApp();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        repository.goToMainPage.observe(this, data -> {
            if (data != null) {
                if (data.isShowDialogDisableTwoStepVerification()) {
                    new DefaultRoundDialog(this)
                            .setTitle(R.string.warning)
                            .setMessage(R.string.two_step_verification_disable)
                            .setPositiveButton(R.string.dialog_ok, (dialog, which) -> goToMainPage(data.getUserId()))
                            .show();
                } else {
                    goToMainPage(data.getUserId());
                }
            }
        });

        repository.goToWelcomePage.observe(this, userId -> {
            if (userId != null) {
                WelcomeFragment fragment = new WelcomeFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("newUser", true);
                bundle.putLong("userId", userId);
                fragment.setArguments(bundle);
                new HelperFragment(getSupportFragmentManager(), fragment).setResourceContainer(R.id.ar_layout_root).setReplace(true).load(false);
            }
        });

        if (savedInstanceState != null) {
            repository.saveInstance(
                    savedInstanceState.getInt(KEY_SAVE_CODE_NUMBER),
                    savedInstanceState.getString(KEY_SAVE_PHONE_NUMBER_MASK),
                    savedInstanceState.getString(KEY_SAVE_PHONE_NUMBER_NUMBER),
                    savedInstanceState.getString(KEY_SAVE_NAME_COUNTRY),
                    savedInstanceState.getString(KEY_SAVE_REGEX)
            );
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(KEY_SAVE_CODE_NUMBER, repository.getCallingCode());
        savedInstanceState.putString(KEY_SAVE_PHONE_NUMBER_MASK, repository.getPattern());
        savedInstanceState.putString(KEY_SAVE_PHONE_NUMBER_NUMBER, repository.getPhoneNumber());
        savedInstanceState.putString(KEY_SAVE_NAME_COUNTRY, repository.getCountryName());
        savedInstanceState.putString(KEY_SAVE_REGEX, repository.getRegex());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void startApp() {
        StartupActions.makeFolder();

        boolean showPro = false;
        try {
            if (getIntent() != null && getIntent().getExtras() != null) {
                showPro = getIntent().getExtras().getBoolean(showProfile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            G.isLandscape = true;
        } else {
            G.isLandscape = false;
        }

        //  layoutRoot = (FrameLayout) findViewById(R.id.ar_layout_root);

        if (showPro) {
            loadFragmentProfile();
        } else {
            loadFragmentIntroduce();
        }
    }

    private void loadFragmentProfile() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                FragmentRegistrationNickname fragment = new FragmentRegistrationNickname();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.ar_layout_root, fragment)
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left)
                        .commitAllowingStateLoss();
            }
        });

    }

    private void loadFragmentIntroduce() {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!ActivityRegisteration.this.isFinishing()) {

                    try {
                        FragmentIntroduce fragment = new FragmentIntroduce();
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.ar_layout_root, fragment)
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left)
                                .commit();
                    } catch (Exception e) {
                        HelperLog.setErrorLog(e);
                    }
                }
            }
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void goToMainPage(long userId) {
        Intent intent = new Intent(this, ActivityMain.class);
        intent.putExtra(FragmentRegistrationNickname.ARG_USER_ID, userId);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
