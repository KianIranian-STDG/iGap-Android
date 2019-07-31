package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosPanelBinding;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosWalletsAccountM;
import net.iGap.kuknos.view.adapter.WalletSpinnerAdapter;
import net.iGap.kuknos.viewmodel.KuknosChangePassVM;
import net.iGap.kuknos.viewmodel.KuknosLogoutVM;
import net.iGap.kuknos.viewmodel.KuknosPanelVM;
import net.iGap.kuknos.viewmodel.KuknosRecieveVM;
import net.iGap.kuknos.viewmodel.KuknosSendVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

import java.util.ArrayList;
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
                if (position != (kuknosPanelVM.getKuknosWalletsM().getValue().getBalanceInfo().size()-1))
                    kuknosPanelVM.spinnerSelect(position);
                else {
                    Snackbar snackbar = Snackbar.make(binding.fragKuknosPContainer, getString(R.string.kuknos_develop), Snackbar.LENGTH_SHORT);
                    snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    walletSpinner.setSelection(kuknosPanelVM.getPosition());
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
    }

    private void initialSettingBS() {
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.kuknos_setting_changePin));
        items.add(getString(R.string.kuknos_setting_viewRecoveryP));
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
        kuknosPanelVM.getKuknosWalletsM().observe(getViewLifecycleOwner(), new Observer<KuknosWalletsAccountM>() {
            @Override
            public void onChanged(@Nullable KuknosWalletsAccountM kuknosWalletsAccountM) {
                Log.d("amini log", "onChanged: wallet " + kuknosPanelVM.getKuknosWalletsM().getValue().getBalanceInfo().size());
                if (kuknosWalletsAccountM.getBalanceInfo().size() != 0) {
                    WalletSpinnerAdapter adapter = new WalletSpinnerAdapter(getContext(),
                            kuknosWalletsAccountM.getBalanceInfo());
                    walletSpinner.setAdapter(adapter);
                }
            }
        });
    }

    private void onErrorObserver() {

        kuknosPanelVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
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
                            b.putParcelable("balanceClientInfo", kuknosPanelVM.getKuknosWalletsM().getValue().getBalanceInfo().get(kuknosPanelVM.getPosition()));
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
                        return;
                    case 4:
                        fragment = fragmentManager.findFragmentByTag(KuknosBuyPeymanFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosBuyPeymanFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                        break;
                    case 5:
                        /*fragment = fragmentManager.findFragmentByTag(KuknosBuyPeymanFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosBuyPeymanFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }*/
                        Snackbar snackbar = Snackbar.make(binding.fragKuknosPContainer, getString(R.string.kuknos_develop), Snackbar.LENGTH_SHORT);
                        snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
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
}
