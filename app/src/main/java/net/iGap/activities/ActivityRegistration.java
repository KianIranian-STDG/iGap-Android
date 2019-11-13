package net.iGap.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.AccountManager;
import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.FragmentActivation;
import net.iGap.fragments.FragmentIntroduce;
import net.iGap.fragments.FragmentMain;
import net.iGap.fragments.FragmentRegister;
import net.iGap.fragments.FragmentRegistrationNickname;
import net.iGap.fragments.WelcomeFragment;
import net.iGap.helper.PermissionHelper;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.viewmodel.RegistrationViewModel;

import org.paygear.RaadApp;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static org.paygear.utils.Utils.signOutWallet;

public class ActivityRegistration extends ActivityEnhanced {

    public static final String showProfile = "showProfile";

    private RegistrationViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        isOnGetPermission = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                boolean showPro = false;
                boolean isAddAccount = false;
                if (getIntent() != null && getIntent().getExtras() != null) {
                    showPro = getIntent().getExtras().getBoolean(showProfile);
                    isAddAccount = getIntent().getBooleanExtra("add account", false);
                }
                return (T) new RegistrationViewModel(showPro, isAddAccount);
            }
        }).get(RegistrationViewModel.class);

        viewModel.goToMainPage().observe(this, data -> {
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

        viewModel.getExistUser().observe(this, isExist -> {
            if (isExist != null && isExist) {
                new DefaultRoundDialog(this)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.login_exist_account_error)
                        .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {

                        })
                        .show();
            }
        });

        viewModel.goToWelcomePage().observe(this, userId -> {
            if (userId != null) {
                WelcomeFragment fragment = new WelcomeFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("newUser", true);
                bundle.putLong("userId", userId);
                fragment.setArguments(bundle);
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().popBackStack();
                loadFragment(fragment, true);
            }
        });

        viewModel.getGrantPermission().observe(this, isGrantPermission -> {
            if (isGrantPermission != null && isGrantPermission) {
                if (new PermissionHelper(ActivityRegistration.this).grantReadAndRightStoragePermission()) {
                    viewModel.startApp(getSupportFragmentManager().getBackStackEntryCount());
                }
            }
        });

        viewModel.getGoToIntroduction().observe(this, isGo -> loadFragment(new FragmentIntroduce(), true));

        viewModel.getGoToNicknamePage().observe(this, isGo -> loadFragment(new FragmentRegistrationNickname(), true));

        viewModel.getGoToRegisterPage().observe(this, isGo -> loadFragment(new FragmentRegister(), true));

        viewModel.getLoadFromBackStack().observe(this, isLoad -> {
            if (isLoad != null && isLoad) {
                loadFragment(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()), false);
            }
        });

        G.isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.wtf(this.getClass().getName(), "-----------------------------------------");
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    Log.wtf(this.getClass().getName(), "fragment: " + getSupportFragmentManager().getBackStackEntryAt(i).getName());
                }
                Log.wtf(this.getClass().getName(), "-----------------------------------------");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.StoragePermissionRequestCode) {
            Log.wtf(this.getClass().getName(), "onRequestPermissionsResult");
            viewModel.startApp(getSupportFragmentManager().getBackStackEntryCount());
        }
    }

    @Override
    public void onBackPressed() {
        Log.wtf(this.getClass().getName(), "onBackPressed 0");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.registrationFrame);
        if (!(fragment instanceof FragmentActivation) && !(fragment instanceof FragmentRegistrationNickname)) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                Log.wtf(this.getClass().getName(), "onBackPressed");
                super.onBackPressed();
            } else {
                if (getIntent().getBooleanExtra("add account", false)) {
                    WebSocketClient.getInstance().disconnectSocket(false);
                    DbManager.getInstance().closeUiRealm();
                    signOutWallet();
                    AccountManager.getInstance().setCurrentUser();
                    RaadApp.onCreate(this);
                    RequestClientGetRoomList.pendingRequest.remove(0);
                    FragmentMain.mOffset = 0;
                    DbManager.getInstance().changeRealmConfiguration();
                    WebSocketClient.getInstance().connect(true);
                    Log.wtf(this.getClass().getName(), "current user: " + AccountManager.getInstance().getCurrentUser());
                    finish();
                    startActivity(new Intent(this, ActivityMain.class));
                } else {
                    Log.wtf(this.getClass().getName(), "finish");
                    finish();
                }
            }
        }
    }

    private void goToMainPage(long userId) {
        Intent intent = new Intent(this, ActivityMain.class);
        intent.putExtra(FragmentRegistrationNickname.ARG_USER_ID, userId);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.registrationFrame, fragment, fragment.getClass().getName())
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left)
                .commit();
    }
}
