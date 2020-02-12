package net.iGap.kuknos.view;

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

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosPanelBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.PermissionHelper;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.view.adapter.WalletSpinnerArrayAdapter;
import net.iGap.kuknos.viewmodel.KuknosPanelVM;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class KuknosPanelFrag extends BaseAPIViewFrag {

    private FragmentKuknosPanelBinding binding;
    private KuknosPanelVM kuknosPanelVM;
    private Spinner walletSpinner;

    public static KuknosPanelFrag newInstance() {
        return new KuknosPanelFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosPanelVM = ViewModelProviders.of(this).get(KuknosPanelVM.class);
        this.viewModel = kuknosPanelVM;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_panel, container, false);
        binding.setViewmodel(kuknosPanelVM);
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
                if (position != (kuknosPanelVM.getKuknosWalletsM().getValue().getAssets().size()))
                    kuknosPanelVM.spinnerSelect(position);
                else {
                    walletSpinner.setSelection(kuknosPanelVM.getPosition());
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment = fragmentManager.findFragmentByTag(KuknosAddAssetFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosAddAssetFrag.newInstance();
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
                    walletSpinner.setSelection(kuknosPanelVM.getPosition());*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        kuknosPanelVM.getDataFromServer();
        onErrorObserver();
        openPage();
        onDataChanged();
        onProgress();
        onTermsDownload();
    }

    @Override
    public void onResume() {
        kuknosPanelVM.getDataFromServer();
        super.onResume();
    }

    @Override
    public boolean onBackPressed() {
//        popBackStackFragment();
        ((ActivityMain) getActivity()).removeAllFragmentFromMain();
        return true;
    }

    private void initialSettingBS() {
        List<String> items = new ArrayList<>();
//        items.add(getString(R.string.kuknos_setting_changePin));
        items.add(getString(R.string.kuknos_setting_viewRecoveryP));
        items.add(getString(R.string.kuknos_setting_copySeedKey));
        items.add(getString(R.string.kuknos_setting_sepid));
        items.add(getString(R.string.kuknos_setting_logout));

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment().setData(items, -1, position -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = null;
            switch (position) {
                /*case 0:
                    fragment = fragmentManager.findFragmentByTag(KuknosChangePassFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosChangePassFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    break;*/
                case 0:
                    if (kuknosPanelVM.isPinSet()) {
                        fragment = fragmentManager.findFragmentByTag(KuknosViewRecoveryEPFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosViewRecoveryEPFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                    } else {
                        fragment = fragmentManager.findFragmentByTag(KuknosShowRecoveryKeySFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosShowRecoveryKeySFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                    }
                    break;
                case 1:
                    showDialog(1, R.string.kuknos_setting_copySKeyTitel, R.string.kuknos_setting_copySKeyMessage, R.string.kuknos_setting_copySKeyBtn);
                    return;
                case 2:
                    HelperUrl.openWebBrowser(getContext(), "https://www.kuknos.org/wp/");
//                    kuknosPanelVM.getTermsAndCond();
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

    private void onTermsDownload() {
        kuknosPanelVM.getTandCAgree().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        kuknosPanelVM.getKuknosWalletsM().observe(getViewLifecycleOwner(), accountResponse -> {
            if (accountResponse.getAssets().size() != 0) {
                WalletSpinnerArrayAdapter adapter = new WalletSpinnerArrayAdapter(getContext(), accountResponse.getAssets());
                walletSpinner.setAdapter(adapter);
                binding.fragKuknosPError.setVisibility(View.GONE);
            }
        });
    }

    private void onErrorObserver() {

        kuknosPanelVM.getError().observe(getViewLifecycleOwner(), errorM -> {
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
        kuknosPanelVM.getOpenPage().observe(getViewLifecycleOwner(), pageID -> {
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
                    if (fragment == null && !kuknosPanelVM.convertToJSON(kuknosPanelVM.getPosition()).equals("")) {
                        fragment = KuknosSendFrag.newInstance();
                        Bundle b = new Bundle();
                        b.putString("balanceClientInfo", kuknosPanelVM.convertToJSON(kuknosPanelVM.getPosition()));
                        fragment.setArguments(b);
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    } else {
                        Toast.makeText(getContext(), "Can NOT send this token.", Toast.LENGTH_SHORT).show();
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
                    kuknosPanelVM.getOpenPage().setValue(-1);
                    return;
                case 4:
                    fragment = fragmentManager.findFragmentByTag(KuknosBuyPeymanFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosBuyPeymanFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    break;
                case 5:
                    Log.d("amini", "openPage: open trade");
                    fragment = fragmentManager.findFragmentByTag(KuknosTradePagerFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosTradePagerFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    break;
            }
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            kuknosPanelVM.getOpenPage().setValue(-1);
        });
    }

    private void onProgress() {
        kuknosPanelVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
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
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        defaultRoundDialog.setTitle(getResources().getString(titleRes));
        defaultRoundDialog.setMessage(getResources().getString(messageRes));
        defaultRoundDialog.setPositiveButton(getResources().getString(btnRes), (dialog, id) -> {
            if (mode == 1) {
                writeSeedKey();
            }
        });
        defaultRoundDialog.show();
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
            String temp = getString(R.string.kuknos_setting_fileContent) + kuknosPanelVM.getPrivateKeyData();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
