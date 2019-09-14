package net.iGap.kuknos.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosRecoveryKeyBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosShowRecoveryKeyVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class KuknosShowRecoveryKeyFrag extends BaseFragment {

    private FragmentKuknosRecoveryKeyBinding binding;
    private KuknosShowRecoveryKeyVM kuknosShowRecoveryKeyVM;
    private HelperToolbar mHelperToolbar;

    public static KuknosShowRecoveryKeyFrag newInstance() {
        KuknosShowRecoveryKeyFrag kuknosRestoreFrag = new KuknosShowRecoveryKeyFrag();
        return kuknosRestoreFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosShowRecoveryKeyVM = ViewModelProviders.of(this).get(KuknosShowRecoveryKeyVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_recovery_key, container, false);
        binding.setViewmodel(kuknosShowRecoveryKeyVM);
        binding.setLifecycleOwner(this);
        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        kuknosShowRecoveryKeyVM.initMnemonic();

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

        LinearLayout toolbarLayout = binding.fragKuknosRKSToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        onErrorObserver();
        onNextObserver();
        progressState();
        getCachedData();
    }

    private void getCachedData() {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("KUKNOS_REGISTER", Context.MODE_PRIVATE);
        kuknosShowRecoveryKeyVM.setToken(sharedpreferences.getString("Token", ""));
        kuknosShowRecoveryKeyVM.setUsername(sharedpreferences.getString("Username", ""));
    }

    private void onErrorObserver() {
        kuknosShowRecoveryKeyVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    Snackbar snackbar = Snackbar.make(binding.fragKuknosRKSContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                    snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    binding.fragKuknosIdSubmit.setEnabled(false);
                }
            }
        });
    }

    private void onNextObserver() {
        kuknosShowRecoveryKeyVM.getNextPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean nextPage) {
                if (nextPage == true) {
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment;
                    if (kuknosShowRecoveryKeyVM.getPinCheck().get()) {
                        fragment = fragmentManager.findFragmentByTag(KuknosSetPassFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosSetPassFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                    } else {
                        fragment = fragmentManager.findFragmentByTag(KuknosPanelFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosPanelFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                    }
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                }
            }
        });
    }

    private void progressState() {
        kuknosShowRecoveryKeyVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_login_progress_str));
                    binding.fragKuknosIdSubmit.setEnabled(false);
                    binding.fragKuknosRKSProgressV.setVisibility(View.VISIBLE);
                } else {
                    binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_RecoverySK_Btn));
                    binding.fragKuknosIdSubmit.setEnabled(true);
                    binding.fragKuknosRKSProgressV.setVisibility(View.GONE);
                }
            }
        });
    }

}