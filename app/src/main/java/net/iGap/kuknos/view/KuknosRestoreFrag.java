package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
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
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosRestoreBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.generated.callback.OnCheckedChangeListener;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosRestoreVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class KuknosRestoreFrag extends BaseFragment {

    private FragmentKuknosRestoreBinding binding;
    private KuknosRestoreVM kuknosRestoreVM;

    public static KuknosRestoreFrag newInstance() {
        KuknosRestoreFrag kuknosRestoreFrag = new KuknosRestoreFrag();
        return kuknosRestoreFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosRestoreVM = ViewModelProviders.of(this).get(KuknosRestoreVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_restore, container, false);
        binding.setViewmodel(kuknosRestoreVM);
        binding.setLifecycleOwner(this);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosRToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());
        binding.fragKuknosIdPINCheck.setChecked(false);

        onErrorObserver();
        onNextObserver();
        progressState();
    }

    private void onPINCheck() {
        kuknosRestoreVM.getPinCheck().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                binding.fragKuknosIdPINCheck.setText(getResources().getString(R.string.kuknos_Restore_checkBtn));
            else
                binding.fragKuknosIdPINCheck.setText(getResources().getString(R.string.kuknos_Restore_Btn));
        });
    }

    private void onErrorObserver() {
        kuknosRestoreVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    if (errorM.getMessage().equals("0")) {
                        binding.fragKuknosRkeysET.setError("" + getString(errorM.getResID()));
                        binding.fragKuknosRkeysET.requestFocus();
                    }
                    else if (errorM.getMessage().equals("1")) {
                        Snackbar snackbar = Snackbar.make(binding.fragKuknosRContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
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

    private void onNextObserver() {
        kuknosRestoreVM.getNextPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean nextPage) {
                if (nextPage == true) {
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment;
                    if (binding.fragKuknosIdPINCheck.isChecked()) {
                        fragment = fragmentManager.findFragmentByTag(KuknosRestorePassFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosRestorePassFrag.newInstance();
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("key_phrase", kuknosRestoreVM.getKeys().get());
                        fragment.setArguments(bundle);
                    }
                    else {
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
        kuknosRestoreVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_login_progress_str));
                    binding.fragKuknosIdSubmit.setEnabled(false);
                    binding.fragKuknosRkeysET.setEnabled(false);
                    binding.fragKuknosRProgressV.setVisibility(View.VISIBLE);
                }
                else {
                    binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_Restore_Btn));
                    binding.fragKuknosIdSubmit.setEnabled(true);
                    binding.fragKuknosRkeysET.setEnabled(true);
                    binding.fragKuknosRProgressV.setVisibility(View.GONE);
                }
            }
        });
    }
}
