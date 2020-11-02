package net.iGap.kuknos.Fragment;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.kuknos.WalletSpinnerArrayAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosPanelBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.PermissionHelper;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;
import net.iGap.kuknos.viewmodel.KuknosPanelVM;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmKuknos;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class KuknosPanelFrag extends BaseAPIViewFrag<KuknosPanelVM> {

    private FragmentKuknosPanelBinding binding;
    private Spinner walletSpinner;

    public static KuknosPanelFrag newInstance() {
        return new KuknosPanelFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosPanelVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_panel, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        isNeedResume = true;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
//                        popBackStackFragment();
                        ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosPToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        walletSpinner = binding.fragKuknosPWalletSpinner;
        walletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (viewModel.getKuknosWalletsM().getValue() != null && position != (viewModel.getKuknosWalletsM().getValue().getAssets().size()))
                    viewModel.spinnerSelect(position);
                else if (viewModel.getKuknosWalletsM().getValue() != null) {
                    walletSpinner.setSelection(viewModel.getPosition());
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment = fragmentManager.findFragmentByTag(KuknosAssetsPagerFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosAssetsPagerFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                    /*Snackbar snackbar = Snackbar.make(binding.fragKuknosPContainer, getString(R.string.kuknos_develop), Snackbar.LENGTH_SHORT);
                    snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    walletSpinner.setSelection(viewModel.getPosition());*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.fragKuknosBuyAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KuknosBalance.Balance balance = null;
                if (walletSpinner.getSelectedItem() instanceof KuknosBalance.Balance) {
                    balance = (KuknosBalance.Balance) walletSpinner.getSelectedItem();
                }
                if (balance != null && balance.getAssetType() != null) {

                    String iban = DbManager.getInstance().doRealmTask(new DbManager.RealmTaskWithReturn<String>() {

                        @Override
                        public String doTask(Realm realm) {
                            RealmKuknos realmKuknos = realm.where(RealmKuknos.class).findFirst();
                            if (realmKuknos != null && realmKuknos.getIban() != null) {
                                return realmKuknos.getIban();
                            } else {
                                return null;
                            }
                        }
                    });
                    if (iban == null) {
                        viewModel.getInFoFromServerToCheckUserProfile();
                    } else {
                        KuknosBuyAgainFrag buyAgainFrag = KuknosBuyAgainFrag.newInstance();
                        String assetCode = balance.getAssetType();
                        Bundle bundle = new Bundle();
                        bundle.putString("assetType", assetCode);
                        buyAgainFrag.setArguments(bundle);
                        new HelperFragment(getActivity().getSupportFragmentManager(), buyAgainFrag).setReplace(true).load();

                    }

                } else {
                    Toast.makeText(_mActivity, R.string.token_not_selected, Toast.LENGTH_SHORT).show();
                }
            }
        });

        onUserInfoObserver();

        onErrorObserver();

        openPage();

        onDataChanged();

        onProgress();

        onTermsDownload();
    }

    @Override
    public void onResume() {
        viewModel.initApis();
        super.onResume();
    }


    @Override
    public boolean onBackPressed() {
//        popBackStackFragment();
        ((ActivityMain) getActivity()).removeAllFragmentFromMain();
        return true;
    }

    private void onUserInfoObserver() {
        viewModel.getUserInfo().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosBuyAgain.performClick();
            } else {
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.kuknos_buy_again_dialog, null);
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .customView(dialogView, true)
                        .widgetColor(new Theme().getPrimaryColor(getContext()))
                        .build();
                dialog.show();
            }
        });
    }

    private void initialSettingBS() {
        List<String> items = new ArrayList<>();
//        items.add(getString(R.string.kuknos_setting_changePin));
        items.add(getString(R.string.kuknos_panel_Edit_and_change_account_information));
        items.add(getString(R.string.kuknos_setting_copySeedKey));
        items.add(getString(R.string.kuknos_setting_sepid));
        items.add(getString(R.string.kuknos_setting_logout));

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment().setData(items, -1, position -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = KuknosEnterPinFrag.newInstance(() -> {
                        goToShowRecovery();
                    }, false);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    break;
                case 1:
                    fragment = fragmentManager.findFragmentByTag(KuknosEnterPinFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosEnterPinFrag.newInstance(
                                () -> showDialog(1, R.string.kuknos_setting_copySKeyTitel, R.string.kuknos_setting_copySKeyMessage, R.string.kuknos_setting_copySKeyBtn),
                                false);
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    break;
                case 2:
                    HelperUrl.openWebBrowser(getContext(), "http://d.igap.net/kuknus");
//                    viewModel.getTermsAndCond();
                    break;
                case 3:
                    fragment = fragmentManager.findFragmentByTag(KuknosLogoutFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosLogoutFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    break;
            }
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        });
        bottomSheetFragment.setTitle(getResources().getString(R.string.kuknos_setting_title));
        bottomSheetFragment.show(getFragmentManager(), "SettingBottomSheet");
    }

    private void goToShowRecovery() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(KuknosAccountInfoFrag.class.getName());
        if (fragment == null) {
            fragment = KuknosAccountInfoFrag.newInstance();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
    }

    private void onTermsDownload() {
        viewModel.getTandCAgree().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null)
                    showDialogTermAndCondition(s);
            }
        });
    }

    private void showDialogTermAndCondition(String message) {
        if (getActivity() != null) {
            Dialog dialogTermsAndCondition = new Dialog(getActivity());
            dialogTermsAndCondition.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogTermsAndCondition.setContentView(R.layout.terms_condition_dialog);
            AppCompatTextView termsText = dialogTermsAndCondition.findViewById(R.id.termAndConditionTextView);
            termsText.setText(message);
            dialogTermsAndCondition.findViewById(R.id.okButton).setOnClickListener(v -> dialogTermsAndCondition.dismiss());
            dialogTermsAndCondition.show();
        }
    }

    private void onDataChanged() {
        viewModel.getKuknosWalletsM().observe(getViewLifecycleOwner(), accountResponse -> {
            if (accountResponse != null && accountResponse.getAssets().size() != 0) {
                WalletSpinnerArrayAdapter adapter = new WalletSpinnerArrayAdapter(getContext(), accountResponse.getAssets());
                walletSpinner.setAdapter(adapter);
                binding.fragKuknosPError.setVisibility(View.GONE);
                if (viewModel.getPosition() != 0)
                    walletSpinner.setSelection(viewModel.getPosition());
            } else {
                List<KuknosBalance.Balance> noItem = new ArrayList<>();
                KuknosBalance.Balance temp = new KuknosBalance.Balance();
                temp.setAssetCode("-1");
                noItem.add(temp);
                WalletSpinnerArrayAdapter adapter = new WalletSpinnerArrayAdapter(getContext(), noItem);
                walletSpinner.setAdapter(adapter);
            }
        });

        viewModel.getBAndCState().observe(getViewLifecycleOwner(), state -> {
            switch (state) {
                case 0:
                    viewModel.setBalance(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber("0.0") : "0.0");
                    viewModel.setCurrency(getResources().getString(R.string.kuknos_Currency));
                    break;
                case 1:
                    viewModel.setCurrency(getResources().getString(R.string.rial));
                    break;
            }
        });
    }

    private void onErrorObserver() {

        viewModel.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                if (errorM.getMessage().equals("0")) {
                    binding.fragKuknosPError.setVisibility(View.VISIBLE);
                    binding.fragKuknosPSend.setEnabled(false);
                    binding.fragKuknosPHistory.setEnabled(false);
                    binding.fragKuknosPTrading.setEnabled(false);
                }
                if (errorM.getMessage().equals("1")) {
                    Snackbar snackbar = Snackbar.make(binding.fragKuknosPContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                    snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                    snackbar.show();
                }
            }
        });

    }

    private void openPage() {
        viewModel.getOpenPage().observe(getViewLifecycleOwner(), pageID -> {
            if (pageID == -1)
                return;
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = null;
            switch (pageID) {
                case 0:
                    fragment = fragmentManager.findFragmentByTag(KuknosRecieveFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosRecieveFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    break;
                case 1:
                    fragment = fragmentManager.findFragmentByTag(KuknosSendFrag.class.getName());
                    if (fragment == null && !viewModel.convertToJSON(viewModel.getPosition()).equals("") /*&& viewModel.isPmnActive()*/) {
                        fragment = KuknosSendFrag.newInstance();
                        Bundle b = new Bundle();
                        b.putString("balanceClientInfo", viewModel.convertToJSON(viewModel.getPosition()));
                        fragment.setArguments(b);
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    } else {
                        Toast.makeText(getContext(), R.string.kuknos_send_token_error, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    fragment = fragmentManager.findFragmentByTag(KuknosWHistoryFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosWHistoryFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    break;
                case 3:
                    initialSettingBS();
                    viewModel.getOpenPage().setValue(-1);
                    return;
                case 4:
                    fragment = fragmentManager.findFragmentByTag(KuknosBuyPeymanFrag.class.getName());
                    if (fragment == null && !viewModel.convertToJSON(viewModel.getPosition()).equals("")) {
                        fragment = KuknosBuyPeymanFrag.newInstance();
                        Bundle b = new Bundle();
                        b.putString("balanceClientInfo", viewModel.convertToJSON(viewModel.getPosition()));
                        fragment.setArguments(b);
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    } else {
                        fragment = KuknosBuyPeymanFrag.newInstance();
                        Bundle b = new Bundle();
                        b.putString("balanceClientInfo", viewModel.convertToJSON(viewModel.getPosition()));
                        fragment.setArguments(b);
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        Toast.makeText(getContext(), R.string.unavalable, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 5:
                    fragment = fragmentManager.findFragmentByTag(KuknosTradePagerFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosTradePagerFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    break;
                case 6:
                    fragment = fragmentManager.findFragmentByTag(KuknosParsianTermsFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosParsianTermsFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    break;
            }
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            viewModel.getOpenPage().setValue(-1);
        });
    }

    private void onProgress() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosPProgressV.setVisibility(View.VISIBLE);
                binding.fragKuknosPRecieve.setEnabled(false);
                binding.fragKuknosPSend.setEnabled(false);
                binding.fragKuknosPHistory.setEnabled(false);
                binding.fragKuknosPBalanceHolder.setEnabled(false);
            } else {
                binding.fragKuknosPProgressV.setVisibility(View.GONE);
                binding.fragKuknosPRecieve.setEnabled(true);
                binding.fragKuknosPSend.setEnabled(true);
                binding.fragKuknosPHistory.setEnabled(true);
                binding.fragKuknosPBalanceHolder.setEnabled(true);
            }
        });
    }

    private void showDialog(int mode, int titleRes, int messageRes, int btnRes) {
        new MaterialDialog.Builder(getContext())
                .title(getResources().getString(titleRes))
                .positiveText(getResources().getString(btnRes))
                .content(getResources().getString(messageRes))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (mode == 1) {
                            writeSeedKey();
                        }
                    }
                })
                .show();
    }

    private void writeSeedKey() {
        PermissionHelper permissionHelper = new PermissionHelper(getActivity(), this);
        if (!permissionHelper.grantReadAndRightStoragePermission()) {
            return;
        }
        if (!isExternalStorageReadable()) {
            showDialog(2, R.string.kuknos_setting_copySFailTitle, R.string.kuknos_setting_copySFailReadM, R.string.kuknos_setting_copySFailBtn);
            return;
        }
        if (!isExternalStorageWritable()) {
            showDialog(3, R.string.kuknos_setting_copySFailTitle, R.string.kuknos_setting_copySFailWriteM, R.string.kuknos_setting_copySFailBtn);
            return;
        }
        File dir = getDownloadStorageDir(getString(R.string.kuknos_setting_Directory));
        File myExternalFile = new File(dir, getString(R.string.kuknos_setting_fileName));
        try {
            FileOutputStream fos = new FileOutputStream(myExternalFile);
            String temp = getString(R.string.kuknos_setting_fileContent) + viewModel.getPrivateKeyData();
            fos.write(temp.getBytes());
            fos.close();
        } catch (Exception e) {
            showDialog(4, R.string.kuknos_setting_copySFailTitle, R.string.kuknos_setting_copySFailWriteFile, R.string.kuknos_setting_copySFailBtn);
            return;
        }
        showDialog(5, R.string.kuknos_setting_copySSuccessTitle, R.string.kuknos_setting_copySSuccessMessage, R.string.kuknos_setting_copySFailBtn);
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private File getDownloadStorageDir(String fileName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.isDirectory())
            return file;

        if (!file.mkdirs()) {
            showDialog(6, R.string.kuknos_setting_copySFailTitle, R.string.kuknos_setting_copySFailDir, R.string.kuknos_setting_copySFailBtn);
        }
        return file;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean tmp = true;
        for (int grantResult : grantResults) {
            tmp = tmp && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (tmp) {
            writeSeedKey();
        } else {
            showDialog(2, R.string.kuknos_setting_copySFailTitle, R.string.kuknos_setting_copySFailReadM, R.string.kuknos_setting_copySFailBtn);
        }
    }
}
