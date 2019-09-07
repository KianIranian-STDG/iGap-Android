package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentKuknosPanelBinding;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosWalletsAccountM;
import net.iGap.kuknos.view.adapter.WalletSpinnerAdapter;
import net.iGap.kuknos.viewmodel.KuknosPanelVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

import org.stellar.sdk.responses.AccountResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KuknosPanelFrag extends BaseFragment {

    private FragmentKuknosPanelBinding binding;
    private KuknosPanelVM kuknosPanelVM;
    private HelperToolbar mHelperToolbar;
    private Spinner walletSpinner;

    public static KuknosPanelFrag newInstance() {
        KuknosPanelFrag kuknosLoginFrag = new KuknosPanelFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosPanelVM = ViewModelProviders.of(this).get(KuknosPanelVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_panel, container, false);
        binding.setViewmodel(kuknosPanelVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                        ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosPToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        walletSpinner = binding.fragKuknosPWalletSpinner;
        walletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != (kuknosPanelVM.getKuknosWalletsM().getValue().getBalances().length))
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

        Log.d("amini", "onClick: secret data  " + kuknosPanelVM.getPrivateKeyData());
    }

    private void initialSettingBS() {
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.kuknos_setting_changePin));
        items.add(getString(R.string.kuknos_setting_viewRecoveryP));
        items.add(getString(R.string.kuknos_setting_copySeedKey));
        items.add(getString(R.string.kuknos_setting_logout));

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment().setData(items, -1, new BottomSheetItemClickCallback() {
            @Override
            public void onClick(int position) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = fragmentManager.findFragmentByTag(KuknosChangePassFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosChangePassFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                        break;
                    case 1:
                        fragment = fragmentManager.findFragmentByTag(KuknosViewRecoveryEPFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosViewRecoveryEPFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                        break;
                    case 2:
                        showDialog(1, R.string.kuknos_setting_copySKeyTitel, R.string.kuknos_setting_copySKeyMessage, R.string.kuknos_setting_copySKeyBtn);
                        return;
                    case 3:
                        fragment = fragmentManager.findFragmentByTag(KuknosLogoutFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosLogoutFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                        break;
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
        bottomSheetFragment.setTitle(getResources().getString(R.string.kuknos_setting_title));
        bottomSheetFragment.show(getFragmentManager(), "SettingBottomSheet");
    }

    private void onDataChanged() {
        kuknosPanelVM.getKuknosWalletsM().observe(getViewLifecycleOwner(), new Observer<AccountResponse>() {
            @Override
            public void onChanged(@Nullable AccountResponse accountResponse) {
                if (accountResponse.getBalances().length != 0) {
                    WalletSpinnerAdapter adapter = new WalletSpinnerAdapter(getContext(),
                            Arrays.asList(accountResponse.getBalances()));
                    walletSpinner.setAdapter(adapter);
                    binding.fragKuknosPError.setVisibility(View.GONE);
                }
            }
        });
    }

    private void onErrorObserver() {

        kuknosPanelVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    if (errorM.getMessage().equals("0")){
                        binding.fragKuknosPError.setVisibility(View.VISIBLE);
                        binding.fragKuknosPSend.setEnabled(false);
                        binding.fragKuknosPHistory.setEnabled(false);
                        binding.fragKuknosPTrading.setEnabled(false);
                    }
                    if (errorM.getMessage().equals("1")){
                        Snackbar snackbar = Snackbar.make(binding.fragKuknosPContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                        snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }
                }
            }
        });

    }

    private void openPage() {
        kuknosPanelVM.getOpenPage().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer pageID) {
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
                        if (fragment == null) {
                            fragment = KuknosSendFrag.newInstance();
                            Bundle b = new Bundle();
                            b.putString("balanceClientInfo", kuknosPanelVM.convertToJSON(kuknosPanelVM.getPosition()));
                            fragment.setArguments(b);
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
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
                        fragment = fragmentManager.findFragmentByTag(KuknosTradePagerFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosTradePagerFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }/*
                        Snackbar snackbar = Snackbar.make(binding.fragKuknosPContainer, getString(R.string.kuknos_develop), Snackbar.LENGTH_SHORT);
                        snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();*/
                        break;
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                kuknosPanelVM.getOpenPage().setValue(-1);
            }
        });
    }

    private void onProgress() {
        kuknosPanelVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.fragKuknosPProgressV.setVisibility(View.VISIBLE);
                    binding.fragKuknosPRecieve.setEnabled(false);
                    binding.fragKuknosPSend.setEnabled(false);
                    binding.fragKuknosPHistory.setEnabled(false);
                    binding.fragKuknosPBalanceHolder.setEnabled(false);
                }
                else {
                    binding.fragKuknosPProgressV.setVisibility(View.GONE);
                    binding.fragKuknosPRecieve.setEnabled(true);
                    binding.fragKuknosPSend.setEnabled(true);
                    binding.fragKuknosPHistory.setEnabled(true);
                    binding.fragKuknosPBalanceHolder.setEnabled(true);
                }
            }
        });
    }

    private void showDialog(int mode, int titleRes, int messageRes, int btnRes) {
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        defaultRoundDialog.setTitle(getResources().getString(titleRes));
        defaultRoundDialog.setMessage(getResources().getString(messageRes));
        defaultRoundDialog.setPositiveButton(getResources().getString(btnRes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mode == 1) {
                    writeSeedKey();
                }
            }
        });
        defaultRoundDialog.show();
    }

    private void writeSeedKey() {
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
        }
        catch (Exception e) {
            showDialog(4, R.string.kuknos_setting_copySFailTitle, R.string.kuknos_setting_copySFailWriteFile, R.string.kuknos_setting_copySFailBtn);
            return;
        }
        showDialog(5, R.string.kuknos_setting_copySSuccessTitle, R.string.kuknos_setting_copySSuccessMessage, R.string.kuknos_setting_copySFailBtn);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getDownloadStorageDir(String fileName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.isDirectory())
            return file;

        if (!file.mkdirs()) {
            showDialog(6, R.string.kuknos_setting_copySFailTitle, R.string.kuknos_setting_copySFailDir, R.string.kuknos_setting_copySFailBtn);
        }
        return file;
    }
}
